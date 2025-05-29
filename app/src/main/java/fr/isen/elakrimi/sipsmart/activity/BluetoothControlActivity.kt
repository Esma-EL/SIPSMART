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



class BluetoothControlActivity: ComponentActivity() {

    private var gatt: BluetoothGatt? = null
    private var liquidChar: BluetoothGattCharacteristic? = null
    private var tmpChar: BluetoothGattCharacteristic? = null
    private var switchChar: BluetoothGattCharacteristic? = null
    private var switchChar2: BluetoothGattCharacteristic? = null

    private val liquidValue = mutableStateOf(0)
    private val tmpValue = mutableStateOf(0)
    private val connectionState = mutableStateOf("Appuyez sur le bouton pour vous connecter")
    private val isSubscribed = mutableStateOf(false)
    private var skipNextCO2Notification = false
    private var skipNextPMNotification = false

    private val CCCD_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val name = intent.getStringExtra("name") ?: "Appareil inconnu"
        val address = intent.getStringExtra("address") ?: "N/A"
        val rssi = intent.getIntExtra("rssi", 0)

        setContent {
            SIPSMARTTheme  {
                BluetoothControlScreen(
                    name = name,
                    address = address,
                    rssi = rssi,
                    connectionStatus = connectionState.value,
                    isConnected = connectionState.value.startsWith("✅"),
                    liquidValue = liquidValue.value, // <-- Ajout
                    tmpValue = tmpValue.value,
                    isSubscribed = isSubscribed.value,
                    onBack = { finish() },
                    onConnectClick = { connectToDevice(address, name) },
                    onToggleSubscription = { toggleNotifications(it) } // <-- Ajout
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

//                val co2ServiceUUID = UUID.fromString("00001234-0000-1000-8000-00805f9b34fb")
//                val co2CharUUID = UUID.fromString("00005678-0000-1000-8000-00805f9b34fb")
                //             val pmCharUUID = UUID.fromString("00008765-0000-1000-8000-00805f9b34fb")

                val serviceUUID = UUID.fromString("0000feed-cc7a-482a-984a-7f2ed5b3e58f")
                val switchCharUUID = UUID.fromString("0000ABCD-8e22-4541-9d4c-21edae82ed19")
                val switchCharUUID2 = UUID.fromString("00001234-8e22-4541-9d4c-21edae82ed19")
                val service = gattParam.services.find { it.uuid == serviceUUID }
                switchChar = service?.getCharacteristic(switchCharUUID)
                switchChar2 = service?.getCharacteristic(switchCharUUID2)
                Log.d("DEBUG", "switchChar2 = $switchChar2")

                //val service = gattParam.services.find { it.uuid == co2ServiceUUID }
                //co2Char = service?.getCharacteristic(co2CharUUID)
                //pmChar = service?.getCharacteristic(pmCharUUID)

                Log.d("BLE", "liquid char = $serviceUUID")
                Log.d("BLE", "tmp char = $serviceUUID")


                // Active automatiquement les notifications dès que les caractéristiques sont récupérées
                runOnUiThread {
                    toggleNotifications(true)
                }
            }

            override fun onCharacteristicChanged(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic
            ) {
                val raw = characteristic.value
                val uuid = characteristic.uuid
                Log.d("BLE", "Notification reçue pour UUID=$uuid, taille=${raw.size}")


                // Niveau de liquide (anciennement CO2)
                if (uuid == UUID.fromString("0000ABCD-8e22-4541-9d4c-21edae82ed19")) {
                    val liquidLevel = raw[0].toInt() and 0xFF

                    runOnUiThread {
                        liquidValue.value = liquidLevel
                    }

                    Log.d("BLE", "🧪 Niveau de liquide = $liquidLevel %")
                }

                // Température (anciennement PM)
                // Température (anciennement PM)
                else if (uuid == UUID.fromString("00001234-8e22-4541-9d4c-21edae82ed19")) {
                    if (skipNextPMNotification) {
                        skipNextPMNotification = false
                        Log.d("BLE", "🔕 Première notif PM ignorée")
                        return
                    }
                    Log.d("TEMP", "✅ Notification température reçue ! UUID=$uuid")

                    val hex = raw.joinToString(" ") { "%02X".format(it) }
                    Log.d("TEMP", "📦 Donnée brute température : $hex (taille=${raw.size})")

                    if (raw.size >= 2) {
                        val tempRaw = ((raw[0].toInt() and 0xFF) shl 8) or
                                (raw[1].toInt() and 0xFF)
                        val temperature = tempRaw / 100f

                        runOnUiThread {
                            tmpValue.value = temperature.toInt() // ou .value = temperature si tu préfères un float
                        }

                        Log.d("TEMP", "🌡️ Température = %.2f °C".format(temperature))
                    } else {
                        Log.w("TEMP", "⚠️ Donnée température trop courte !")
                    }
                }

                // Log des données brutes
                val hex = raw.joinToString(" ") { String.format("%02X", it) }
                Log.d("BLE", "📥 Notification reçue (${raw.size} octets) : $hex")
            }


        })
    }

    @SuppressLint("MissingPermission")
    private fun toggleNotifications(enable: Boolean) {
        val characteristics = listOfNotNull(switchChar, switchChar2)

        characteristics.forEach { char ->
            val success = gatt?.setCharacteristicNotification(char, enable) ?: false
            if (!success) {
                Log.e("BLE", "❌ Impossible de modifier la notification pour ${char.uuid}")
                return@forEach
            }

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

            val writeSuccess = gatt?.writeDescriptor(descriptor) ?: false
            if (!writeSuccess) {
                Log.e("BLE", "❌ Échec de l'écriture du descriptor pour ${char.uuid}")
            } else {
                Log.d("BLE", "🔔 Notifications ${if (enable) "activées" else "désactivées"} pour ${char.uuid}")
            }
        }

        runOnUiThread {
            isSubscribed.value = enable
        }

        if (enable) {
            skipNextCO2Notification = true
            skipNextPMNotification = true
        }
    }



    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
        gatt?.close()
    }
}
