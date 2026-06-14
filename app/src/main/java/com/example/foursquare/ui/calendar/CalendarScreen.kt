package com.example.foursquare.ui.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.foursquare.ui.common.FourSquareTopBar

private data class DummyEvent(
    val id: String,
    val title: String,
    val dateTime: String,
    val groupName: String
)

private val dummyEvents = listOf(
    DummyEvent("e1", "Dinner @ Tatte",  "Today · 7:00 PM", "CS Squad"),
    DummyEvent("e2", "Movie night",     "Sat · 9:00 PM",   "Roomies"),
    DummyEvent("e3", "Boston Common walk", "Sun · 2:00 PM","Hometown")
)

/**
 * Screen 9 — Calendar
 * Displays locked-in group plans on a monthly calendar and supports adding personal events.
 */
@Composable
fun CalendarScreen() {
    var showAddEventDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { FourSquareTopBar(title = "Calendar") },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddEventDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add event")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier       = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Month header + grid placeholder
            item {
                Text("June 2026", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                // TODO: Replace with CalendarView or a custom month-grid composable
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
                        Text("Month grid goes here",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Upcoming events header
            item {
                Text("Upcoming plans", style = MaterialTheme.typography.titleSmall)
            }

            items(dummyEvents) { event ->
                EventCard(event = event)
            }
        }
    }

    // Add event dialog skeleton
    if (showAddEventDialog) {
        AddEventDialog(
            onDismiss = { showAddEventDialog = false },
            onSave    = { showAddEventDialog = false }
        )
    }
}

/** Card for a single upcoming event in the calendar list. */
@Composable
private fun EventCard(event: DummyEvent) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp)) {
            // Color accent bar (group color placeholder)
            Surface(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp),
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.small
            ) {}

            Spacer(Modifier.width(12.dp))

            Column {
                Text(event.title,     style = MaterialTheme.typography.bodyMedium)
                Text("${event.dateTime} · ${event.groupName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

/** Modal dialog for adding a new personal or group event. */
@Composable
private fun AddEventDialog(
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    var place by remember { mutableStateOf("") }
    var time  by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add event") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Date: 2026/06/03",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)

                OutlinedTextField(
                    value         = place,
                    onValueChange = { place = it },
                    label         = { Text("Place") },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth()
                )
                // TODO: Show Google Places autocomplete suggestions beneath field

                OutlinedTextField(
                    value         = time,
                    onValueChange = { time = it },
                    label         = { Text("Time") },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = onSave) { Text("Save Event") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CalendarScreenPreview() {
    CalendarScreen()
}

@Preview(showBackground = true)
@Composable
private fun AddEventDialogPreview() {
    AddEventDialog(onDismiss = {}, onSave = {})
}