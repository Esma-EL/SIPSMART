package fr.isen.elakrimi.sipsmart.activity

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import fr.isen.elakrimi.screen.BluetoothControlScreen
import fr.isen.elakrimi.sipsmart.ui.theme.SIPSMARTTheme
import java.util.UUID
import java.util.Queue
import java.util.LinkedList
import fr.isen.elakrimi.sipsmart.FirebaseAuthViewModel
import androidx.activity.viewModels
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat




class BluetoothControlActivity: ComponentActivity() {

    private var gatt: BluetoothGatt? = null
    private var liquidChar: BluetoothGattCharacteristic? = null
    private var tmpChar: BluetoothGattCharacteristic? = null

    private val liquidValue = mutableStateOf(0)
    private val tmpValue = mutableStateOf(0)
    private val connectionState = mutableStateOf("Appuyez sur le bouton pour vous connecter")
    private val isSubscribed = mutableStateOf(false)
    private var skipNextCO2Notification = false
    private var skipNextPMNotification = false
    private var latestTemperature: Float? = null
    private var latestLiquidLevel: Float? = null
    private val viewModel: FirebaseAuthViewModel by viewModels()




    private val CCCD_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    private val descriptorQueue: Queue<BluetoothGattDescriptor> = LinkedList()

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
        }

        super.onCreate(savedInstanceState)

        val name = intent.getStringExtra("name") ?: "Appareil inconnu"
        val address = intent.getStringExtra("address") ?: "N/A"
        val rssi = intent.getIntExtra("rssi", 0)

        setContent {
            SIPSMARTTheme {
                BluetoothControlScreen(
                    name = name,
                    address = address,
                    rssi = rssi,
                    connectionStatus = connectionState.value,
                    isConnected = connectionState.value.startsWith("✅"),
                    liquidValue = liquidValue.value,
                    tmpValue = tmpValue.value,
                    isSubscribed = isSubscribed.value,
                    onBack = { finish() },
                    onConnectClick = { connectToDevice(address, name) },
                    onToggleSubscription = { toggleNotifications(it) }
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun connectToDevice(address: String, name: String) {
        connectionState.value = "Connexion BLE en cours..."
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        val device = bluetoothAdapter.getRemoteDevice(address)

        gatt = device.connectGatt(this, false, object : BluetoothGattCallback() {

            override fun onConnectionStateChange(gattParam: BluetoothGatt, status: Int, newState: Int) {
                runOnUiThread {
                    if (newState == BluetoothGatt.STATE_CONNECTED) {
                        connectionState.value = "✅ Connecté à $name"
                        gattParam.discoverServices()
                    } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                        connectionState.value = "❌ Déconnecté"
                    }
                }
            }

            override fun onServicesDiscovered(gattParam: BluetoothGatt, status: Int) {
                gattParam.services.forEach { service ->
                    Log.d("BLE", "Service: ${service.uuid}")
                    service.characteristics.forEach { char ->
                        Log.d("BLE", " └ Char: ${char.uuid}")
                    }
                }

                val serviceUUID = UUID.fromString("0000feed-cc7a-482a-984a-7f2ed5b3e58f")
                val tmpCharUUID = UUID.fromString("00001234-8e22-4541-9d4c-21edae82ed19")
                val liquidCharUUID = UUID.fromString("0000ABCD-8e22-4541-9d4c-21edae82ed19")

                val service = gattParam.services.find { it.uuid == serviceUUID }
                tmpChar = service?.getCharacteristic(tmpCharUUID)
                liquidChar = service?.getCharacteristic(liquidCharUUID)

                // Active automatiquement les notifications dès que les caractéristiques sont récupérées
                runOnUiThread {
                    toggleNotifications(true)
                }
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                val raw = characteristic.value
                val hex = raw.joinToString(" ") { String.format("%02X", it) }
                Log.d("BLE", "📥 Notification reçue (${raw.size} octets) : $hex")

                when (characteristic.uuid) {

                    tmpChar?.uuid -> {
                        val tempRaw = (raw[0].toInt() shl 8) or (raw[1].toInt() and 0xFF)
                        val tempSigned = if (tempRaw and 0x8000 != 0) tempRaw - 0x10000 else tempRaw
                        val temperature = tempSigned / 100.0
                        val tempInt = temperature.toInt()

                        runOnUiThread {
                            val temperatureToSave = tempInt.toFloat()
                            tmpValue.value = tempInt

                            viewModel.updateTemperature(tempInt)
                            viewModel.saveLastTemperatureToFirebase()

                            latestTemperature = temperatureToSave
                            trySaveMeasurement() // ✅ tentative d’enregistrement groupé température + liquide
                        }
                    }

                    liquidChar?.uuid -> {
                        val liquid = (raw[0].toInt() and 0xFF)

                        runOnUiThread {
                            val convertedLevel = when (liquid) {
                                in 75..85 -> 1f
                                in 15..25 -> 0.2f
                                else -> (liquid / 100f).coerceIn(0f, 1f)
                            }

                            liquidValue.value = liquid

                            viewModel.updateLiquidLevel(convertedLevel)
                            viewModel.saveLiquidLevelToFirebase(liquid)

                            latestLiquidLevel = convertedLevel

                            if (liquid == 20) {
                                triggerLowHydrationNotification()
                            }

                            trySaveMeasurement() // ✅ pareil : n’enregistre que si les deux valeurs sont prêtes
                        }

                        Log.d("BLE", "liquid = $liquid %")
                    }
                }
            }



            override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d("BLE", "✅ Descriptor écrit pour ${descriptor.characteristic.uuid}")
                } else {
                    Log.e("BLE", "❌ Échec d’écriture du descriptor pour ${descriptor.characteristic.uuid} - status=$status")
                }
                descriptorQueue.poll()
                writeNextDescriptor()
            }
        })
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
        gatt?.close()
        viewModel.fetchLastFiveMeasurements()
    }

    @SuppressLint("MissingPermission")
    private fun toggleNotifications(enable: Boolean) {
        val characteristics = listOfNotNull(liquidChar, tmpChar)

        characteristics.forEach { char ->
            val success = gatt?.setCharacteristicNotification(char, enable) ?: false
            if (!success) {
                Log.e("BLE", "❌ Impossible de modifier la notification pour ${char.uuid}")
                return@forEach
            }

            char.descriptors.forEach {
                Log.d("BLE", "🧩 ${char.uuid} → descriptor: ${it.uuid}")
            }

            Log.d("BLE", "🔍 Propriétés de ${char.uuid}: ${char.properties}")

            val descriptor = char.getDescriptor(CCCD_UUID)
            if (descriptor == null) {
                Log.w("BLE", "⚠️ Descriptor CCCD non trouvé pour ${char.uuid}")
                return@forEach
            }

            descriptor.value = if (enable) {
                BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            } else {
                BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
            }

            descriptorQueue.add(descriptor)
        }

        writeNextDescriptor()

        runOnUiThread {
            isSubscribed.value = enable
        }

        if (enable) {
            skipNextCO2Notification = true
            skipNextPMNotification = true
        }
    }

    @SuppressLint("MissingPermission")
    private fun writeNextDescriptor() {
        if (descriptorQueue.isNotEmpty()) {
            val descriptor = descriptorQueue.peek()
            val success = gatt?.writeDescriptor(descriptor) ?: false
            if (!success) {
                Log.e("BLE", "❌ Échec de l'écriture du descriptor pour ${descriptor.characteristic.uuid}")
                descriptorQueue.poll() // on enlève pour passer au suivant
                writeNextDescriptor()
            }
        }



    }
    private fun triggerLowHydrationNotification() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "hydration_alerts"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alerte Hydratation",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications pour rappeler de remplir la gourde."
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("⚠️ Hydratation basse")
            .setContentText("Remplissez votre gourde, le niveau est à 20%")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(1001, notification)
    }

    private fun trySaveMeasurement() {
        if (latestTemperature != null && latestLiquidLevel != null) {
            viewModel.saveMeasurementToHistory(latestTemperature, latestLiquidLevel)
            latestTemperature = null
            latestLiquidLevel = null
            Log.d("BLE", "✅ Enregistrement groupé effectué")
        }
    }



}


