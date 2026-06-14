package com.example.foursquare.ui.map

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.foursquare.ui.common.FourSquareTopBar

// ── Map screen ────────────────────────────────────────────────────────────────

/**
 * Screen 8 — Map (Navigate Together)
 * Shows a shared live map with group members' locations and ETA info.
 *
 * @param onOpenDirections Called with (placeId, placeName) when the user taps "Get Directions".
 */
@Composable
fun MapScreen(
    onOpenDirections: (placeId: String, placeName: String) -> Unit
) {
    Scaffold(
        topBar = { FourSquareTopBar(title = "Map") },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: center on user location */ }) {
                Icon(Icons.Default.MyLocation, contentDescription = "My location")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Map placeholder — replace with GoogleMap composable
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text  = "Google Maps SDK goes here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    // TODO: GoogleMap(cameraPositionState = ...) { /* markers */ }
                }
            }

            // Bottom info card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Tatte Bakery & Café",       // TODO: replace with locked destination
                        style = MaterialTheme.typography.titleMedium)
                    Text("Newbury St · 1.2 mi",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(Modifier.height(12.dp))

                    // ETA / miles / arrived summary
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MapStat("12",  "min ETA")
                        MapStat("1.2", "miles")
                        MapStat("3/5", "arrived")
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick  = { /* TODO: check in */ },
                            modifier = Modifier.weight(1f)
                        ) { Text("Check in") }

                        Button(
                            onClick  = {
                                onOpenDirections("1", "Tatte Bakery & Café")
                            },
                            modifier = Modifier.weight(1f)
                        ) { Text("Share ETA") }
                    }
                }
            }
        }
    }
}

/** Small stat column used in the map bottom card. */
@Composable
private fun MapStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineSmall)
        Text(label, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// ── Directions sub-screen ─────────────────────────────────────────────────────

/**
 * Sub-screen — Directions
 * Shows step-by-step walking/driving directions via the Google Directions API.
 *
 * @param placeId   ID of the destination place.
 * @param placeName Display name of the destination.
 * @param onBack    Called when the back arrow is tapped.
 */
@Composable
fun DirectionsScreen(
    placeId: String,
    placeName: String,
    onBack: () -> Unit
) {
    var travelMode by remember { mutableStateOf("Walking") }
    val modes = listOf("Walking", "Driving", "Transit")

    Scaffold(
        topBar = {
            FourSquareTopBar(
                title    = "Directions",
                showBack = true,
                onBack   = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Live ETA: updates as you move.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Destination
            Text("Destination", style = MaterialTheme.typography.labelMedium)
            OutlinedTextField(
                value         = placeName,
                onValueChange = {},
                readOnly      = true,
                modifier      = Modifier.fillMaxWidth()
            )

            // Travel mode selector
            Text("Mode (optimal route)", style = MaterialTheme.typography.labelMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                modes.forEach { mode ->
                    FilterChip(
                        selected = travelMode == mode,
                        onClick  = { travelMode = mode },
                        label    = { Text(mode) }
                    )
                }
            }

            HorizontalDivider()

            // TODO: Fetch and display actual directions from Google Directions API
            Text("ETA: 10 mins · 0.4 mi",      style = MaterialTheme.typography.bodyMedium)
            Text("Steps (approx.): 917",         style = MaterialTheme.typography.bodyMedium)
            Text("Last updated: --:-- --",       style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(8.dp))

            TextButton(onClick = { /* TODO: open Google Maps deep link */ }) {
                Text("Open route in Google Maps")
            }
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MapScreenPreview() {
    MapScreen(onOpenDirections = { _, _ -> })
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DirectionsScreenPreview() {
    DirectionsScreen(
        placeId   = "1",
        placeName = "Tatte Bakery & Café",
        onBack    = {}
    )
}
