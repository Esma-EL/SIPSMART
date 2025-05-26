package fr.isen.elakrimi.sipsmart.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.AccountCircle

enum class NavItem(val label: String, val icon: ImageVector) {
    Home("Accueil", Icons.Filled.Home),
    Tips("Tips", Icons.Filled.Newspaper),
    Profile("Profil", Icons.Filled.AccountCircle)  // icône profil Material Design
}

@Composable
fun NavBar(
    selectedItem: NavItem,
    onItemSelected: (NavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xFFF98E8E), // Couleur rose
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(horizontal = 30.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem.values().forEachIndexed { index, item ->
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = if (item == selectedItem) Color.White else Color(0xFFEEEEEE),
                    modifier = Modifier
                        .size(35.dp)
                        .clickable { onItemSelected(item) }
                        .padding(horizontal = 1.dp)
                )

                // Trait entre les icônes, sauf après la dernière
                if (index < NavItem.values().lastIndex) {
                    Divider(
                        color = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier
                            .height(35.dp)
                            .width(2.dp)
                    )
                }
            }
        }
    }
}
