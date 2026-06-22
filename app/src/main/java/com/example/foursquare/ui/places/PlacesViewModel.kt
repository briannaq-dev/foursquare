package com.example.foursquare.ui.places

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foursquare.data.PlacesRepository
import com.example.foursquare.data.SavedPlace
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PlacesViewModel : ViewModel() {

    private val uid get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private val repo get() = PlacesRepository(uid)

    val savedPlaces: StateFlow<List<SavedPlace>> = repo.observeSavedPlaces()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun savePlace(place: SavedPlace) = viewModelScope.launch {
        repo.savePlace(place)
    }

    fun removePlace(placeId: String) = viewModelScope.launch {
        repo.removePlace(placeId)
    }
}