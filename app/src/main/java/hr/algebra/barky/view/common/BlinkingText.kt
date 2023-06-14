package hr.algebra.barky.view.common

import androidx.compose.animation.core.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.ExperimentalUnitApi

@OptIn(ExperimentalUnitApi::class)
@Composable
fun BlinkingText(
    text: String,
    duration: Int,
    style: TextStyle,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Center
) {
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = duration
            },
            repeatMode = RepeatMode.Reverse
        )
    )

    Text(
        modifier = modifier.alpha(alpha),
        text = text,
        style = style,
        textAlign = textAlign
    )
}