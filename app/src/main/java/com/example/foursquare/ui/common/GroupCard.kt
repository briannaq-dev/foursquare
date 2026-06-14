package com.example.foursquare.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Card representing a single group in the Groups list.
 *
 * @param name        Group display name (e.g. "CS Squad").
 * @param memberCount Number of members.
 * @param statusLine  Short status string (e.g. "Voting · 12 places" or "No active plans").
 * @param onClick     Called when the card is tapped.
 */
@Composable
fun GroupCard(
    name: String,
    memberCount: Int,
    statusLine: String,
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
            // Avatar circle placeholder
            Surface(
                modifier = Modifier.size(40.dp),
                shape    = MaterialTheme.shapes.extraLarge,
                color    = MaterialTheme.colorScheme.tertiaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text  = name.first().toString(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, style = MaterialTheme.typography.titleSmall)
                Text(
                    text  = "$memberCount members · $statusLine",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GroupCardPreview() {
    GroupCard(
        name        = "CS Squad",
        memberCount = 5,
        statusLine  = "Voting · 12 places",
        onClick     = {}
    )
}
