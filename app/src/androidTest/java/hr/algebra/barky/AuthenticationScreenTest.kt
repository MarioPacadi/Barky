package hr.algebra.barky

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import androidx.paging.ExperimentalPagingApi
import androidx.test.ext.junit.runners.AndroidJUnit4
import hr.algebra.barky.state.AuthenticationState
import hr.algebra.barky.state.PackageState
import hr.algebra.barky.ui.theme.DogsAppTheme
import hr.algebra.barky.view.auth.AuthenticationScreen
import hr.algebra.barky.viewmodel.PackageViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@SuppressLint("ComposableNaming")
@OptIn(ExperimentalPagingApi::class)
@RunWith(AndroidJUnit4::class)
class AuthenticationScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun enterEmailAndPassword_clickLoginButton() {

        // Wait for the activity to be idle
        composeTestRule.waitForIdle()

        var loginClicked = false
        composeTestRule.runOnUiThread {
            composeTestRule.setContent {
                AuthenticationScreen(
                    authenticationState = AuthenticationState(),
                    packagesState = PackageState(),
                    icon = R.drawable.login,
                    onLogin = {loginClicked = true},
                    onRegister = {},
                    onEmailChanged = {},
                    onPasswordChanged = {},
                    isLogin = true,
                    navController = rememberNavController()
                )
            }
        }

        // Enter email and password
        composeTestRule.onNodeWithContentDescription("Email address")
            .performTextInput("test@example.com")

        composeTestRule.onNodeWithContentDescription("Password")
            .performTextInput("y3l10W!@")

        // Click the login button
        composeTestRule.onNodeWithText("Login")
            .performClick()

        Assert.assertTrue(loginClicked)
    }
}
