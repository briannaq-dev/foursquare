package com.example.foursquare.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

/** Represents a single tab in the bottom navigation bar. */
data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: Any          // matches a @Serializable Destinations object
)

/** All five main tabs shown in the app's bottom navigation bar. */
val bottomNavItems = listOf(
    BottomNavItem("Discover", Icons.Default.Search,        com.example.foursquare.ui.navigation.Discover),
    BottomNavItem("Places",   Icons.Default.Place,         com.example.foursquare.ui.navigation.Places),
    BottomNavItem("Groups",   Icons.Default.Group,         com.example.foursquare.ui.navigation.Groups),
    BottomNavItem("Map",      Icons.Default.Map,           com.example.foursquare.ui.navigation.MapTab),
    BottomNavItem("Calendar", Icons.Default.CalendarMonth, com.example.foursquare.ui.navigation.Calendar)
)

/**
 * Bottom navigation bar rendered on every main-tab screen.
 *
 * @param currentRoute  The currently active route class name used to highlight the correct tab.
 * @param onTabSelected Called with the destination route when the user taps a tab.
 */
@Composable
fun FourSquareBottomBar(
    currentRoute: String?,
    onTabSelected: (Any) -> Unit
) {
    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected  = currentRoute == item.route::class.qualifiedName,
                onClick   = { onTabSelected(item.route) },
                icon      = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label     = { Text(item.label) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FourSquareBottomBarPreview() {
    FourSquareBottomBar(
        currentRoute = "com.example.foursquare.ui.navigation.Discover",
        onTabSelected = {}
    )
}
