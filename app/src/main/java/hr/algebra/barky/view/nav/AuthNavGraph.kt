package hr.algebra.barky.view.nav

import android.content.Context
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import hr.algebra.barky.R
import hr.algebra.barky.viewmodel.ProfileViewModel
import hr.algebra.barky.repository.firebase.AuthenticationRepository
import hr.algebra.barky.view.routes.AuthScreen
import hr.algebra.barky.view.auth.AuthenticationScreen
import hr.algebra.barky.viewmodel.AuthenticationViewModel
import hr.algebra.barky.viewmodel.PackageViewModel


fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    authenticationViewModel: AuthenticationViewModel,
    packagesViewModel : PackageViewModel,
    context : Context
) {
    navigation(
        route = Graph.AUTH,
        startDestination = AuthScreen.Login.route
    ) {
        composable(route = AuthScreen.Login.route) {
            AuthenticationScreen(
                icon = R.drawable.login,
                onLogin = {
                    authenticationViewModel.logIn(
                        onSuccess = {
                            navController.popBackStack()
                            navController.navigate(Graph.MAIN)
                        },
                        onFail = {
                            //Toast.makeText(context,context.getString(R.string.unable_to_login),Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                onRegister = {
                    navController.popBackStack()
                    navController.navigate(AuthScreen.Register.route)
                },
                authenticationState = authenticationViewModel.authenticationState.value,
                packagesState=packagesViewModel.packagesState.value,
                onEmailChanged = {authenticationViewModel.onEmailChanged(it)},
                onPasswordChanged = {authenticationViewModel.onPasswordChanged(it)},
                navController = navController
            )
        }
        composable(route = AuthScreen.Register.route) {
            AuthenticationScreen(
                icon = R.drawable.register,
                onLogin = {
                    navController.popBackStack()
                    navController.navigate(AuthScreen.Login.route)
                },
                onRegister = {
                    authenticationViewModel.register(
                        onSuccess = {
                            ProfileViewModel().registerUserData(AuthenticationRepository.auth.currentUser!!.uid,packagesViewModel.packagesState.value)
                            navController.popBackStack()
                            navController.navigate(Graph.MAIN)
                        },
                        onFail = {
                            //Toast.makeText(context,context.getString(R.string.unable_to_register),Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                authenticationState = authenticationViewModel.authenticationState.value,
                packagesState=packagesViewModel.packagesState.value,
                onEmailChanged = {authenticationViewModel.onEmailChanged(it)},
                onPasswordChanged = {authenticationViewModel.onPasswordChanged(it)},
                isLogin = false,
                navController = navController
            )
        }
    }
}