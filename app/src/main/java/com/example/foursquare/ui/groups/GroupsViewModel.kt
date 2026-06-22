package com.example.foursquare.ui.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foursquare.data.Group
import com.example.foursquare.data.GroupRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GroupsViewModel(
    private val repo: GroupRepository = GroupRepository()
) : ViewModel() {

    private val uid get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val groups: StateFlow<List<Group>> = repo.observeUserGroups(uid)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun createGroup(name: String) = viewModelScope.launch {
        repo.createGroup(name, uid)
    }

    fun joinGroup(inviteCode: String) = viewModelScope.launch {
        repo.joinGroup(inviteCode, uid)
    }
}