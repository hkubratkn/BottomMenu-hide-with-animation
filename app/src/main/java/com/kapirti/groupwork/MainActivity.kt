package com.kapirti.groupwork

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.kapirti.groupwork.ui.theme.GroupWorkTheme
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.LocalOverScrollConfiguration
import androidx.compose.foundation.gestures.OverScrollConfiguration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.res.painterResource

@ExperimentalAnimationApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BottomBarAnimationApp()
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun BottomBarAnimationApp() {

    // State of bottomBar, set state to false, if current page route is "car_details"
    val bottomBarState = rememberSaveable { (mutableStateOf(true)) }

    // State of topBar, set state to false, if current page route is "car_details"
    val topBarState = rememberSaveable { (mutableStateOf(true)) }

    GroupWorkTheme {
        val navController = rememberNavController()

        // Subscribe to navBackStackEntry, required to get current route
        val navBackStackEntry by navController.currentBackStackEntryAsState()

        // Control TopBar and BottomBar
        when (navBackStackEntry?.destination?.route) {
            "cars" -> {
                // Show BottomBar and TopBar
                bottomBarState.value = true
                topBarState.value = true
            }
            "bikes" -> {
                // Show BottomBar and TopBar
                bottomBarState.value = true
                topBarState.value = true
            }
            "settings" -> {
                // Show BottomBar and TopBar
                bottomBarState.value = true
                topBarState.value = true
            }
            "car_details" -> {
                // Hide BottomBar and TopBar
                bottomBarState.value = false
                topBarState.value = false
            }
        }

        // IMPORTANT, Scaffold from Accompanist, initialized in build.gradle.
        // We use Scaffold from Accompanist, because we need full control of paddings, for example
        // in default Scaffold from Compose we can't disable padding for content from top if we
        // have TopAppBar. In our case it's required because we have animation for TopAppBar,
        // content should be under TopAppBar and we manually control padding for each pages.
        com.google.accompanist.insets.ui.Scaffold(
            bottomBar = {
                BottomBar(
                    navController = navController,
                    bottomBarState = bottomBarState
                )
            },
            topBar = {
                TopBar(
                    navController = navController,
                    topBarState = topBarState
                )
            },
            content = {
                NavHost(
                    navController = navController,
                    startDestination = NavigationItem.StartScreen.route,
                ) {
                    composable(NavigationItem.Cars.route) {
                        // show BottomBar and TopBar
                        LaunchedEffect(Unit) {
                            bottomBarState.value = true
                            topBarState.value = true
                        }
                        CarsScreen(
                            navController = navController,
                        )
                    }
                    composable(NavigationItem.Bikes.route) {
                        // show BottomBar and TopBar
                        LaunchedEffect(Unit) {
                            bottomBarState.value = true
                            topBarState.value = true
                        }
                        BikesScreen(
                            navController = navController
                        )
                    }
                    composable(NavigationItem.Settings.route) {
                        // show BottomBar and TopBar
                        LaunchedEffect(Unit) {
                            bottomBarState.value = true
                            topBarState.value = true
                        }
                        SettingsScreen(
                            navController = navController,
                        )
                    }
                    composable(NavigationItem.CarDetails.route) {
                        // hide BottomBar and TopBar
                        LaunchedEffect(Unit) {
                            bottomBarState.value = false
                            topBarState.value = false
                        }
                        CarDetailsScreen(
                            navController = navController,
                        )
                    }
                    composable(NavigationItem.StartScreen.route){
                        LaunchedEffect(Unit){
                            bottomBarState.value = false
                            topBarState.value = false
                        }
                        StartScreen(navController = navController)
                    }
                }
            }
        )
    }
}

@Composable
fun StartScreen(navController: NavController){
    Text(text="startscreen")
    Button(
        onClick={
            navController.navigate("car_details")
        }
    ){
        Text(text="go on")
    }
}


@ExperimentalAnimationApi
@Composable
fun BottomBar(navController: NavController, bottomBarState: MutableState<Boolean>) {
    val items = listOf(
        NavigationItem.Cars,
        NavigationItem.Bikes,
        NavigationItem.Settings
    )

    AnimatedVisibility(
        visible = bottomBarState.value,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        content = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    BottomNavigationItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = item.title
                            )
                        },
                        label = { Text(text = item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    )
}

@ExperimentalAnimationApi
@Composable
fun TopBar(navController: NavController, topBarState: MutableState<Boolean>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val title: String = when (navBackStackEntry?.destination?.route ?: "cars") {
        "cars" -> "Cars"
        "bikes" -> "Bikes"
        "settings" -> "Settings"
        "car_details" -> "Cars"
        else -> "Cars"
    }

    AnimatedVisibility(
        visible = topBarState.value,
        enter = slideInVertically(initialOffsetY = { -it }),
        exit = slideOutVertically(targetOffsetY = { -it }),
        content = {
            TopAppBar(
                title = { Text(text = title) },
            )
        }
    )
}


@Composable
fun BikesScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = {
            Text(text = "BIKES SCREEN")
        }
    )
}

@Composable
fun CarDetailsScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = {
            Text(text = "CAR DETAILS SCREEN")
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CarsScreen(navController: NavController) {
    // Use CompositionLocalProvider to create custom OverScrollConfiguration with vertical padding,
    // otherwise glow effect fill be under TopBar and BottomBar
    CompositionLocalProvider(
        LocalOverScrollConfiguration provides OverScrollConfiguration(
            drawPadding = PaddingValues(vertical = 56.dp)
        ),
        content = {
            LazyColumn(
                // Vertical content padding is 64dp, because 56dp is height of TopBar and BottomBar + 8dp visual padding
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 64.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = {
                    //Title text
                    item {
                        Text(text = "CARS SCREEN")
                    }

                    // 20 cards with content
                    items(20) {
                        CarCard(
                            navController = navController,
                            name = "Car card number is $it"
                        )
                    }
                }
            )
        }
    )
}

@Composable
fun CarCard(navController: NavController, name: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.Cyan,
        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                content = {
                    Text(
                        text = name
                    )
                    Button(
                        onClick = { navController.navigate("car_details") },
                        content = { Text(text = "Open car details page") }
                    )
                }
            )
        }
    )
}

@Composable
fun SettingsScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = {
            Text(text = "SETTINGS SCREEN")
        }
    )
}

sealed class NavigationItem(var route: String, var icon: Int, var title: String) {
    object Cars : NavigationItem("cars", R.drawable.ic_launcher_foreground, "Cars")
    object Bikes : NavigationItem("bikes", R.drawable.ic_launcher_background, "Bikes")
    object Settings : NavigationItem("settings", R.drawable.ic_launcher_foreground, "Settings")
    object CarDetails : NavigationItem("car_details", R.drawable.ic_launcher_background, "Car details")
    object StartScreen : NavigationItem("start_screen", R.drawable.ic_launcher_background, "Start Screen")
}