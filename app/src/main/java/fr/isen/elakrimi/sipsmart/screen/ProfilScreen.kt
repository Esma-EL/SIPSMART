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
import androidx.compose.ui.graphics.StrokeCap
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
    val backgroundColor = Color(0xFFF98E8E)
    val whiteColor = Color.White

    // Simulated values for screenshot
    val temperatureHistory = listOf(21f, 23f, 22f, 24f, 25f)
    val liquidLevelHistory = listOf(0.8f, 0.2f, 0.8f, 0.2f, 0.8f)
    var showCharts by remember { mutableStateOf(true) }

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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Button(
                    onClick = { showCharts = !showCharts },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF98E8E),
                        contentColor = Color.White
                    )
                ) {
                    Text(if (showCharts) "Masquer l'historique" else "Afficher l'historique")
                }

                if (showCharts) {
                    Text("Températures récentes (°C)", style = MaterialTheme.typography.titleMedium)
                    ChartWithAxesTemperature(values = temperatureHistory, lineColor = Color(0xFFF98E8E))

                    Text("Hydratation récente (%)", style = MaterialTheme.typography.titleMedium)
                    ChartWithAxesLiquid(values = liquidLevelHistory, lineColor = Color(0xFF3A9AD9))
                }
            }
        }
    }
}

@Composable
fun ChartWithAxesTemperature(values: List<Float>, lineColor: Color, modifier: Modifier = Modifier.fillMaxWidth().height(220.dp).padding(8.dp)) {
    if (values.isEmpty()) {
        Text("Pas de données à afficher")
        return
    }

    Canvas(modifier = modifier) {
        val padding = 40f
        val graphWidth = size.width - padding
        val graphHeight = size.height - padding
        val minVal = 0f
        val maxVal = 30f
        val range = maxVal - minVal

        drawLine(Color.Gray, Offset(padding, 0f), Offset(padding, size.height), strokeWidth = 2f)
        drawLine(Color.Gray, Offset(padding, size.height - padding), Offset(size.width, size.height - padding), strokeWidth = 2f)

        val gradValues = listOf(0f, 10f, 20f, 30f)
        gradValues.forEach { grad ->
            val y = size.height - padding - ((grad - minVal) / range) * graphHeight
            drawContext.canvas.nativeCanvas.drawText(
                "${grad.toInt()}°C", 0f, y,
                android.graphics.Paint().apply {
                    textSize = 30f
                    color = android.graphics.Color.BLACK
                }
            )
        }

        val pointSpacing = graphWidth / (values.size - 1)
        val points = values.mapIndexed { index, value ->
            val x = padding + index * pointSpacing
            val y = size.height - padding - ((value - minVal) / range) * graphHeight
            Offset(x, y)
        }

        for (i in 0 until points.size - 1) {
            drawLine(color = lineColor, start = points[i], end = points[i + 1], strokeWidth = 4f, cap = StrokeCap.Round)
        }

        points.forEach {
            drawCircle(color = lineColor, radius = 8f, center = it)
        }

        points.forEachIndexed { index, point ->
            drawContext.canvas.nativeCanvas.drawText("M${index + 1}", point.x - 15f, size.height,
                android.graphics.Paint().apply {
                    textSize = 30f
                    color = android.graphics.Color.DKGRAY
                })
        }
    }
}

@Composable
fun ChartWithAxesLiquid(values: List<Float>, lineColor: Color, modifier: Modifier = Modifier.fillMaxWidth().height(220.dp).padding(8.dp)) {
    if (values.isEmpty()) {
        Text("Pas de données à afficher")
        return
    }

    Canvas(modifier = modifier) {
        val padding = 40f
        val graphWidth = size.width - padding
        val graphHeight = size.height - padding
        val minVal = 0.0f
        val maxVal = 0.8f
        val range = maxVal - minVal

        drawLine(Color.Gray, Offset(padding, 0f), Offset(padding, size.height), strokeWidth = 2f)
        drawLine(Color.Gray, Offset(padding, size.height - padding), Offset(size.width, size.height - padding), strokeWidth = 2f)

        val gradValues = listOf(0f, 0.2f, 0.8f)
        gradValues.forEach { grad ->
            val y = size.height - padding - ((grad - minVal) / range) * graphHeight
            drawContext.canvas.nativeCanvas.drawText("${(grad * 100).toInt()}%", 0f, y,
                android.graphics.Paint().apply {
                    textSize = 30f
                    color = android.graphics.Color.BLACK
                })
        }

        val pointSpacing = graphWidth / (values.size - 1)
        val points = values.mapIndexed { index, value ->
            val safeValue = value.coerceIn(minVal, maxVal)
            val x = padding + index * pointSpacing
            val y = size.height - padding - ((safeValue - minVal) / range) * graphHeight
            Offset(x, y)
        }

        for (i in 0 until points.size - 1) {
            drawLine(color = lineColor, start = points[i], end = points[i + 1], strokeWidth = 4f, cap = StrokeCap.Round)
        }

        points.forEach {
            drawCircle(color = lineColor, radius = 8f, center = it)
        }

        points.forEachIndexed { index, point ->
            drawContext.canvas.nativeCanvas.drawText("M${index + 1}", point.x - 15f, size.height,
                android.graphics.Paint().apply {
                    textSize = 30f
                    color = android.graphics.Color.DKGRAY
                })
        }
    }
}
