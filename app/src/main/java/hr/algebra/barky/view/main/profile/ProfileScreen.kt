package hr.algebra.barky.view.main.profile

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import hr.algebra.barky.model.utils.TimerData
import hr.algebra.barky.view.common.TimerAnimation
import hr.algebra.barky.view.common.TimerViewAnimation
import hr.algebra.barky.R
import hr.algebra.barky.model.media.User
import hr.algebra.barky.view.nav.Graph
import hr.algebra.barky.view.routes.TopBarScreen
import hr.algebra.barky.viewmodel.TimerViewModel


@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    user: User,
    postCount:Int,
    timerViewModel: TimerViewModel = viewModel(),
    navController: NavHostController
) {

    val profile by remember {
        mutableStateOf(user)
    }

    Card(
        elevation = 6.dp, modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp, bottom = 100.dp, start = 16.dp, end = 16.dp)
            .border(width = 2.dp, color = Color.White, shape = RoundedCornerShape(30.dp))
    ) {
        Column(
            modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.watchdog),
                contentDescription = "husky",
                modifier = modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colors.secondary,
                        shape = CircleShape
                    ),
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(MaterialTheme.colors.secondary)
            )
            Text(text = profile.mail, fontWeight = FontWeight.Bold)
            Text(text = "Package: ${profile.packageID}")

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            {
                ProfileStats(
                    count = profile.consumption.toString(),
                    title = "Upload Limit",
                    timerViewModel,
                    user.package_changed_timer
                )

                //Gallery FileCount
                ProfileStats(
                    count = postCount.toString(),
                    title = "Posts",
                    timerViewModel,
                    user.consumption_timer
                )
            }
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(onClick = {
                    navController.popBackStack()
                    navController.navigate(Graph.EDIT)
                },
                enabled = !profile.package_change
                    ) {
                    Text(text = "Edit profile")
                }

                Button(onClick = {
                    navController.popBackStack()
                    navController.navigate(TopBarScreen.Gallery.route)
                }) {
                    Text(text = "Gallery")
                }
            }


        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ProfileStats(
    count: String,
    title: String,
    timerViewModel: TimerViewModel,
    changedTimer: Long
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = count, fontWeight = FontWeight.Bold)
        Text(title)

        val timerData: TimerData by timerViewModel.timerState.collectAsState()
        TimerViewAnimation(
            timerData = if (changedTimer.toInt()==0) TimerData(0) else timerData,
            timeAnimation = TimerAnimation.NONE,
            backgroundColor = Color.Black,
            textColor = Color.White,
            icon = painterResource(id = R.drawable.ic_baseline_access_time_24),
            iconColor = ColorFilter.tint(color = Color.White)
        )
    }
}