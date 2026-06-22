package com.example.foursquare.ui.voting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foursquare.data.GroupRepository
import com.example.foursquare.data.Vote
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class VotingViewModel(
    private val repo: GroupRepository = GroupRepository()
) : ViewModel() {

    private val uid get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private var groupId = ""

    val votes: StateFlow<List<Vote>> get() = _votes
    private val _votes = MutableStateFlow<List<Vote>>(emptyList())

    fun loadVotes(gId: String) {
        groupId = gId
        repo.observeVotes(gId)
            .onEach { _votes.value = it }
            .launchIn(viewModelScope)
    }

    fun castVote(placeId: String, placeName: String) = viewModelScope.launch {
        repo.castVote(groupId, uid, placeId, placeName)
    }
}