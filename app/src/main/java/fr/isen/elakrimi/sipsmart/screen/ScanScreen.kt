package fr.isen.elakrimi.sipsmart.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.isen.elakrimi.sipsmart.FirebaseAuthViewModel

data class BLEDevice(val name: String, val address: String, val rssi: Int)

@Composable
fun ScanScreen(
    modifier: Modifier = Modifier,
    devices: List<BLEDevice>,
    isScanning: Boolean,
    remainingTime: Int,
    onStartScan: () -> Unit,
    onStopScan: () -> Unit,
    onBack: () -> Unit,
    onDeviceClick: (BLEDevice) -> Unit,
    onConnectClick: () -> Unit,
    viewModel: FirebaseAuthViewModel,
    onLogout: () -> Unit,
    navController: NavController
) {

    val backgroundColor = Color(0xFFF98E8E)
    val whiteColor = Color.White
    val authState = viewModel.authState.collectAsState()
    Text("AuthState: ${authState.value}")



    val userName = authState.value.let { state ->
        when(state) {
            is FirebaseAuthViewModel.AuthState.Success -> state.user?.displayName ?: "Utilisateur"
            else -> "Utilisateur"
        }
    }

    println("User name in ScanScreen: $userName")

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                authState.value.let { state ->
                    when (state) {
                        is FirebaseAuthViewModel.AuthState.Success -> {
                            Text(
                                text = "Bienvenue, ${state.user?.displayName ?: "Utilisateur"} !",
                                style = MaterialTheme.typography.headlineMedium,
                                color = whiteColor
                            )
                        }
                        else -> {
                            Text(
                                "Utilisateur non connecté",
                                style = MaterialTheme.typography.headlineMedium,
                                color = whiteColor
                            )
                        }
                    }
                }

                IconButton(
                    onClick = {
                        viewModel.signOut()
                        onLogout()
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Logout,
                        contentDescription = "Déconnexion",
                        tint = whiteColor
                    )
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            // Contenu principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(60.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { if (isScanning) onStopScan() else onStartScan() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF98E8E),
                        contentColor = Color.White
                    )
                ) {
                    Text(if (isScanning) "Arrêter le scan" else "Démarrer le scan")
                }


                Spacer(modifier = Modifier.height(30.dp))

                Text(text = if (isScanning) "Scan en cours... ($remainingTime s restantes)" else "Scan arrêté")

                Spacer(modifier = Modifier.height(20.dp))



                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(devices) { device ->
                        BLEDeviceItem(device = device, onClick = { onDeviceClick(device) })
                    }
                }
            }
        }
    }
}

@Composable
fun BLEDeviceItem(device: BLEDevice, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${device.name} (${device.rssi} dB)",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = device.address,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}
