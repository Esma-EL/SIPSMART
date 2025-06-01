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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

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
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Conseils pour bien s’hydrater",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Quantité recommandée", style = MaterialTheme.typography.titleMedium)
                Text("• Bois entre 1.5L et 2L d’eau par jour.")
                Text("• Augmente ta consommation si tu fais du sport ou s’il fait chaud.")

                Spacer(modifier = Modifier.height(8.dp))

                Text("Fréquence", style = MaterialTheme.typography.titleMedium)
                Text("• Bois régulièrement tout au long de la journée.")
                Text("• N’attends pas d’avoir soif : la sensation arrive tard.")

                Spacer(modifier = Modifier.height(8.dp))

                Text("Astuces pratiques", style = MaterialTheme.typography.titleMedium)
                Text("• Garde ta gourde connectée à portée de main.")
                Text("• Commence ta journée avec un verre d’eau.")
                Text("• Utilise l’application pour suivre ton niveau et ta température en temps réel.")

                Spacer(modifier = Modifier.height(8.dp))

                Text("Alimentation", style = MaterialTheme.typography.titleMedium)
                Text("• Complète ton hydratation avec des aliments riches en eau : pastèque, concombres, oranges.")
                Text("• Privilégie les boissons non sucrées.")

                Spacer(modifier = Modifier.height(8.dp))

                Text("Environnement", style = MaterialTheme.typography.titleMedium)
                Text("• En cas de forte chaleur, pense à boire plus souvent.")
                Text("• Adapte ta consommation en fonction de ton environnement et de ton activité.")
            }
        }
    }
}
