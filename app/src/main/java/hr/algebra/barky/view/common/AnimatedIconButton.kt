package hr.algebra.barky.view.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun AnimatedIconButton(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        MaterialTheme.colors.primary
    ),
    icon_visible : Boolean=false
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val isPressed by interactionSource.collectIsPressedAsState()
    Button(
        modifier = modifier,
        colors = colors,
        enabled = enabled,
        interactionSource = interactionSource,
        onClick = onClick) {
        AnimatedVisibility(visible = isPressed || icon_visible) {
            Row{
                icon()
                Spacer(modifier = modifier.size(ButtonDefaults.IconSpacing))
            }
        }
        text()
    }
}