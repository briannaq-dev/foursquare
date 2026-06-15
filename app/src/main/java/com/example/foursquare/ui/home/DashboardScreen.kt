package com.example.foursquare.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.foursquare.ui.common.GroupCard

// Dummy data

private data class DummyDashGroup(
    val id: String,
    val name: String,
    val memberCount: Int,
    val statusLine: String
)

private data class DummyUpcomingPlan(
    val id: String,
    val title: String,
    val subtitle: String   // e.g. "Today · 7:00 PM · CS Squad"
)

private val dummyDashGroups = listOf(
    DummyDashGroup("g1", "CS Squad",  5, "Voting · 12 places"),
    DummyDashGroup("g2", "Hometown",  5, "No active plans"),
    DummyDashGroup("g3", "Roomies",   5, "Plan · Movie night")
)

private val dummyUpcomingPlans = listOf(
    DummyUpcomingPlan("e1", "Dinner @ Tatte",  "Today · 7:00 PM · CS Squad"),
    DummyUpcomingPlan("e2", "Movie night",     "Sat · 9:00 PM · Roomies")
)

// Screen

/**
 * Screen 3 — Dashboard
 * Home hub shown after sign-in. Surfaces the user's groups, upcoming plans, and a
 * quick search bar for discovering new places.
 *
 * @param userName          The signed-in user's display name.
 * @param onSearchClick     Called when the search bar is tapped.
 * @param onGroupClick      Called with the group ID when a group chip is tapped.
 * @param onSeeAllGroups    Called when "See all" next to "Your groups" is tapped.
 * @param onPlanClick       Called with the plan/event ID when an upcoming plan is tapped.
 * @param onProfileClick    Called when the user's avatar / initials button is tapped.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    userName: String = "Brianna",
    onSearchClick: () -> Unit = {},
    onGroupClick: (String) -> Unit = {},
    onSeeAllGroups: () -> Unit = {},
    onPlanClick: (String) -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text  = "Hi, $userName",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text  = "Where to next?",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    // User avatar / initials button
                    FilledTonalIconButton(onClick = onProfileClick) {
                        Text(
                            text  = userName.take(2).uppercase(),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier       = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
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

            // Your groups
            item {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text("Your groups", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = onSeeAllGroups) { Text("See all") }
                }

                Spacer(Modifier.height(8.dp))

                // Horizontal scrolling group chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(dummyDashGroups) { group ->
                        GroupAvatarChip(
                            name        = group.name,
                            memberCount = group.memberCount,
                            onClick     = { onGroupClick(group.id) }
                        )
                    }
                }
            }

            // Upcoming plans
            item {
                Text("Upcoming plans", style = MaterialTheme.typography.titleMedium)
            }

            items(dummyUpcomingPlans) { plan ->
                UpcomingPlanCard(
                    title    = plan.title,
                    subtitle = plan.subtitle,
                    onClick  = { onPlanClick(plan.id) }
                )
            }
        }
    }
}

// Sub-composables

/**
 * Vertical chip showing a group's avatar circle, name, and member count.
 * Used in the horizontal "Your groups" row on the Dashboard.
 */
@Composable
private fun GroupAvatarChip(
    name: String,
    memberCount: Int,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp)
    ) {
        // Avatar circle
        Surface(
            modifier = Modifier.size(52.dp),
            shape    = MaterialTheme.shapes.extraLarge,
            color    = MaterialTheme.colorScheme.primaryContainer,
            onClick  = onClick
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text  = name.first().toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text     = name,
            style    = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
        Text(
            text  = "$memberCount members",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Card for a single upcoming group plan shown on the Dashboard.
 *
 * @param title    Event title (e.g. "Dinner @ Tatte").
 * @param subtitle Date/time/group info line.
 * @param onClick  Called when the card is tapped.
 */
@Composable
private fun UpcomingPlanCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        onClick   = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier          = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Color accent bar (group color placeholder)
            Surface(
                modifier = Modifier
                    .width(4.dp)
                    .height(36.dp),
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.small
            ) {}

            Spacer(Modifier.width(12.dp))

            Column {
                Text(title,    style = MaterialTheme.typography.bodyMedium)
                Text(subtitle, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// Previews

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DashboardScreenPreview() {
    DashboardScreen(
        userName       = "Brianna",
        onGroupClick   = {},
        onSeeAllGroups = {},
        onPlanClick    = {},
        onProfileClick = {}
    )
}
