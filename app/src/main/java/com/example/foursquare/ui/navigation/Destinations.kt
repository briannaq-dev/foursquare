package com.example.foursquare.ui.navigation

import kotlinx.serialization.Serializable

// Authentication:
@Serializable object SignIn
@Serializable object SignUp

// Main tabs:
// Home hub shown after sign-in
@Serializable object Dashboard
@Serializable object Discover
@Serializable object Places
@Serializable object Groups
@Serializable object MapTab
@Serializable object Calendar

// Profile
@Serializable object Profile

// Detail / sub-screens
@Serializable data class PlaceDetail(val placeId: String)
@Serializable data class GroupDetail(val groupId: String)
@Serializable data class Directions(val placeId: String, val placeName: String)
@Serializable data class Voting(val groupId: String, val groupName: String)
