package com.example.foursquare.ui.navigation

import kotlinx.serialization.Serializable

@Serializable object SignIn
@Serializable object SignUp

@Serializable object Discover
@Serializable object Places
@Serializable object Groups
@Serializable object MapTab
@Serializable object Calendar

@Serializable data class PlaceDetail(val placeId: String)
@Serializable data class GroupDetail(val groupId: String)
@Serializable data class Directions(val placeId: String, val placeName: String)
