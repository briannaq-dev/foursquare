package com.example.foursquare.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Card representing a single place (restaurant, activity, event, etc.).
 * Used in DiscoverScreen, PlacesScreen, and GroupDetailScreen.
 *
 * @param name       Display name of the place.
 * @param category   Category label (e.g. "Café", "Park").
 * @param rating     Rating out of 5.0.
 * @param distance   Distance string (e.g. "0.3 mi").
 * @param onClick    Called when the card is tapped.
 */
@Composable
fun PlaceCard(
    name: String,
    category: String,
    rating: Double,
    distance: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder icon / image slot
            Surface(
                modifier = Modifier.size(48.dp),
                shape    = MaterialTheme.shapes.small,
                color    = MaterialTheme.colorScheme.primaryContainer
            ) {}

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = name,     style = MaterialTheme.typography.titleSmall)
                Text(text = "$category · $distance",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.secondary)
                    Spacer(Modifier.width(2.dp))
                    Text(text = rating.toString(),
                        style = MaterialTheme.typography.labelSmall)
                }
            }

            IconButton(onClick = { /* TODO: save/unsave */ }) {
                Icon(Icons.Default.FavoriteBorder, contentDescription = "Save place")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlaceCardPreview() {
    PlaceCard(
        name     = "Tatte Bakery & Café",
        category = "Café",
        rating   = 4.6,
        distance = "0.3 mi",
        onClick  = {}
    )
}
