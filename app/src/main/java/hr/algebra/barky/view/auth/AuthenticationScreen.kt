package hr.algebra.barky.view.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FaceRetouchingOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import hr.algebra.barky.R
import hr.algebra.barky.repository.firebase.AuthenticationRepository
import hr.algebra.barky.state.AuthenticationState
import hr.algebra.barky.state.PackageState
import hr.algebra.barky.view.common.AnimatedIconButton
import hr.algebra.barky.view.nav.Graph

@Composable
fun AuthenticationScreen(
    authenticationState: AuthenticationState,
    packagesState : PackageState,
    icon: Int, // resource!
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    isLogin: Boolean = true,
    navController : NavHostController
) {

    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    val state = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }
    val pack by remember {
        mutableStateOf(packagesState)
    }

    AnimatedVisibility(
        visibleState = state,
        enter = fadeIn() + slideInHorizontally(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioHighBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    ) {

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = stringResource(R.string.authentication)
            )

            Card(
                modifier = modifier
                    .padding(vertical = 50.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                shape = RoundedCornerShape(size = 9.dp),
                border = BorderStroke(width = 1.dp, color = MaterialTheme.colors.primary)
            ) {
                Column(
                    modifier = modifier.padding(all = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(9.dp)
                ) {

                    OutlinedTextField(
                        modifier = modifier.fillMaxWidth(),
                        value = authenticationState.email,
                        onValueChange = onEmailChanged,
                        label = { Text(text = stringResource(id = R.string.email_address)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        isError = !authenticationState.isEmailValid
                    )
                    OutlinedTextField(
                        modifier = modifier.fillMaxWidth(),
                        value = authenticationState.password,
                        onValueChange = onPasswordChanged,
                        label = { Text(text = stringResource(id = R.string.password)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        isError = !authenticationState.isPasswordValid,
                        visualTransformation = PasswordVisualTransformation()
                    )

                    if(!isLogin) {
                        PackageScreen(pack)
                    }

                    AnimatedIconButton(
                        modifier = modifier.fillMaxWidth(),
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = stringResource(id = R.string.person)
                            )
                        },
                        text = {
                            Text(
                                if (isLogin) stringResource(id = R.string.login)
                                else stringResource(
                                    id = R.string.register
                                )
                            )
                        },
                        onClick = if (isLogin) onLogin else onRegister,
                        enabled = authenticationState.isEmailValid && authenticationState.isPasswordValid,
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colors.secondary)
                    )

                    Row(
                        modifier = modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AnimatedIconButton(
                            modifier = modifier,
                            icon = {
                                Icon(
                                    imageVector = Icons.Filled.FaceRetouchingOff,
                                    contentDescription = stringResource(id = R.string.guest)
                                )
                            },
                            text = {
                                Text(text = stringResource(id = R.string.guest))
                            },
                            onClick ={
                                AuthenticationRepository.logout()
                                navController.popBackStack()
                                navController.navigate(Graph.MAIN)
                            },
                            colors = ButtonDefaults.buttonColors(Color.DarkGray),
                            icon_visible = true
                        )

                        AnimatedIconButton(
                            modifier = modifier,
                            icon = {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = stringResource(id = R.string.person)
                                )
                            },
                            text = {
                                Text(
                                    if (isLogin) stringResource(id = R.string.register)
                                    else stringResource(
                                        id = R.string.login
                                    )
                                )
                            },
                            onClick = if (isLogin) onRegister else onLogin,
                        )
                    }

                }


            }

        }

    }
}