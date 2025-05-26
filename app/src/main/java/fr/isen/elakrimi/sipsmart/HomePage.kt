package fr.isen.elakrimi.sipsmart

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.isen.elakrimi.sipsmart.components.NavItem

@Composable
fun HomePage(
    selectedNavItem: NavItem,
    onNavItemSelected: (NavItem) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Titre de l’écran selon l’élément sélectionné
        Text(
            text = when (selectedNavItem) {
                NavItem.Home -> "Accueil"
                NavItem.Bluetooth -> "Connexion Bluetooth"
            },
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Contenu selon la page sélectionnée
        when (selectedNavItem) {
            NavItem.Home -> {
                Text("Bienvenue sur SipSmart. Ici tu peux voir l’état général.")
            }
            NavItem.Bluetooth -> {
                Text("Connexion Bluetooth en cours...")
                // Ici tu pourras appeler une fonction ou une UI liée au Bluetooth
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Se déconnecter")
        }
    }
}
