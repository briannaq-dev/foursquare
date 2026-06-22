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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foursquare.data.SavedPlace
import com.example.foursquare.ui.common.FourSquareTopBar
import com.example.foursquare.ui.common.PlaceCard
import com.example.foursquare.ui.places.PlacesViewModel

// ── Dummy discovery data ──────────────────────────────────────────────────────

private data class DummyPlace(
    val id: String,
    val name: String,
    val category: String,
    val rating: Double,
    val distance: String
)

private val dummyPlaces = listOf(
    DummyPlace("1", "Tatte Bakery & Café", "Café",    4.6, "0.3 mi"),
    DummyPlace("2", "Saltie Girl",          "Seafood", 4.8, "0.7 mi"),
    DummyPlace("3", "Boston Common",        "Park",    4.7, "0.8 mi"),
    DummyPlace("4", "Trident Booksellers",  "Books",   4.5, "0.9 mi"),
    DummyPlace("5", "Flour Bakery",         "Café",    4.5, "1.1 mi"),
    DummyPlace("6", "The Esplanade",        "Park",    4.6, "1.4 mi")
)

private val filterChips = listOf("All", "Food", "Activity", "Events")

// ── Screen ────────────────────────────────────────────────────────────────────

/**
 * Screen 4 — Discover
 * Browseable list of nearby places filtered by category and distance.
 * Users can save/unsave places directly from this list — saves are written
 * to Firestore via [PlacesViewModel].
 *
 * @param placesViewModel Provides saved-place state and save/remove actions.
 * @param onPlaceClick    Called with the place ID when a place card is tapped.
 */
@Composable
fun DiscoverScreen(
    placesViewModel: PlacesViewModel = viewModel(),
    onPlaceClick: (String) -> Unit
) {
    var searchQuery    by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    // Real saved places from Firestore — used to show filled/unfilled heart
    val savedPlaces by placesViewModel.savedPlaces.collectAsState()

    // Filter dummy places by search query and selected chip
    val filteredPlaces = dummyPlaces.filter { place ->
        val matchesSearch = searchQuery.isBlank() ||
                place.name.contains(searchQuery, ignoreCase = true) ||
                place.category.contains(searchQuery, ignoreCase = true)
        val matchesFilter = selectedFilter == "All" ||
                when (selectedFilter) {
                    "Food"     -> place.category in listOf("Café", "Seafood", "Bakery")
                    "Activity" -> place.category in listOf("Park", "Books", "Sports")
                    "Events"   -> false   // TODO: real events from API
                    else       -> true
                }
        matchesSearch && matchesFilter
    }

    Scaffold(
        topBar = { FourSquareTopBar(title = "Discover") }
    ) { innerPadding ->
        LazyColumn(
            modifier            = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding      = PaddingValues(16.dp),
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

            // Distance label
            item {
                Text(
                    text  = "Within 2 mi",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // TODO: Add Slider for radius control
            }

            // Empty state
            if (filteredPlaces.isEmpty()) {
                item {
                    Box(
                        modifier         = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text(
                            "No places found — try a different search or filter.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Place list with real save/unsave wired to Firestore
            items(filteredPlaces) { place ->
                val alreadySaved = savedPlaces.find { it.name == place.name }

                PlaceCard(
                    name     = place.name,
                    category = place.category,
                    rating   = place.rating,
                    distance = place.distance,
                    onClick  = { onPlaceClick(place.id) },
                    isSaved  = alreadySaved != null,
                    onSaveClick = {
                        if (alreadySaved != null) {
                            // Already saved — remove it
                            placesViewModel.removePlace(alreadySaved.id)
                        } else {
                            // Not saved — save it to Firestore
                            placesViewModel.savePlace(
                                SavedPlace(
                                    name     = place.name,
                                    category = place.category,
                                    rating   = place.rating
                                )
                            )
                        }
                    }
                )
            }
        }
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DiscoverScreenPreview() {
    DiscoverScreen(onPlaceClick = {})
}