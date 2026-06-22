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
import com.google.firebase.auth.FirebaseAuth

private val preferenceCategories = listOf(
    "Food", "Coffee", "Parks", "Concerts", "Sports", "Art"
)

/**
 * Profile Screen
 * Shows the signed-in user's real name/email from FirebaseAuth,
 * saved places stats, category preferences, and a sign-out button.
 *
 * @param onPlaceClick  Called with the place ID when a saved place is tapped.
 * @param onSignOut     Called when the user taps "Sign out".
 * @param onEditProfile Called when the user taps the edit icon.
 */
@Composable
fun ProfileScreen(
    onPlaceClick: (String) -> Unit = {},
    onSignOut: () -> Unit = {},
    onEditProfile: () -> Unit = {}
) {
    // Real user data from FirebaseAuth
    val currentUser  = FirebaseAuth.getInstance().currentUser
    val userName     = currentUser?.displayName
        ?: currentUser?.email?.substringBefore("@")
        ?: "User"
    val userEmail    = currentUser?.email ?: ""
    val userInitials = userName.take(2).uppercase()

    // Preference toggles — TODO: persist to Firestore user profile
    val selectedPrefs = remember { mutableStateListOf("Food", "Coffee") }

    // Placeholder saved place data — TODO: wire to PlacesViewModel
    val dummySavedPlaces = listOf(
        Triple("1", "Tatte Bakery & Café", "Café"),
        Triple("2", "Saltie Girl",          "Seafood"),
        Triple("3", "Trident Booksellers",  "Books")
    )

    Scaffold(
        topBar = { FourSquareTopBar(title = "Profile") }
    ) { innerPadding ->
        LazyColumn(
            modifier            = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding      = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // User header
            item {
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar circle with real initials
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape    = MaterialTheme.shapes.extraLarge,
                        color    = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text  = userInitials,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(userName,  style = MaterialTheme.typography.titleLarge)
                        Text(
                            userEmail,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
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
                        ProfileStat(label = "Saved",   value = dummySavedPlaces.size.toString())
                        VerticalDivider(modifier = Modifier.height(32.dp))
                        ProfileStat(label = "Visited", value = "0")
                        VerticalDivider(modifier = Modifier.height(32.dp))
                        ProfileStat(label = "Groups",  value = "0")
                    }
                }
            }

            // Category preferences
            item {
                Text("My Preferences", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement   = Arrangement.spacedBy(8.dp),
                    modifier              = Modifier.fillMaxWidth()
                ) {
                    preferenceCategories.forEach { pref ->
                        FilterChip(
                            selected = selectedPrefs.contains(pref),
                            onClick  = {
                                if (selectedPrefs.contains(pref)) selectedPrefs.remove(pref)
                                else selectedPrefs.add(pref)
                            },
                            label = { Text(pref) }
                        )
                    }
                }
            }

            // Saved places section
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

            items(dummySavedPlaces) { (id, name, category) ->
                PlaceCard(
                    name     = name,
                    category = category,
                    rating   = 4.5,
                    distance = "",
                    onClick  = { onPlaceClick(id) }
                )
            }

            // Visit history
            item {
                Text("Visit History", style = MaterialTheme.typography.titleMedium)
            }

            items(dummySavedPlaces) { (_, name, category) ->
                HistoryRow(name = name, category = category)
            }

            // Sign out button
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

/**
 * Small stat column used in the profile stats card.
 *
 * @param label Short label shown below the value.
 * @param value Numeric string to display.
 */
@Composable
private fun ProfileStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineSmall)
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Single row in the visit history list.
 *
 * @param name     Place name.
 * @param category Place category label.
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

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen(
        onPlaceClick  = {},
        onSignOut     = {},
        onEditProfile = {}
    )
}