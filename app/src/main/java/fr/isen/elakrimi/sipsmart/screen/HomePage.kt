package fr.isen.elakrimi.sipsmart.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.isen.elakrimi.sipsmart.FirebaseAuthViewModel
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.Font
import fr.isen.elakrimi.sipsmart.R  // Assure-toi que ce package correspond bien à ton projet
import androidx.compose.runtime.getValue


@Composable
fun HomePage(
    viewModel: FirebaseAuthViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val authState = viewModel.authState.collectAsState()

    val backgroundColor = Color(0xFFF98E8E)
    val whiteColor = Color.White
    HomePageContent(viewModel = viewModel, modifier = modifier)

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background // page blanche/neutre par défaut
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Barre du haut, rose avec texte blanc et bouton blanc
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
                                // tu peux afficher un message d'erreur ou logger
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

            // Ici tu peux ajouter d'autres éléments sous la barre du haut
            Spacer(modifier = Modifier.height(16.dp))
            HomePageContent(viewModel = viewModel) // 👈 C’est ici que tu l’ajoutes
        }
    }
}

@Composable
fun CircularHydrationProgress(
    progress: Float, // entre 0f et 1f
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(150.dp) // taille du cercle
    ) {
        CircularProgressIndicator(
            progress = progress,
            strokeWidth = 30.dp, // épaisseur du cercle
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFF98E8E) // couleur rose
        )
        Text(
            text = "${(progress * 100).toInt()}%",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Black
        )
    }
}


@Composable
fun HomePageContent( viewModel: FirebaseAuthViewModel,
                     modifier: Modifier = Modifier
) {
    val hydrationGoal by viewModel.hydrationGoal.collectAsState()
    val montserratFontFamily = FontFamily(
        Font(R.font.montserrat_bold, FontWeight.W200
        )
    )
    Column(
        modifier = modifier
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

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = { /* vers page Bluetooth */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF98E8E))
        ) {
            Text("Se connecter à ma gourde", color = Color.White)
        }
    }
}

@Composable
fun Thermometer(
    temperature: Float, // température actuelle
    maxTemperature: Float = 50f, // max température à atteindre
    modifier: Modifier = Modifier
) {
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





