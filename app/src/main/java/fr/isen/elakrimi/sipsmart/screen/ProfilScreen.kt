package fr.isen.elakrimi.sipsmart.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import fr.isen.elakrimi.sipsmart.FirebaseAuthViewModel

@Composable
fun ProfilScreen(
    viewModel: FirebaseAuthViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val authState = viewModel.authState.collectAsState()
    val measurements = viewModel.measurementHistory.collectAsState()

    val backgroundColor = Color(0xFFF98E8E)
    val whiteColor = Color.White

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchLastFiveMeasurements()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                authState.value.let { state ->
                    Text(
                        text = when (state) {
                            is FirebaseAuthViewModel.AuthState.Success -> "Bienvenue, ${state.user?.displayName ?: "Utilisateur"} !"
                            else -> "Utilisateur non connecté"
                        },
                        style = MaterialTheme.typography.headlineMedium,
                        color = whiteColor
                    )
                }
                IconButton(onClick = {
                    viewModel.signOut()
                    onLogout()
                }) {
                    Icon(Icons.Default.Logout, contentDescription = "Déconnexion", tint = whiteColor)
                }
            }

            // Données mesurées
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    "Historique des 5 dernières mesures",
                    style = MaterialTheme.typography.titleLarge
                )

                if (measurements.value.isEmpty()) {
                    Text("Aucune donnée à afficher.", color = Color.Gray)
                } else {
                    measurements.value.forEachIndexed { index, (temp, level) ->
                        val displayLevel = (level.coerceIn(0f, 1f) * 100).toInt()
                        Text("Mesure ${index + 1} : Température = ${temp}°C | Liquide = ${displayLevel}%")
                    }
                }
            }
        }
    }
}
