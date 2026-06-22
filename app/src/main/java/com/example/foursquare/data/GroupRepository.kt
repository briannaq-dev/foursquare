package com.example.foursquare.data

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class Group(
    val id: String = "",
    val name: String = "",
    val memberIds: List<String> = emptyList(),
    val createdBy: String = "",
    val inviteCode: String = ""
)

data class Vote(
    val userId: String = "",
    val placeId: String = "",
    val placeName: String = ""
)

class GroupRepository {
    private val db = Firebase.firestore

    // Observe all groups where current user is a member
    fun observeUserGroups(uid: String): Flow<List<Group>> = callbackFlow {
        val reg = db.collection("groups")
            .whereArrayContains("memberIds", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val groups = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Group::class.java)?.copy(id = doc.id)
                }.orEmpty()
                trySend(groups)
            }
        awaitClose { reg.remove() }
    }

    suspend fun createGroup(name: String, createdBy: String): String {
        val code = (100000..999999).random().toString()
        val group = hashMapOf(
            "name" to name,
            "memberIds" to listOf(createdBy),
            "createdBy" to createdBy,
            "inviteCode" to code
        )
        val ref = db.collection("groups").add(group).await()
        return ref.id
    }

    suspend fun joinGroup(inviteCode: String, uid: String) {
        val snapshot = db.collection("groups")
            .whereEqualTo("inviteCode", inviteCode)
            .get().await()
        val doc = snapshot.documents.firstOrNull() ?: return
        val members = (doc.get("memberIds") as? List<*>)?.toMutableList() ?: mutableListOf()
        if (!members.contains(uid)) {
            members.add(uid)
            doc.reference.update("memberIds", members).await()
        }
    }

    // Observe real-time votes for a group
    fun observeVotes(groupId: String): Flow<List<Vote>> = callbackFlow {
        val reg = db.collection("groups").document(groupId)
            .collection("votes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val votes = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Vote::class.java)
                }.orEmpty()
                trySend(votes)
            }
        awaitClose { reg.remove() }
    }

    suspend fun castVote(groupId: String, userId: String, placeId: String, placeName: String) {
        db.collection("groups").document(groupId)
            .collection("votes").document(userId)
            .set(mapOf("userId" to userId, "placeId" to placeId, "placeName" to placeName))
            .await()
    }
}