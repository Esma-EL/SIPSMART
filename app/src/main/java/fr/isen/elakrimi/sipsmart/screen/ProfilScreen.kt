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
import androidx.compose.runtime.*


@Composable
fun ProfilScreen(
    viewModel: FirebaseAuthViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val authState = viewModel.authState.collectAsState()
    val backgroundColor = Color(0xFFF98E8E)
    val whiteColor = Color.White

    var hydrationGoal by remember { mutableStateOf("") }
    var emailForReset by remember { mutableStateOf("") }
    var confirmationMessage by remember { mutableStateOf("") }

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

            // Contenu principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                var goalInput by remember { mutableStateOf("") }

                Text(text = "Définis ton objectif d’hydratation (en L) :", style = MaterialTheme.typography.titleMedium)

                TextField(
                    value = goalInput,
                    onValueChange = { goalInput = it },
                    label = { Text("Ex: 2.0") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (goalInput.isNotBlank()) {
                            viewModel.updateHydrationGoal("${goalInput}L")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF98E8E),
                        contentColor = Color.White // couleur du texte
                    )
                ) {
                    Text("Enregistrer l’objectif")
                }




                Text(
                    " Changer ton mot de passe :",
                    style = MaterialTheme.typography.titleMedium
                )
                OutlinedTextField(
                    value = emailForReset,
                    onValueChange = { emailForReset = it },
                    label = { Text("Ton adresse email") },
                    singleLine = true
                )

                Button(
                    onClick = {
                        viewModel.sendPasswordResetEmail(emailForReset)
                        confirmationMessage = "Email de réinitialisation envoyé à $emailForReset 📬"
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF98E8E),
                        contentColor = Color.White // couleur du texte
                    )
                ) {
                    Text("Envoyer un lien de réinitialisation")
                }


                if (confirmationMessage.isNotBlank()) {
                    Text(
                        confirmationMessage,
                        color = Color(0xFF4CAF50),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}



