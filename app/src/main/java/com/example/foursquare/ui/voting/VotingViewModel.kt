package com.example.foursquare.ui.voting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foursquare.data.GroupRepository
import com.example.foursquare.data.Vote
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for VotingScreen.
 * Loads real-time votes from Firestore and exposes actions to cast votes.
 *
 * @param repo Data source for group/vote Firestore operations.
 */
class VotingViewModel(
    private val repo: GroupRepository = GroupRepository()
) : ViewModel() {

    private val uid get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    /** Exposed so VotingScreen can compare votes against the current user. */
    val currentUid: String get() = uid

    private var groupId = ""

    private val _votes = MutableStateFlow<List<Vote>>(emptyList())

    /** Real-time list of votes for the current group, updated by Firestore. */
    val votes: StateFlow<List<Vote>> get() = _votes

    /**
     * Starts observing real-time votes for the given group.
     * Called from LaunchedEffect when VotingScreen first opens.
     *
     * @param gId Firestore group document ID.
     */
    fun loadVotes(gId: String) {
        groupId = gId
        repo.observeVotes(gId)
            .onEach { _votes.value = it }
            .launchIn(viewModelScope)
    }

    /**
     * Writes the current user's vote to Firestore.
     * Uses the user's UID as the document ID so each user has exactly one vote.
     *
     * @param placeId   Firestore place ID being voted for.
     * @param placeName Display name stored with the vote for easy display.
     */
    fun castVote(placeId: String, placeName: String) = viewModelScope.launch {
        repo.castVote(groupId, uid, placeId, placeName)
    }
}