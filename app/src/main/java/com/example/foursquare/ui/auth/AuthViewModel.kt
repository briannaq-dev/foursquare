package com.example.foursquare.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class AuthState {
    data object Loading   : AuthState()
    data object SignedOut : AuthState()
    data class  SignedIn(val uid: String, val email: String?) : AuthState()
}

class AuthViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    val authState: StateFlow<AuthState> = repo.observeAuthState()
        .map { user ->
            if (user == null) AuthState.SignedOut
            else AuthState.SignedIn(user.uid, user.email)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AuthState.Loading)

    var errorMessage = MutableStateFlow<String?>(null)
        private set

    fun signIn(email: String, password: String) = viewModelScope.launch {
        repo.signIn(email, password).onFailure { errorMessage.value = it.message }
    }

    fun signUp(email: String, password: String) = viewModelScope.launch {
        repo.signUp(email, password).onFailure { errorMessage.value = it.message }
    }

    fun signOut() = repo.signOut()
    fun clearError() { errorMessage.value = null }
}