package fr.isen.elakrimi.sipsmart.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
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
                Text("Historique des 5 dernières mesures", style = MaterialTheme.typography.titleLarge)

                if (measurements.value.isEmpty()) {
                    Text("Aucune donnée à afficher.", color = Color.Gray)
                } else {
                    val tempValues = measurements.value.map { it.first.toFloat().coerceIn(0f, 30f) }
                    val levelValues = measurements.value.map { (it.second.coerceIn(0f, 1f) * 100f).coerceIn(0f, 80f) }
                    val abscisses = listOf("M1", "M2", "M3", "M4", "M5")

                    // Température
                    Text("Températures récentes (°C)", style = MaterialTheme.typography.titleMedium)
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        val stepX = size.width / 6
                        val yLabels = listOf(0, 10, 20, 30)

                        // Axe Y
                        drawLine(Color.Black, Offset(60f, 0f), Offset(60f, size.height), strokeWidth = 4f)
                        drawLine(Color.Black, Offset(60f, size.height), Offset(size.width, size.height), strokeWidth = 4f)

                        yLabels.forEach { label ->
                            val y = size.height - (label / 30f) * size.height
                            drawLine(Color.LightGray, Offset(60f, y), Offset(size.width, y))
                            drawIntoCanvas {
                                it.nativeCanvas.drawText(
                                    "$label°C", 10f, y,
                                    android.graphics.Paint().apply {
                                        color = android.graphics.Color.BLACK
                                        textSize = 32f
                                    }
                                )
                            }
                        }

                        abscisses.forEachIndexed { i, label ->
                            val x = 60f + (i + 1) * stepX
                            drawIntoCanvas {
                                it.nativeCanvas.drawText(
                                    label, x - 20f, size.height - 10f,
                                    android.graphics.Paint().apply {
                                        color = android.graphics.Color.BLACK
                                        textSize = 30f
                                    }
                                )
                            }
                        }

                        val points = tempValues.mapIndexed { i, temp ->
                            Offset(60f + (i + 1) * stepX, size.height - (temp / 30f) * size.height)
                        }

                        points.zipWithNext().forEach { (p1, p2) ->
                            drawLine(Color.Red, p1, p2, strokeWidth = 4f)
                        }

                        points.forEach {
                            drawCircle(Color.Red, radius = 8f, center = it)
                        }
                    }

                    // Hydratation
                    Text("Hydratation récente (%)", style = MaterialTheme.typography.titleMedium)
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        val stepX = size.width / 6
                        val yLabels = listOf(0, 20, 80)

                        // Axe Y
                        drawLine(Color.Black, Offset(60f, 0f), Offset(60f, size.height), strokeWidth = 4f)
                        drawLine(Color.Black, Offset(60f, size.height), Offset(size.width, size.height), strokeWidth = 4f)

                        yLabels.forEach { label ->
                            val y = size.height - (label / 80f) * size.height
                            drawLine(Color.LightGray, Offset(60f, y), Offset(size.width, y))
                            drawIntoCanvas {
                                it.nativeCanvas.drawText(
                                    "$label", 10f, y,
                                    android.graphics.Paint().apply {
                                        color = android.graphics.Color.BLACK
                                        textSize = 32f
                                    }
                                )
                            }
                        }

                        abscisses.forEachIndexed { i, label ->
                            val x = 60f + (i + 1) * stepX
                            drawIntoCanvas {
                                it.nativeCanvas.drawText(
                                    label, x - 20f, size.height - 10f,
                                    android.graphics.Paint().apply {
                                        color = android.graphics.Color.BLACK
                                        textSize = 30f
                                    }
                                )
                            }
                        }

                        val points = levelValues.mapIndexed { i, level ->
                            Offset(60f + (i + 1) * stepX, size.height - (level / 80f) * size.height)
                        }

                        points.zipWithNext().forEach { (p1, p2) ->
                            drawLine(Color.Blue, p1, p2, strokeWidth = 4f)
                        }

                        points.forEach {
                            drawCircle(Color.Blue, radius = 8f, center = it)
                        }
                    }
                }
            }
        }
    }
}
