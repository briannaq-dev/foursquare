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
import com.example.foursquare.ui.common.FourSquareTopBar
import com.example.foursquare.ui.common.PlaceCard

// ── Dummy data ────────────────────────────────────────────────────────────────

private data class DummyPlaceRecord(
    val id: String, val name: String, val category: String,
    val rating: Double, val distance: String, val status: String
)

private val dummyMyPlaces = listOf(
    DummyPlaceRecord("1", "Tatte Bakery & Café", "Café",    4.6, "0.3 mi", "Visited"),
    DummyPlaceRecord("2", "Saltie Girl",          "Seafood", 4.8, "0.7 mi", "Visited"),
    DummyPlaceRecord("3", "Boston Common",        "Park",    4.7, "0.8 mi", "Planned"),
    DummyPlaceRecord("4", "Trident Booksellers",  "Books",   4.5, "0.9 mi", "Saved")
)

private val tabs = listOf("Saved", "Visited", "History")

// ── Screen ────────────────────────────────────────────────────────────────────

/**
 * Screen 5 — My Places
 * Shows the current user's saved spots, visited places, and history.
 *
 * @param onPlaceClick Called with the place ID when a card is tapped.
 */
@Composable
fun PlacesScreen(
    onPlaceClick: (String) -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

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
                modifier            = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PlaceStat(label = "Saved",   count = 47)
                PlaceStat(label = "Visited", count = 32)
                PlaceStat(label = "This month", count = 12)
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
                modifier       = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text  = "Recent",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                items(dummyMyPlaces) { place ->
                    PlaceCard(
                        name     = place.name,
                        category = place.category,
                        rating   = place.rating,
                        distance = place.distance,
                        onClick  = { onPlaceClick(place.id) }
                    )
                }
            }
        }
    }
}

/** Small stat column used in the summary row. */
@Composable
private fun PlaceStat(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = count.toString(), style = MaterialTheme.typography.headlineSmall)
        Text(text = label,           style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// ── Sub-screen ────────────────────────────────────────────────────────────────

/**
 * Sub-screen — Place Detail
 * Shows full information about a single place.
 *
 * @param placeId The ID of the place to display.
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
                title    = "Place Detail",   // TODO: replace with actual place name
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
            // TODO: Place image
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {}

            Text("Place ID: $placeId", style = MaterialTheme.typography.titleMedium)
            Text("Category · Distance", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)

            // TODO: Rating row, address, hours

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { /* TODO: Add to group */ },
                    modifier = Modifier.weight(1f)) {
                    Text("+ Add to group")
                }
                OutlinedButton(onClick = { /* TODO: Get directions */ },
                    modifier = Modifier.weight(1f)) {
                    Text("Directions")
                }
            }
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

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
