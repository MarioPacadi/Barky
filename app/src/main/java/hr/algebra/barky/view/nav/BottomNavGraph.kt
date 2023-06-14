package hr.algebra.barky.view.nav

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.paging.ExperimentalPagingApi
import hr.algebra.barky.viewmodel.AuthenticationViewModel
import hr.algebra.barky.viewmodel.DogsViewModel
import hr.algebra.barky.viewmodel.GalleryViewModel
import hr.algebra.barky.viewmodel.MapViewModel
import hr.algebra.barky.viewmodel.PackageViewModel
import hr.algebra.barky.R
import hr.algebra.barky.repository.firebase.AuthenticationRepository
import hr.algebra.barky.state.DogsState
import hr.algebra.barky.view.main.*
import hr.algebra.barky.view.main.gallery.GalleryScreen
import hr.algebra.barky.view.main.gallery.UploadFileScreen
import hr.algebra.barky.view.main.profile.EditProfileScreen
import hr.algebra.barky.view.main.profile.ProfileScreen
import hr.algebra.barky.view.routes.BottomNavScreen
import hr.algebra.barky.view.routes.TopBarScreen
import hr.algebra.barky.viewmodel.*

@ExperimentalPagingApi
@Composable
fun BottomNavGraph(navController: NavHostController) {

    val context = LocalContext.current
    val authenticationViewModel = hiltViewModel<AuthenticationViewModel>()
    val packagesViewModel = hiltViewModel<PackageViewModel>()
    var profileViewModel = hiltViewModel<ProfileViewModel>()
    val galleryViewModel = hiltViewModel<GalleryViewModel>()

    NavHost(
        navController = navController,
        startDestination = BottomNavScreen.Dogs.route
    ) {
        composable(route = BottomNavScreen.Dogs.route) {
            val dogsViewModel = hiltViewModel<DogsViewModel>()
            val dogsState = DogsState(dogsViewModel)
            DogsScreen(
                dogsState = dogsState,
                user = profileViewModel.profileState.value,
                onUpdate = {
                    dogsViewModel.update(
                        if (it.liked) it.copy(liked = false) else it.copy(
                            liked = true
                        )
                    )
                },
                onDelete = { dogsViewModel.delete(it) }
            )
        }
        composable(route = BottomNavScreen.About.route) {
            AboutScreen()
        }
        composable(route = BottomNavScreen.Map.route) {
            val mapViewModel = hiltViewModel<MapViewModel>()
            MapScreen(mapState = mapViewModel.mapState.value)
        }
        composable(route = TopBarScreen.AddPhotos.route) {
            UploadFileScreen(
                user = profileViewModel.profileState.value
            ) {
                navController.popBackStack()
                navController.navigate(TopBarScreen.Gallery.route)
            }
        }
        composable(route = TopBarScreen.Profile.route) {
            profileViewModel.getProfileData(AuthenticationRepository.auth.currentUser!!.uid)
            ProfileScreen(user = profileViewModel.profileState.value, postCount = profileViewModel.postCount.value, navController = navController)
        }
        composable(route = TopBarScreen.Gallery.route) {
            galleryViewModel.refreshDogState(AuthenticationRepository.auth.currentUser!!.uid)
            GalleryScreen(
                galleryState=galleryViewModel.galleryState,
                hashTagState=galleryViewModel.hashTagState,
                dogsState=galleryViewModel.dogsState,
                user = profileViewModel.profileState.value,
                onUpdate = {
                    //galleryViewModel.update(it)
                },
                onDelete = {
                    galleryViewModel.delete(dog=it,userId=AuthenticationRepository.auth.currentUser!!.uid)
                })
        }

        composable(route = Graph.EDIT) {
            val authState = authenticationViewModel.authenticationState.value
            val packageState = packagesViewModel.packagesState.value
            val userState = profileViewModel.profileState.value

            EditProfileScreen(
                authenticationState = authState,
                packagesState = packageState,
                userState = userState,
                icon = R.drawable.update,
                onUpdate = {
                    authenticationViewModel.update(
                        onSuccess = {
                            ProfileViewModel().updateUserData(
                                profileViewModel.profileState.value,
                                packageState,
                                authState
                            )
                            navController.popBackStack()
                            navController.navigate(TopBarScreen.Profile.route)
                        },
                        onFail = {
                            Toast.makeText(
                                context,
                                context.getString(R.string.unable_to_update),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                },
                onEmailChanged = { authenticationViewModel.onEmailChanged(it) },
                onPasswordChanged = { authenticationViewModel.onPasswordChanged(it) },
            )
        }
    }
}