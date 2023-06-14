package hr.algebra.barky.view.routes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import hr.algebra.barky.R

sealed class TopBarScreen(
    val route: String,
    val title: Int,
    val icon: ImageVector
) {
    object Profile: TopBarScreen(
        route = "profile",
        title = R.string.profile,
        icon = Icons.Default.ManageAccounts
    )
    object Gallery: TopBarScreen(
        route = "gallery",
        title = R.string.gallery,
        icon = Icons.Default.PhotoLibrary
    )
    object AddPhotos: TopBarScreen(
        route = "add_photo",
        title = R.string.addphotos,
        icon = Icons.Default.AddToPhotos
    )
}