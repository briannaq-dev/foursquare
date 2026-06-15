package com.example.foursquare.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

/**
 * A reusable row of overlapping member avatar circles.
 * Used in [GroupDetailScreen] and [DashboardScreen] to show who's in a group.
 *
 * @param initials      List of 1–2 char initials, one per member (max 5 shown, then "+N" overflow).
 * @param modifier      Optional layout modifier.
 */
@Composable
fun MemberAvatarRow(
    initials: List<String>,
    modifier: Modifier = Modifier
) {
    val displayList = initials.take(5)
    val overflow    = initials.size - displayList.size

    Row(
        modifier          = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        displayList.forEachIndexed { index, initial ->
            Box(
                modifier = Modifier
                    .offset(x = (-8 * index).dp)  // overlap effect
                    .zIndex((displayList.size - index).toFloat())
            ) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape    = MaterialTheme.shapes.extraLarge,
                    color    = avatarColor(index),
                    border   = ButtonDefaults.outlinedButtonBorder
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text  = initial,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }

        // Overflow badge
        if (overflow > 0) {
            Spacer(Modifier.width(4.dp))
            Text(
                text  = "+$overflow",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Returns a distinct container color based on avatar position index.
@Composable
private fun avatarColor(index: Int) = when (index % 4) {
    0    -> MaterialTheme.colorScheme.primary
    1    -> MaterialTheme.colorScheme.secondary
    2    -> MaterialTheme.colorScheme.tertiary
    else -> MaterialTheme.colorScheme.error
}

// Preview
@Preview(showBackground = true)
@Composable
private fun MemberAvatarRowPreview() {
    MemberAvatarRow(
        initials = listOf("BQ", "VW", "JK", "MK", "AS", "TL"),
        modifier = Modifier.padding(16.dp)
    )
}
