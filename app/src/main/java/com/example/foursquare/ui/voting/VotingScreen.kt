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
import com.example.foursquare.ui.common.FourSquareTopBar

// Dummy data

private data class VoteCandidate(
    val id: String,
    val name: String,
    val category: String,
    val rating: Double,
    val votes: Int,
    val totalVotes: Int
)

private val dummyCandidates = listOf(
    VoteCandidate("p1", "Tatte Bakery",   "Café",    4.6, 4, 5),
    VoteCandidate("p2", "Saltie Girl",    "Seafood", 4.8, 3, 5),
    VoteCandidate("p3", "Boston Common",  "Park",    4.7, 2, 5),
    VoteCandidate("p4", "Trident Books",  "Books",   4.5, 1, 5)
)

// Screen
/**
 * Screen 7 (alternate) — Voting
 * Full-screen view of the active poll for a group. Users can see live vote counts,
 * cast their vote, and lock in the final pick once voting closes.
 *
 * This screen mirrors the "Vote & Lock" mockup from the proposal and can be reached
 * from [GroupDetailScreen] via an explicit "Open vote" action.
 *
 * @param groupId       The ID of the group whose poll is displayed.
 * @param groupName     Display name shown in the top bar.
 * @param onBack        Called when the back arrow is tapped.
 * @param onLockIn      Called with the winning candidate ID when the host locks the pick.
 */
@Composable
fun VotingScreen(
    groupId: String,
    groupName: String = "CS Squad",
    onBack: () -> Unit = {},
    onLockIn: (candidateId: String) -> Unit = {}
) {
    // TODO: load real vote state from ViewModel / Firestore real-time listener
    var userVote by remember { mutableStateOf<String?>(null) }

    // Simulated countdown
    // TODO: replace with actual expiry timestamp from Firestore
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
            modifier       = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Poll header
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

            // Vote candidates
            items(dummyCandidates) { candidate ->
                VoteCandidateCard(
                    candidate  = candidate,
                    isSelected = userVote == candidate.id,
                    onVote     = {
                        userVote = candidate.id
                        // TODO: write vote to Firestore via ViewModel
                    }
                )
            }

            // Lock in button
            // host only — TODO: gate behind host role check)
            item {
                Spacer(Modifier.height(8.dp))

                val winner = dummyCandidates.maxByOrNull { it.votes }

                Button(
                    onClick  = { winner?.let { onLockIn(it.id) } },
                    modifier = Modifier.fillMaxWidth(),
                    enabled  = userVote != null   // at least the current user voted
                ) {
                    Text("Lock in final pick")
                }

                if (userVote == null) {
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

// Sub-composables

/**
 * Card for a single voting candidate showing its name, category, live vote bar,
 * and vote count.
 *
 * @param candidate  The place being voted on.
 * @param isSelected Whether the current user has voted for this candidate.
 * @param onVote     Called when the user taps this card to vote.
 */
@Composable
private fun VoteCandidateCard(
    candidate: VoteCandidate,
    isSelected: Boolean,
    onVote: () -> Unit
) {
    val progress = if (candidate.totalVotes > 0)
        candidate.votes.toFloat() / candidate.totalVotes
    else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick  = onVote,
        colors   = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) CardDefaults.outlinedCardBorder() else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 0.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text  = candidate.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text  = "${candidate.category} · ⭐ ${candidate.rating}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Vote count badge
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text     = "${candidate.votes}/${candidate.totalVotes}",
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

            // Live vote progress bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// Previews

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
            candidate  = VoteCandidate("p1", "Tatte Bakery", "Café", 4.6, 4, 5),
            isSelected = true,
            onVote     = {}
        )
        VoteCandidateCard(
            candidate  = VoteCandidate("p2", "Saltie Girl", "Seafood", 4.8, 3, 5),
            isSelected = false,
            onVote     = {}
        )
    }
}
