package com.example.foursquare.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.foursquare.ui.common.FourSquareTopBar
import com.example.foursquare.ui.common.PlaceCard

// Dummy data

private data class DummySavedPlace(
    val id: String,
    val name: String,
    val category: String,
    val rating: Double,
    val distance: String
)

private val dummySavedPlaces = listOf(
    DummySavedPlace("1", "Tatte Bakery & Café", "Café",    4.6, "0.3 mi"),
    DummySavedPlace("2", "Saltie Girl",          "Seafood", 4.8, "0.7 mi"),
    DummySavedPlace("3", "Trident Booksellers",  "Books",   4.5, "0.9 mi")
)

private val preferenceCategories = listOf("Food", "Coffee", "Parks", "Concerts", "Sports", "Art")

// Screen

/**
 * Profile Screen
 * Displays the signed-in user's avatar, stats, saved places, category preferences,
 * and a visit history section.
 *
 * @param onPlaceClick  Called with the place ID when a saved place card is tapped.
 * @param onSignOut     Called when the user taps "Sign out".
 * @param onEditProfile Called when the user taps "Edit profile".
 */
@Composable
fun ProfileScreen(
    onPlaceClick: (String) -> Unit = {},
    onSignOut: () -> Unit = {},
    onEditProfile: () -> Unit = {}
) {
    // TODO: load from ViewModel / auth state
    val userName   = "Vanessa Wang"
    val userEmail  = "wang.van@northeastern.edu"
    val savedCount = 47
    val visitedCount = 32
    val groupCount = 3

    // Preference toggles
    // TODO: persist to Firestore user profile
    val selectedPrefs = remember { mutableStateListOf("Food", "Coffee") }

    Scaffold(
        topBar = {
            FourSquareTopBar(
                title = "Profile"
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier       = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // User header
            item {
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar circle
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape    = MaterialTheme.shapes.extraLarge,
                        color    = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text  = userName.take(2).uppercase(),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(userName,  style = MaterialTheme.typography.titleLarge)
                        Text(userEmail, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    IconButton(onClick = onEditProfile) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit profile")
                    }
                }
            }

            // Stats row
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier              = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProfileStat(label = "Saved",   value = savedCount.toString())
                        VerticalDivider(modifier = Modifier.height(32.dp))
                        ProfileStat(label = "Visited", value = visitedCount.toString())
                        VerticalDivider(modifier = Modifier.height(32.dp))
                        ProfileStat(label = "Groups",  value = groupCount.toString())
                    }
                }
            }

            // Category preferences
            item {
                Text("My Preferences", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                // Wrap chips in a flow-like row using multiple rows
                // TODO: replace with FlowRow when stable
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    preferenceCategories.forEach { pref ->
                        FilterChip(
                            selected = selectedPrefs.contains(pref),
                            onClick  = {
                                if (selectedPrefs.contains(pref)) selectedPrefs.remove(pref)
                                else selectedPrefs.add(pref)
                                // TODO: sync preference update to Firestore
                            },
                            label = { Text(pref) }
                        )
                    }
                }
            }

            // Saved places
            item {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text("Saved Places", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { /* TODO: navigate to full PlacesScreen */ }) {
                        Text("See all")
                    }
                }
            }

            items(dummySavedPlaces) { place ->
                PlaceCard(
                    name     = place.name,
                    category = place.category,
                    rating   = place.rating,
                    distance = place.distance,
                    onClick  = { onPlaceClick(place.id) }
                )
            }

            // History
            item {
                Text("Visit History", style = MaterialTheme.typography.titleMedium)
            }

            items(dummySavedPlaces) { place ->
                HistoryRow(name = place.name, category = place.category)
            }

            // Sign out
            item {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick  = onSignOut,
                    modifier = Modifier.fillMaxWidth(),
                    colors   = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Sign out")
                }
            }
        }
    }
}

// Sub-composables

/** Small stat column used in the profile stats card. */
@Composable
private fun ProfileStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineSmall)
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/**
 * Single row in the visit history list.
 *
 * @param name     Place name.
 * @param category Place category.
 */
@Composable
private fun HistoryRow(name: String, category: String) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector        = Icons.Default.History,
            contentDescription = null,
            tint               = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier           = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(name,     style = MaterialTheme.typography.bodyMedium)
            Text(category, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
    HorizontalDivider()
}

// Previews

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen(
        onPlaceClick  = {},
        onSignOut     = {},
        onEditProfile = {}
    )
}
