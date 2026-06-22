package com.example.foursquare.ui.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.example.foursquare.ui.common.FourSquareTopBar

/**
 * Screen 8 — Map (Navigate Together)
 * Shows a live Google Map centered on Boston with a marker for the
 * group's destination. Requests location permission on first open.
 *
 * @param onOpenDirections Called with (placeId, placeName) when the user taps "Directions".
 */
@Composable
fun MapScreen(
    onOpenDirections: (placeId: String, placeName: String) -> Unit
) {
    val context = LocalContext.current

    // Boston as default center
    val boston = LatLng(42.3601, -71.0589)

    // Track whether location permission has been granted
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted -> hasLocationPermission = isGranted }

    // Request permission when screen first opens
    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(boston, 13f)
    }

    Scaffold(
        topBar = { FourSquareTopBar(title = "Map") },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Re-center on Boston (TODO: re-center on user's real location)
                cameraPositionState.position =
                    CameraPosition.fromLatLngZoom(boston, 13f)
            }) {
                Icon(Icons.Default.MyLocation, contentDescription = "My location")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Live Google Map
            GoogleMap(
                modifier            = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                cameraPositionState = cameraPositionState,
                properties          = MapProperties(
                    isMyLocationEnabled = hasLocationPermission
                ),
                uiSettings          = MapUiSettings(
                    myLocationButtonEnabled = false,  // we use our own FAB
                    zoomControlsEnabled     = true
                )
            ) {
                // Destination marker — Tatte Bakery as example
                // TODO: Replace with the group's locked destination from Firestore
                Marker(
                    state   = MarkerState(
                        position = LatLng(42.3494, -71.0785)  // Tatte on Newbury St
                    ),
                    title   = "Tatte Bakery & Café",
                    snippet = "Group destination"
                )
            }

            // Bottom info card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Tatte Bakery & Café",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Newbury St · 1.2 mi",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(12.dp))

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
                            onClick  = { onOpenDirections("1", "Tatte Bakery & Café") },
                            modifier = Modifier.weight(1f)
                        ) { Text("Directions") }
                    }
                }
            }
        }
    }
}

/**
 * Small stat column used in the map bottom card.
 *
 * @param value The numeric value to display (e.g. "12").
 * @param label The label below the value (e.g. "min ETA").
 */
@Composable
private fun MapStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineSmall)
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Sub-screen — Directions
 * Shows step-by-step directions info and a link to open Google Maps.
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
            modifier            = Modifier
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

            Text("Destination", style = MaterialTheme.typography.labelMedium)
            OutlinedTextField(
                value         = placeName,
                onValueChange = {},
                readOnly      = true,
                modifier      = Modifier.fillMaxWidth()
            )

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

            Text("ETA: 10 mins · 0.4 mi",  style = MaterialTheme.typography.bodyMedium)
            Text("Steps (approx.): 917",    style = MaterialTheme.typography.bodyMedium)
            Text(
                "Last updated: --:-- --",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            TextButton(onClick = { /* TODO: open Google Maps deep link */ }) {
                Text("Open route in Google Maps")
            }
        }
    }
}

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