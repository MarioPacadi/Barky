package hr.algebra.barky.view.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.paging.ExperimentalPagingApi
import hr.algebra.barky.view.MainScreen
import hr.algebra.barky.viewmodel.AuthenticationViewModel
import hr.algebra.barky.viewmodel.PackageViewModel

@ExperimentalPagingApi
@Composable
fun RootNavGraph(navController: NavHostController) {

    val authenticationViewModel = viewModel<AuthenticationViewModel>()
    val packagesViewModel = viewModel<PackageViewModel>()
    val context= LocalContext.current

    NavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = Graph.AUTH) {
        authNavGraph(navController = navController,authenticationViewModel,packagesViewModel,context)
        composable(route = Graph.MAIN) {
            MainScreen()
        }

    }
}