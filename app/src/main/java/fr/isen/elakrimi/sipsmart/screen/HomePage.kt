package fr.isen.elakrimi.sipsmart.screen

import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Canvas
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.elakrimi.sipsmart.FirebaseAuthViewModel
import fr.isen.elakrimi.sipsmart.R
import fr.isen.elakrimi.sipsmart.activity.ScanActivity
import androidx.navigation.NavController
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap



@Composable
fun HomePage(
    viewModel: FirebaseAuthViewModel,
    onLogout: () -> Unit,
    onConnectClick: () -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val authState = viewModel.authState.collectAsState()
    val backgroundColor = Color(0xFFF98E8E)
    val whiteColor = Color.White


    // Charger le niveau liquide depuis Firebase au démarrage de la composable
    LaunchedEffect(Unit) {
        if (viewModel.authState.value is FirebaseAuthViewModel.AuthState.Success) {
            println("User connecté, on récupère les données")
            viewModel.fetchTemperatureFromFirebase()
            viewModel.fetchLiquidLevelFromFirebase()
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

               /* IconButton(
                    onClick = {
                        viewModel.saveHydrationGoalToFirebase(
                            goal = viewModel.hydrationGoal.value,
                            onSuccess = {
                                viewModel.signOut()
                                onLogout()
                            },
                            onFailure = {
                                viewModel.signOut()
                                onLogout()
                            }
                        )
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Logout,
                        contentDescription = "Déconnexion",
                        tint = whiteColor
                    )
                }*/
            }

            Spacer(modifier = Modifier.height(16.dp))

            HomePageContent(
                viewModel = viewModel,
                onConnectClick = onConnectClick,
                navController = navController,
                modifier = modifier
            )
        }
    }
}

@Composable
fun HomePageContent(
    viewModel: FirebaseAuthViewModel,
    onConnectClick: () -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hydrationGoal by viewModel.hydrationGoal.collectAsState()
    val montserratFontFamily = FontFamily(Font(R.font.montserrat_bold, FontWeight.W200))
    val liquidLevel by viewModel.liquidLevel.collectAsState(initial = 0f)
    val temperature by viewModel.lastTemperature.collectAsState()
    Column(
        modifier
            .fillMaxSize()
            .padding(40.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Niveau de la gourde ",
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 30.dp)
        )

        CircularHydrationProgress(progress = liquidLevel.coerceIn(0f, 1f))
        Thermometer(temperature = temperature ?: 0f)
        Spacer(modifier = Modifier.height(10.dp)) // ← ajoute un espace de 10dp ici
        Button(
            onClick = {
                val bluetoothManager =
                    context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
                val bluetoothAdapter = bluetoothManager?.adapter

                val locationManager =
                    context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
                val isLocationEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
                        locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true

                when {
                    bluetoothAdapter == null -> {
                        showAlert(context, "Bluetooth non supporté", "Ce dispositif ne supporte pas le Bluetooth.")
                    }

                    !bluetoothAdapter.isEnabled -> {
                        showAlert(context, "Bluetooth désactivé", "Veuillez activer le Bluetooth pour continuer.")
                    }

                    !isLocationEnabled -> {
                        showAlert(context, "Localisation désactivée", "Veuillez activer la localisation pour scanner les appareils BLE.")
                    }

                    else -> {
                        try {
                            val intent = Intent(context, ScanActivity::class.java)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            showAlert(context, "Erreur", "Impossible d'ouvrir l'activité de scan.")
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF98E8E)),
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = "Trouver ma gourde",
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

fun showAlert(context: Context, title: String, message: String) {
    AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("OK", null)
        .show()
}

@Composable
fun CircularHydrationProgress(progress: Float, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(130.dp)
    ) {
        // Cercle de fond (ombre pour l'effet 3D)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 50.dp.toPx()
            val radius = size.minDimension / 2 - strokeWidth / 2
            val center = Offset(size.width / 2, size.height / 2)

            // Ombre décalée pour simuler la profondeur
            drawArc(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFB66E6E), Color.Transparent),
                    center = center + Offset(10f, 10f), // décalage ombre
                    radius = radius
                ),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Cercle principal avec dégradé pour effet 3D
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth =20.dp.toPx()
            val radius = size.minDimension / 2 - strokeWidth / 2
            val center = Offset(size.width / 2, size.height / 2)

            drawArc(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFF98E8E), Color(0xFFC25A5A)),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, size.height)
                ),
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Text(
            text = "${(progress * 100).toInt()}%",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black
        )
    }
}


@Composable
fun Thermometer(
    temperature: Float,
    maxTemperature: Float = 50f,
    modifier: Modifier = Modifier
) {
    val fillRatio = (temperature / maxTemperature).coerceIn(0f, 1f)

    Column(
        modifier = modifier.width(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Affichage de la température en haut
        Text(
            text = "${temperature.toInt()}°C",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        // Canvas pour dessiner le thermomètre
        Box(
            modifier = Modifier
                .height(160.dp)
                .width(60.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Dimensions de la tige du thermomètre (rectangle arrondi)
                val tubeWidth = width * 0.3f
                val tubeLeft = (width - tubeWidth) / 2f
                val tubeTop = 0f
                val tubeBottom = height * 0.95f

                // Dessiner la tige extérieure (contour)
                drawRoundRect(
                    color = Color.LightGray,
                    topLeft = Offset(tubeLeft, tubeTop),
                    size = androidx.compose.ui.geometry.Size(tubeWidth, tubeBottom),
                    cornerRadius = CornerRadius(tubeWidth / 2, tubeWidth / 2),
                    style = Stroke(width = 9f)
                )

                // Dessiner le liquide dans la tige (rempli du bas vers le haut)
                val fillHeight = tubeBottom * fillRatio
                drawRoundRect(
                    color = Color(0xFFF98E8E),
                    topLeft = Offset(tubeLeft, tubeBottom - fillHeight),
                    size = androidx.compose.ui.geometry.Size(tubeWidth, fillHeight),
                    cornerRadius = CornerRadius(tubeWidth / 2, tubeWidth / 2),
                    style = Fill
                )

                // Dessiner le bulbe (rond en bas)
                val bulbRadius = width * 0.35f
                val bulbCenter = Offset(width / 2f, tubeBottom + bulbRadius / 2)

                // Contour du bulbe
                drawCircle(
                    color = Color.LightGray,
                    radius = bulbRadius,
                    center = bulbCenter,
                    style = Stroke(width = 9f)
                )

                // Liquide dans le bulbe (plein si température > 0)
                if (temperature > 0f) {
                    drawCircle(
                        color = Color(0xFFF98E8E),
                        radius = bulbRadius - 4f,
                        center = bulbCenter
                    )
                }
            }
        }
    }
}
