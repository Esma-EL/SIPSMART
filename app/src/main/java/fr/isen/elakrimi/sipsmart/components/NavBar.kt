package fr.isen.elakrimi.sipsmart.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavItem(val icon: ImageVector) {
    Home(Icons.Filled.Home),
    Bluetooth(Icons.Filled.Bluetooth)
}


@Composable
fun NavBar(
    selectedItem: NavItem,
    onItemSelected: (NavItem) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        NavItem.values().forEach { item ->
            Icon(
                imageVector = item.icon,
                contentDescription = item.name,
                tint = if (item == selectedItem)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onItemSelected(item) }
                    .padding(8.dp)
            )
        }
    }
}

