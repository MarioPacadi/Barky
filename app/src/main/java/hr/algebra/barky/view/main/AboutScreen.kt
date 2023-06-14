package hr.algebra.barky.view.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import hr.algebra.barky.view.common.BlinkingText
import hr.algebra.barky.R
import hr.algebra.barky.repository.firebase.AuthenticationRepository

@Composable
fun AboutScreen(modifier: Modifier = Modifier) {

    val state = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    AnimatedVisibility(
        visibleState = state,
        enter = fadeIn(
            animationSpec = tween(3000)
        )
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.watchdog),
                contentDescription = stringResource(R.string.cinema),
                colorFilter = ColorFilter.tint(MaterialTheme.colors.secondary)
            )
            BlinkingText(
                modifier = modifier.padding(bottom = 50.dp),
                text = stringResource(R.string.made),
                style = typography.h3,
                duration = 1000
            )
            if (AuthenticationRepository.loggedIn()){
                Text(
                    text = AuthenticationRepository.auth.currentUser!!.email.toString(),
                    fontFamily = FontFamily.Cursive,
                )
            }
        }
    }
}