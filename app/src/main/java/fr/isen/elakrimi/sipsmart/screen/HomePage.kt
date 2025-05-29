package fr.isen.elakrimi.sipsmart.screen

import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
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

                IconButton(
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
                }
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

    Column(
        modifier
            .fillMaxSize()
            .padding(40.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Objectif d’hydratation 💧 : $hydrationGoal",
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 30.dp)
        )

        CircularHydrationProgress(progress = 0.6f)
        Thermometer(temperature = 32f)

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
                fontSize = 15.sp,
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
        modifier = modifier.size(150.dp)
    ) {
        CircularProgressIndicator(
            progress = progress,
            strokeWidth = 30.dp,
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFF98E8E)
        )
        Text(
            text = "${(progress * 100).toInt()}%",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black
        )
    }
}

@Composable
fun Thermometer(temperature: Float, maxTemperature: Float = 50f, modifier: Modifier = Modifier) {
    val fillRatio = (temperature / maxTemperature).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .width(40.dp)
            .height(150.dp)
            .background(Color.LightGray, shape = MaterialTheme.shapes.small)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fillRatio)
                .align(Alignment.BottomCenter)
                .background(Color(0xFFF98E8E), shape = MaterialTheme.shapes.small)
        )
        Text(
            text = "${temperature.toInt()}°C",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 4.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black
        )
    }
}
