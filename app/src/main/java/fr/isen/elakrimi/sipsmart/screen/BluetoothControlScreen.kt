package fr.isen.elakrimi.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BluetoothControlScreen(
    name: String,
    address: String,
    rssi: Int,
    connectionStatus: String,
    isConnected: Boolean,
    liquidValue: Int,
    tmpValue: Int,
    isSubscribed: Boolean,
    onBack: () -> Unit,
    onConnectClick: () -> Unit,
    onToggleSubscription: (Boolean) -> Unit
) {
    // Etats pour garder les anciennes valeurs de CO2 et PM
    val liquidHistory = rememberHistoryState()
    val tmpHistory = rememberHistoryState()

    // Mettre à jour l'historique des valeurs
    if (isConnected) {
        liquidHistory.add(liquidValue)
        tmpHistory.add(tmpValue)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SIPSMART") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Bloc de connexion (visible uniquement si non connecté)
            if (!isConnected) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Nom : $name", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Adresse : $address", fontSize = 14.sp, color = Color.Gray)
                        Text("RSSI : $rssi dBm", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Statut : $connectionStatus", fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            enabled = !isConnected,
                            onClick = onConnectClick,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Se connecter")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSubscribed,
                                onCheckedChange = onToggleSubscription
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Recevoir notifications")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Bloc affichage des données CO2 et PM (visible uniquement si connecté)
            if (isConnected) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD1C4E9)),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).align(Alignment.CenterHorizontally),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Température",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFFF6F2F2)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "$tmpValue °C",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF98E8E)),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp).align(Alignment.CenterHorizontally),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Niveau de liquide",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFFFFFFFF)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "$liquidValue %",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

            }
        }
    }
}

// Fonction pour mémoriser l'historique des valeurs
@Composable
fun rememberHistoryState(): HistoryState {
    val state = remember { HistoryState() }
    return state
}

// Classe pour gérer l'historique des valeurs
class HistoryState {
    private val _values = mutableListOf<Int>()
    val values: List<Int> get() = _values

    fun add(value: Int) {
        _values.add(value)
        if (_values.size > 10) { // Garder seulement les 10 dernières valeurs
            _values.removeAt(0)
        }
    }
}

