package com.example.foursquare.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import com.example.foursquare.ui.auth.SignInScreen
import com.example.foursquare.ui.auth.SignUpScreen
import com.example.foursquare.ui.calendar.CalendarScreen
import com.example.foursquare.ui.common.FourSquareBottomBar
import com.example.foursquare.ui.discover.DiscoverScreen
import com.example.foursquare.ui.groups.GroupDetailScreen
import com.example.foursquare.ui.groups.GroupsScreen
import com.example.foursquare.ui.home.DashboardScreen
import com.example.foursquare.ui.map.DirectionsScreen
import com.example.foursquare.ui.map.MapScreen
import com.example.foursquare.ui.places.PlaceDetailScreen
import com.example.foursquare.ui.places.PlacesScreen
import com.example.foursquare.ui.profile.ProfileScreen
import com.example.foursquare.ui.voting.VotingScreen

// Screens that should show the bottom nav bar
private val bottomNavRoutes = setOf(
    Dashboard::class.qualifiedName,
    Discover::class.qualifiedName,
    Places::class.qualifiedName,
    Groups::class.qualifiedName,
    MapTab::class.qualifiedName,
    Calendar::class.qualifiedName
)

@Composable
fun FourSquareNavigation(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = bottomNavRoutes.any { currentRoute?.contains(it.toString()) == true }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                FourSquareBottomBar(
                    currentRoute = currentRoute,
                    onTabSelected = { route ->
                        navController.navigate(route) {
                            popUpTo(Dashboard) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = SignIn,
            modifier         = Modifier.padding(innerPadding)
        ) {
            // Authentication
            composable<SignIn> {
                SignInScreen(
                    onSignInClick  = { navController.navigate(Dashboard) },
                    onSignUpClick  = { navController.navigate(SignUp) },
                    onGoogleSignIn = { navController.navigate(Dashboard) }
                )
            }
            composable<SignUp> {
                SignUpScreen(
                    onCreateAccount = { navController.navigate(Dashboard) },
                    onBack          = { navController.popBackStack() }
                )
            }

            // Main tabs
            composable<Dashboard> {
                DashboardScreen(
                    onGroupClick   = { navController.navigate(GroupDetail(it)) },
                    onSeeAllGroups = { navController.navigate(Groups) },
                    onPlanClick    = { /* TODO: navigate to event detail */ },
                    onProfileClick = { navController.navigate(Profile) }
                )
            }
            composable<Discover> {
                DiscoverScreen(onPlaceClick = { navController.navigate(PlaceDetail(it)) })
            }
            composable<Places> {
                PlacesScreen(onPlaceClick = { navController.navigate(PlaceDetail(it)) })
            }
            composable<Groups> {
                GroupsScreen(onGroupClick = { navController.navigate(GroupDetail(it)) })
            }
            composable<MapTab> {
                MapScreen(onOpenDirections = { id, name ->
                    navController.navigate(Directions(id, name))
                })
            }
            composable<Calendar> {
                CalendarScreen()
            }

            // Profile
            composable<Profile> {
                ProfileScreen(
                    onPlaceClick  = { navController.navigate(PlaceDetail(it)) },
                    onSignOut     = {
                        navController.navigate(SignIn) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onEditProfile = { /* TODO: edit profile flow */ }
                )
            }

            // Detail / sub-screens
            composable<PlaceDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<PlaceDetail>()
                PlaceDetailScreen(
                    placeId = route.placeId,
                    onBack  = { navController.popBackStack() }
                )
            }
            composable<GroupDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<GroupDetail>()
                GroupDetailScreen(
                    groupId = route.groupId,
                    onBack  = { navController.popBackStack() }
                )
            }
            composable<Directions> { backStackEntry ->
                val route = backStackEntry.toRoute<Directions>()
                DirectionsScreen(
                    placeId   = route.placeId,
                    placeName = route.placeName,
                    onBack    = { navController.popBackStack() }
                )
            }
            composable<Voting> { backStackEntry ->
                val route = backStackEntry.toRoute<Voting>()
                VotingScreen(
                    groupId   = route.groupId,
                    groupName = route.groupName,
                    onBack    = { navController.popBackStack() },
                    onLockIn  = { navController.popBackStack() }
                )
            }
        }
    }
}
