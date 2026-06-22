package com.example.foursquare.ui.voting

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foursquare.ui.common.FourSquareTopBar

/**
 * Screen 7 — Voting
 * Full-screen live poll for a group. Members can cast votes and see
 * real-time results from Firestore. The host can lock in the final pick.
 *
 * @param viewModel  Provides real-time vote state and write actions.
 * @param groupId    The Firestore ID of the group whose poll is shown.
 * @param groupName  Display name shown in the top bar.
 * @param onBack     Called when the back arrow is tapped.
 * @param onLockIn   Called with the winning candidate ID when locked in.
 */
@Composable
fun VotingScreen(
    viewModel: VotingViewModel = viewModel(),
    groupId: String,
    groupName: String = "CS Squad",
    onBack: () -> Unit = {},
    onLockIn: (candidateId: String) -> Unit = {}
) {
    // Real-time votes from Firestore
    val votes by viewModel.votes.collectAsState()

    // Load votes for this group when screen opens
    LaunchedEffect(groupId) { viewModel.loadVotes(groupId) }

    // Current user's vote (derived from Firestore data)
    val myVotePlaceId = votes.find {
        it.userId == viewModel.currentUid
    }?.placeId

    val timeLeft = "2h remaining"

    Scaffold(
        topBar = {
            FourSquareTopBar(
                title    = groupName,
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Poll header card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors   = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier          = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector        = Icons.Default.HowToVote,
                            contentDescription = null,
                            tint               = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "Vote on tonight",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                timeLeft,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // Instruction text
            item {
                Text(
                    text  = "Tap a place to cast your vote. The group can see live results.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Vote candidates — derived from real vote data
            val candidateIds = votes.map { it.placeId }.distinct()

            items(candidateIds) { placeId ->
                val placeName  = votes.first { it.placeId == placeId }.placeName
                val voteCount  = votes.count { it.placeId == placeId }
                val isSelected = myVotePlaceId == placeId

                VoteCandidateCard(
                    placeId    = placeId,
                    placeName  = placeName,
                    voteCount  = voteCount,
                    totalVotes = votes.size.coerceAtLeast(1),
                    isSelected = isSelected,
                    onVote     = { viewModel.castVote(placeId, placeName) }
                )
            }

            // Empty state when no votes yet
            if (votes.isEmpty()) {
                item {
                    Box(
                        modifier          = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment  = Alignment.Center
                    ) {
                        Text(
                            "No votes yet — be the first!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Lock in button
            item {
                Spacer(Modifier.height(8.dp))
                val winner = votes.groupBy { it.placeId }
                    .maxByOrNull { it.value.size }?.key

                Button(
                    onClick  = { winner?.let { onLockIn(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    enabled  = myVotePlaceId != null
                ) {
                    Text("Lock in final pick")
                }

                if (myVotePlaceId == null) {
                    Text(
                        "Vote first to enable locking",
                        style    = MaterialTheme.typography.labelSmall,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

/**
 * Card for a single voting candidate showing live vote bar and count.
 *
 * @param placeId    Firestore place ID for this candidate.
 * @param placeName  Display name of the place.
 * @param voteCount  Number of votes this candidate has received.
 * @param totalVotes Total votes cast across all candidates.
 * @param isSelected Whether the current user voted for this candidate.
 * @param onVote     Called when the user taps to vote.
 */
@Composable
private fun VoteCandidateCard(
    placeId: String,
    placeName: String,
    voteCount: Int,
    totalVotes: Int,
    isSelected: Boolean,
    onVote: () -> Unit
) {
    val progress = voteCount.toFloat() / totalVotes

    Card(
        modifier  = Modifier.fillMaxWidth(),
        onClick   = onVote,
        colors    = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        border    = if (isSelected) CardDefaults.outlinedCardBorder() else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 0.dp else 2.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text  = placeName,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text     = "$voteCount/$totalVotes",
                        style    = MaterialTheme.typography.labelSmall,
                        color    = if (isSelected)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
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
private fun VotingScreenPreview() {
    VotingScreen(
        groupId   = "g1",
        groupName = "CS Squad",
        onBack    = {},
        onLockIn  = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun VoteCandidateCardPreview() {
    Column(
        modifier            = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        VoteCandidateCard(
            placeId    = "p1",
            placeName  = "Tatte Bakery",
            voteCount  = 4,
            totalVotes = 5,
            isSelected = true,
            onVote     = {}
        )
        VoteCandidateCard(
            placeId    = "p2",
            placeName  = "Saltie Girl",
            voteCount  = 3,
            totalVotes = 5,
            isSelected = false,
            onVote     = {}
        )
    }
}