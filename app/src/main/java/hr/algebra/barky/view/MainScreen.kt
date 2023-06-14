package hr.algebra.barky.view

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.paging.ExperimentalPagingApi
import com.google.firebase.auth.FirebaseAuth
import hr.algebra.barky.repository.firebase.AuthenticationRepository
import hr.algebra.barky.util.aspect.BottomNavClick
import hr.algebra.barky.view.nav.BottomNavGraph
import hr.algebra.barky.view.routes.BottomNavScreen
import hr.algebra.barky.view.routes.TopBarScreen

@ExperimentalPagingApi
@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        topBar = { if (AuthenticationRepository.loggedIn()) TopBar(navController = navController) }
    ) {
        it.toString()
        BottomNavGraph(navController = navController)
    }

    DisposableEffect(LocalLifecycleOwner.current) {

        onDispose {
            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser != null) {
                auth.signOut()
            }
        }
    }
}

//https://developer.android.com/jetpack/compose/layouts/material
@Composable
fun TopBar(navController: NavHostController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    TopAppBar(
        title = { Text(text = "Watch dogs") },
        elevation = 8.dp,
//        navigationIcon = {
//            IconButton(onClick = {}) {
//                Icon(
//                    imageVector = Icons.Filled.Menu,
//                    contentDescription = null
//                )
//            }
//        },
        actions = {
            TopBarScreen::class.sealedSubclasses.forEach {
                AddAction(
                    screen = it.objectInstance!!,
                    currentDestination = currentDestination,
                    navController = navController
                )
            }
        }
    )
}

@Composable
fun BottomBar(navController: NavHostController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    BottomNavigation {
        BottomNavScreen::class.sealedSubclasses.forEach {
            AddItem(
                screen = it.objectInstance!!,
                currentDestination = currentDestination,
                navController = navController
            )
        }
    }

}

@Composable
fun RowScope.AddItem(
    screen: BottomNavScreen,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    BottomNavigationItem(
        icon = {
            Icon(
                imageVector = screen.icon,
                contentDescription = stringResource(id = screen.title)
            )
        },
        label = { Text(text = stringResource(id = screen.title)) },
        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
        onClick = @BottomNavClick {
            navController.navigate(screen.route) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
        })
}

@Composable
fun AddAction(
    screen: TopBarScreen,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    IconButton(
        onClick = {
            var selected=currentDestination?.hierarchy?.any { it.route == screen.route } == true
            navController.navigate(screen.route) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
        }) {
        Icon(imageVector = screen.icon, contentDescription = stringResource(id = screen.title))
    }
}
