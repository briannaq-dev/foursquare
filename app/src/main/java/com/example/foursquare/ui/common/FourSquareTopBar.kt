package com.example.foursquare.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

/**
 * Shared top app bar used by all screens.
 *
 * @param title        Screen title shown in the center/start.
 * @param showBack     Whether to show a back-navigation arrow.
 * @param onBack       Called when the back arrow is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FourSquareTopBar(
    title: String,
    showBack: Boolean = false,
    onBack: () -> Unit = {}
) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            if (showBack) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor    = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

// ── Preview ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun FourSquareTopBarPreview() {
    FourSquareTopBar(title = "Discover", showBack = false)
}

@Preview(showBackground = true)
@Composable
private fun FourSquareTopBarWithBackPreview() {
    FourSquareTopBar(title = "CS Squad", showBack = true)
}
