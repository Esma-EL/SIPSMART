package fr.isen.elakrimi.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.text.font.FontWeight

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
    onToggleSubscription: (Boolean) -> Unit,
    onLogout: (() -> Unit)? = null, // optionnel, si tu veux gérer déconnexion
    viewModel: Any? = null // si tu veux appeler signOut sur un viewModel (adapter le type)
) {
    // Couleurs pour la top bar
    val backgroundColor = Color(0xFFF98E8E)
    val whiteColor = Color.White

    // Etats pour garder l'historique des valeurs
    val liquidHistory = rememberHistoryState()
    val tmpHistory = rememberHistoryState()

    if (isConnected) {
        liquidHistory.add(liquidValue)
        tmpHistory.add(tmpValue)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar personnalisée
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

                // Bouton logout optionnel, si viewModel et onLogout fournis
                if (viewModel != null && onLogout != null) {
                    IconButton(
                        onClick = {

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
            }

            // Le contenu principal avec padding
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!isConnected) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 80.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF98E8E)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                        shape = RoundedCornerShape(30.dp)
                    ) {
                        Column(modifier = Modifier.padding(15.dp)) {
                            Text(
                                text = "Nom : $name",
                                fontWeight = FontWeight.Bold,
                                fontSize = 25.sp,
                                color = Color(0xFFFFFFFF)
                            )
                            Spacer(modifier = Modifier.height(40.dp))
                            Text(
                                text = "Adresse : $address",
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                            Text(
                                text = "RSSI : $rssi dBm",
                                fontSize = 18.sp,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(50.dp))
                            Button(
                                onClick = onConnectClick,
                                enabled = !isConnected,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(60.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFFFFFF),
                                    contentColor = Color.Black
                                )
                            ) {
                                Text("Se connecter")
                            }
                            Spacer(modifier = Modifier.height(50.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isSubscribed,
                                    onCheckedChange = onToggleSubscription,
                                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFFF98E8E))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Notifications")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }

                if (isConnected) {
                    // Température
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF98E8E)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Température",
                                fontWeight = FontWeight.Bold,
                                fontSize = 25.sp,
                                color = Color(0xFFFFFFFF)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "$tmpValue °C",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF3C3C3C)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(1.dp))

                    // Niveau de liquide
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 30.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF98E8E)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Niveau de liquide",
                                fontWeight = FontWeight.Bold,
                                fontSize = 25.sp,
                                color = Color(0xFFFFFFFF)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "$liquidValue %",
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
}

// Fonction pour mémoriser l'historique des valeurs
@Composable
fun rememberHistoryState(): HistoryState {
    return remember { HistoryState() }
}

// Classe pour gérer l'historique des valeurs
class HistoryState {
    private val _values = mutableListOf<Int>()
    val values: List<Int> get() = _values

    fun add(value: Int) {
        _values.add(value)
        if (_values.size > 10) {
            _values.removeAt(0)
        }
    }
}
