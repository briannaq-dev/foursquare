package com.example.foursquare.ui.places

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foursquare.ui.common.FourSquareTopBar
import com.example.foursquare.ui.common.PlaceCard

private val tabs = listOf("Saved", "Visited", "History")

/**
 * Screen 5 — My Places
 * Shows the signed-in user's saved places read from Firestore.
 * Tabs for Saved, Visited, and History.
 *
 * @param viewModel    Provides real-time saved places and save/remove actions.
 * @param onPlaceClick Called with the place ID when a card is tapped.
 */
@Composable
fun PlacesScreen(
    viewModel: PlacesViewModel = viewModel(),
    onPlaceClick: (String) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Real saved places from Firestore
    val savedPlaces by viewModel.savedPlaces.collectAsState()

    Scaffold(
        topBar = { FourSquareTopBar(title = "My Places") }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Summary stats row
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PlaceStat(label = "Saved",      count = savedPlaces.size)
                PlaceStat(label = "Visited",    count = 0)   // TODO: track visited separately
                PlaceStat(label = "This month", count = savedPlaces.count {
                    val oneMonthAgo = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
                    it.savedAt > oneMonthAgo
                })
            }

            // Tabs: Saved / Visited / History
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick  = { selectedTabIndex = index },
                        text     = { Text(title) }
                    )
                }
            }

            // Place list
            LazyColumn(
                modifier            = Modifier.fillMaxSize(),
                contentPadding      = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (savedPlaces.isEmpty()) {
                    item {
                        Box(
                            modifier         = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No saved places yet — tap ♡ on any place to save it.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    item {
                        Text(
                            text  = "Recent",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    items(savedPlaces) { place ->
                        PlaceCard(
                            name     = place.name,
                            category = place.category,
                            rating   = place.rating,
                            distance = "",
                            onClick  = { onPlaceClick(place.id) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Small stat column used in the summary row.
 *
 * @param label Short label shown below the count.
 * @param count Numeric value to display.
 */
@Composable
private fun PlaceStat(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = count.toString(), style = MaterialTheme.typography.headlineSmall)
        Text(
            text  = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Sub-screen — Place Detail
 * Shows full information about a single place with options to save or get directions.
 *
 * @param placeId The Firestore ID of the place to display.
 * @param onBack  Called when the back arrow is tapped.
 */
@Composable
fun PlaceDetailScreen(
    placeId: String,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            FourSquareTopBar(
                title    = "Place Detail",
                showBack = true,
                onBack   = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Place image placeholder
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.medium
            ) {}

            Text("Place ID: $placeId", style = MaterialTheme.typography.titleMedium)
            Text(
                "Category · Distance",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // TODO: Rating row, address, hours from Google Places API

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick  = { /* TODO: Add to group */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+ Add to group")
                }
                OutlinedButton(
                    onClick  = { /* TODO: Get directions */ },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Directions")
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PlacesScreenPreview() {
    PlacesScreen(onPlaceClick = {})
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PlaceDetailScreenPreview() {
    PlaceDetailScreen(placeId = "1", onBack = {})
}