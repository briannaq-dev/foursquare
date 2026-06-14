package com.example.foursquare.ui.groups

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.foursquare.ui.common.FourSquareTopBar
import com.example.foursquare.ui.common.GroupCard

// ── Dummy data ────────────────────────────────────────────────────────────────

private data class DummyGroup(
    val id: String, val name: String, val memberCount: Int, val statusLine: String
)

private val dummyGroups = listOf(
    DummyGroup("g1", "CS Squad",  5, "Voting · 12 places"),
    DummyGroup("g2", "Hometown",  4, "No active plans"),
    DummyGroup("g3", "Roomies",   3, "Plan · Movie night")
)

private data class DummyVoteOption(val name: String, val votes: Int, val totalVotes: Int)

private val dummyVoteOptions = listOf(
    DummyVoteOption("Tatte Bakery", 4, 5),
    DummyVoteOption("Saltie Girl",  3, 5),
    DummyVoteOption("Boston Common",2, 5),
    DummyVoteOption("Trident Books",1, 5)
)

// ── Groups list screen ────────────────────────────────────────────────────────

/**
 * Screen 6 — Groups
 * Lists the user's groups and provides a way to create or join one via invite code.
 *
 * @param onGroupClick Called with the group ID when a group card is tapped.
 */
@Composable
fun GroupsScreen(
    onGroupClick: (String) -> Unit
) {
    var inviteCode by remember { mutableStateOf("") }

    Scaffold(
        topBar = { FourSquareTopBar(title = "Groups") },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: create group dialog */ }) {
                Icon(Icons.Default.Add, contentDescription = "Create group")
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
            // Create group button
            item {
                Button(
                    onClick  = { /* TODO: open create-group dialog */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Create a group")
                }
            }

            // Join via invite code
            item {
                Text("Join with code", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value         = inviteCode,
                        onValueChange = { inviteCode = it },
                        placeholder   = { Text("X4P-92K") },
                        singleLine    = true,
                        modifier      = Modifier.weight(1f)
                    )
                    Button(onClick = { /* TODO: join group */ }) {
                        Text("Join")
                    }
                }
            }

            // Groups list header
            item {
                Text("Your groups", style = MaterialTheme.typography.titleSmall)
            }

            items(dummyGroups) { group ->
                GroupCard(
                    name        = group.name,
                    memberCount = group.memberCount,
                    statusLine  = group.statusLine,
                    onClick     = { onGroupClick(group.id) }
                )
            }
        }
    }
}

// ── Group Detail sub-screen ───────────────────────────────────────────────────

/**
 * Screen 7 — Group Detail / Vote & Lock
 * Displays group members, a live voting poll, and a "Lock in final pick" button.
 *
 * @param groupId The ID of the group to display.
 * @param onBack  Called when the back arrow is tapped.
 */
@Composable
fun GroupDetailScreen(
    groupId: String,
    onBack: () -> Unit
) {
    // TODO: load actual votes from ViewModel / Firestore
    var userVote by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            FourSquareTopBar(
                title    = "CS Squad",   // TODO: replace with actual group name
                showBack = true,
                onBack   = onBack
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier       = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Group header: member count + next event
            item {
                Text(
                    text  = "5 members · Saturday dinner",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                // TODO: member avatar row
            }

            // Vote section
            item {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text("Vote on tonight", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Ends in 2h",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            items(dummyVoteOptions) { option ->
                VoteOptionRow(
                    name       = option.name,
                    votes      = option.votes,
                    totalVotes = option.totalVotes,
                    isSelected = userVote == option.name,
                    onVote     = { userVote = option.name }
                )
            }

            // Lock in button
            item {
                Button(
                    onClick  = { /* TODO: lock in final pick */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Lock in final pick")
                }
            }
        }
    }
}

/** Single row in the voting poll showing a place name and live vote bar. */
@Composable
private fun VoteOptionRow(
    name: String,
    votes: Int,
    totalVotes: Int,
    isSelected: Boolean,
    onVote: () -> Unit
) {
    val progress = if (totalVotes > 0) votes.toFloat() / totalVotes else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        onClick = onVote
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(name, style = MaterialTheme.typography.bodyMedium)
                Text("$votes/$totalVotes", style = MaterialTheme.typography.labelSmall)
            }
            Spacer(Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GroupsScreenPreview() {
    GroupsScreen(onGroupClick = {})
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GroupDetailScreenPreview() {
    GroupDetailScreen(groupId = "g1", onBack = {})
}
