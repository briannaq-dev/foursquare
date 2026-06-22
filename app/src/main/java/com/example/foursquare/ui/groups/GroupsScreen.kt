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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foursquare.ui.common.FourSquareTopBar
import com.example.foursquare.ui.common.GroupCard

/**
 * Screen 6 — Groups
 * Lists the signed-in user's groups from Firestore.
 * Allows creating a new group or joining one via invite code.
 *
 * @param viewModel    Provides real-time group list and create/join actions.
 * @param onGroupClick Called with the group ID when a group card is tapped.
 */
@Composable
fun GroupsScreen(
    viewModel: GroupsViewModel = viewModel(),
    onGroupClick: (String) -> Unit
) {
    var inviteCode       by remember { mutableStateOf("") }
    var showCreateDialog by remember { mutableStateOf(false) }

    // Real Firestore data — recomposes automatically when groups change
    val groups by viewModel.groups.collectAsState()

    Scaffold(
        topBar = { FourSquareTopBar(title = "Groups") },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Create group")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier            = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding      = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Create group button
            item {
                Button(
                    onClick  = { showCreateDialog = true },
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
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value         = inviteCode,
                        onValueChange = { inviteCode = it },
                        placeholder   = { Text("X4P-92K") },
                        singleLine    = true,
                        modifier      = Modifier.weight(1f)
                    )
                    Button(
                        onClick = {
                            if (inviteCode.isNotBlank()) {
                                viewModel.joinGroup(inviteCode)
                                inviteCode = ""
                            }
                        }
                    ) {
                        Text("Join")
                    }
                }
            }

            // Groups list header
            item {
                Text(
                    text  = if (groups.isEmpty()) "No groups yet" else "Your groups",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Real groups from Firestore
            items(groups) { group ->
                GroupCard(
                    name        = group.name,
                    memberCount = group.memberIds.size,
                    statusLine  = "Invite code: ${group.inviteCode}",
                    onClick     = { onGroupClick(group.id) }
                )
            }
        }
    }

    // Create group dialog
    if (showCreateDialog) {
        CreateGroupDialog(
            onDismiss     = { showCreateDialog = false },
            onCreateGroup = { name ->
                viewModel.createGroup(name)
                showCreateDialog = false
            }
        )
    }
}

/**
 * Dialog for creating a new group.
 * Shown when the user taps "Create a group" or the FAB.
 *
 * @param onDismiss     Called when the dialog is dismissed.
 * @param onCreateGroup Called with the group name when the user confirms.
 */
@Composable
private fun CreateGroupDialog(
    onDismiss: () -> Unit,
    onCreateGroup: (String) -> Unit
) {
    var groupName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title            = { Text("Create a group") },
        text             = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text  = "Give your group a name. You'll get an invite code to share.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value         = groupName,
                    onValueChange = { groupName = it },
                    label         = { Text("Group name") },
                    placeholder   = { Text("e.g. CS Squad") },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick  = { if (groupName.isNotBlank()) onCreateGroup(groupName) },
                enabled  = groupName.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

/**
 * Screen 7 — Group Detail / Vote & Lock
 * Displays group members, a live voting poll, and a lock-in button.
 *
 * @param groupId The ID of the group to display.
 * @param onBack  Called when the back arrow is tapped.
 */
@Composable
fun GroupDetailScreen(
    groupId: String,
    onBack: () -> Unit
) {
    var userVote by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            FourSquareTopBar(
                title    = "Group Detail",
                showBack = true,
                onBack   = onBack
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier            = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding      = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text  = "Group ID: $groupId",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

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

// Dummy vote options for GroupDetailScreen preview
private data class DummyVoteOption(val name: String, val votes: Int, val totalVotes: Int)
private val dummyVoteOptions = listOf(
    DummyVoteOption("Tatte Bakery", 4, 5),
    DummyVoteOption("Saltie Girl",  3, 5),
    DummyVoteOption("Boston Common",2, 5),
    DummyVoteOption("Trident Books",1, 5)
)

/**
 * Single row in the voting poll showing a place name and live vote progress bar.
 *
 * @param name       Place name.
 * @param votes      Current vote count for this option.
 * @param totalVotes Total votes cast across all options.
 * @param isSelected Whether the current user voted for this option.
 * @param onVote     Called when the user taps this row to vote.
 */
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
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(
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

@Preview(showBackground = true)
@Composable
private fun CreateGroupDialogPreview() {
    CreateGroupDialog(onDismiss = {}, onCreateGroup = {})
}