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

@Composable
fun TipsScreen(
    viewModel: FirebaseAuthViewModel,
    onLogout: () -> Unit,
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

            // Top bar rose avec texte blanc et bouton logout
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
                        viewModel.signOut()
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

            // Contenu des conseils d’hydratation
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Conseils pour bien s’hydrater 💧",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text("• Bois au moins 1.5L d'eau par jour.")
                Text("• Prends ta gourde partout avec toi.")
                Text("• N’attends pas d’avoir soif pour boire.")
                Text("• Alterne eau et boissons non sucrées.")
                // Tu peux ajouter d’autres conseils ici
            }
        }
    }
}
