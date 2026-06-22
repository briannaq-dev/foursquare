package com.example.foursquare.data

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class SavedPlace(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val rating: Double = 0.0,
    val savedAt: Long = System.currentTimeMillis()
)

class PlacesRepository(private val uid: String) {
    private val collection = Firebase.firestore
        .collection("users").document(uid).collection("savedPlaces")

    fun observeSavedPlaces(): Flow<List<SavedPlace>> = callbackFlow {
        val reg = collection.addSnapshotListener { snapshot, error ->
            if (error != null) { close(error); return@addSnapshotListener }
            val places = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(SavedPlace::class.java)?.copy(id = doc.id)
            }.orEmpty()
            trySend(places)
        }
        awaitClose { reg.remove() }
    }

    suspend fun savePlace(place: SavedPlace) { collection.add(place).await() }
    suspend fun removePlace(placeId: String) { collection.document(placeId).delete().await() }
}