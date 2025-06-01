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
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import fr.isen.elakrimi.sipsmart.FirebaseAuthViewModel
import androidx.compose.material.icons.filled.ArrowBack
import fr.isen.elakrimi.sipsmart.R
import androidx.compose.foundation.shape.RoundedCornerShape


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

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Top bar rose avec texte blanc et bouton logout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onBack() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Retour",
                        tint = whiteColor
                    )
                }



            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contenu principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Nouveau bouton image-only
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF98E8E))
                        .clickable {
                            if (isScanning) onStopScan() else onStartScan()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val iconRes = if (isScanning) R.drawable.ic_stop else R.drawable.ic_start
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = if (isScanning) "Stop Scan" else "Start Scan",
                        modifier = Modifier.size(199.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

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
fun calculateSignalStrength(rssi: Int): Float {
    return when {
        rssi >= -50 -> 1f
        rssi <= -100 -> 0f
        else -> (rssi + 100) / 50f
    }
}

@Composable
fun BLEDeviceItem(device: BLEDevice, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth() // étire sur toute la largeur dispo
            .padding(horizontal = 8.dp, vertical = 6.dp) // marge légère
            .height(70.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF98E8E))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp), // espace à l’intérieur
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = device.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
                Text(
                    text = device.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }

            Text(
                text = "${device.rssi} dB",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }
}



