package com.example.foursquare.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * A compact chip that surfaces a group event or plan inline.
 * Used on the [CalendarScreen] day cells and the [DashboardScreen] upcoming-plans section.
 *
 * @param label    Short event title (e.g. "Dinner @ Tatte").
 * @param timeText Short time string (e.g. "7 PM").
 * @param onClick  Called when the chip is tapped.
 * @param modifier Optional layout modifier.
 */
@Composable
fun EventChip(
    label: String,
    timeText: String = "",
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier  = modifier,
        shape     = MaterialTheme.shapes.small,
        color     = MaterialTheme.colorScheme.primaryContainer,
        onClick   = onClick
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector        = Icons.Default.CalendarToday,
                contentDescription = null,
                modifier           = Modifier.size(12.dp),
                tint               = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text  = if (timeText.isNotBlank()) "$label · $timeText" else label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1
            )
        }
    }
}

// Preview
@Preview(showBackground = true)
@Composable
private fun EventChipPreview() {
    Column(
        modifier            = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        EventChip(label = "Dinner @ Tatte", timeText = "7 PM")
        EventChip(label = "Movie night",    timeText = "9 PM")
        EventChip(label = "Boston Common Walk")
    }
}
