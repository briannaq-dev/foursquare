package com.example.foursquare.ui.discover

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.foursquare.ui.common.FourSquareBottomBar
import com.example.foursquare.ui.common.FourSquareTopBar
import com.example.foursquare.ui.common.PlaceCard

// ── Dummy data ────────────────────────────────────────────────────────────────

private data class DummyPlace(
    val id: String, val name: String, val category: String,
    val rating: Double, val distance: String
)

private val dummyPlaces = listOf(
    DummyPlace("1", "Tatte Bakery & Café", "Café",    4.6, "0.3 mi"),
    DummyPlace("2", "Saltie Girl",          "Seafood", 4.8, "0.7 mi"),
    DummyPlace("3", "Boston Common",        "Park",    4.7, "0.8 mi"),
    DummyPlace("4", "Trident Booksellers",  "Books",   4.5, "0.9 mi")
)

private val filterChips = listOf("All", "Food", "Activity", "Events")

// ── Screen ────────────────────────────────────────────────────────────────────

/**
 * Screen 4 — Discover
 * Browseable list of nearby places filtered by category and distance.
 *
 * @param onPlaceClick Called with the place ID when a place card is tapped.
 */
@Composable
fun DiscoverScreen(
    onPlaceClick: (String) -> Unit
) {
    var searchQuery     by remember { mutableStateOf("") }
    var selectedFilter  by remember { mutableStateOf("All") }

    Scaffold(
        topBar = { FourSquareTopBar(title = "Discover") }
    ) { innerPadding ->
        LazyColumn(
            modifier       = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search bar
            item {
                OutlinedTextField(
                    value         = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder   = { Text("Search places, events…") },
                    leadingIcon   = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth()
                )
            }

            // Category filter chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filterChips) { chip ->
                        FilterChip(
                            selected = selectedFilter == chip,
                            onClick  = { selectedFilter = chip },
                            label    = { Text(chip) }
                        )
                    }
                }
            }

            // Distance slider label (TODO: hook up actual slider)
            item {
                Text(
                    text  = "Within 2 mi",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // TODO: Add Slider for radius control
            }

            // Place list
            items(dummyPlaces) { place ->
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

// ── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DiscoverScreenPreview() {
    DiscoverScreen(onPlaceClick = {})
}
