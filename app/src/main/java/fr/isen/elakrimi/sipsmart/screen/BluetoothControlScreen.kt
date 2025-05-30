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
import androidx.compose.foundation.shape.RoundedCornerShape



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

            // Carte de connexion (si non connecté)
            if (!isConnected) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF4EDF7)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Nom : $name",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF5E548E)
                        )
                        Text(
                            "Adresse : $address",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            "RSSI : $rssi dBm",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Statut : $connectionStatus",
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF9A79B8)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onConnectClick,
                            enabled = !isConnected,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFDAA5E4),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Se connecter")
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSubscribed,
                                onCheckedChange = onToggleSubscription,
                                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF9A79B8))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Recevoir notifications")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Données affichées quand connecté
            if (isConnected) {

                // Température
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE9DEF9)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Température",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF5E548E)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "$tmpValue °C",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF3C3C3C)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Niveau de liquide
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFD8D8)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Niveau de liquide",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFFB04B4B)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "$liquidValue %",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF3C3C3C)
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

