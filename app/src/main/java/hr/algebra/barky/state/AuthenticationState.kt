package hr.algebra.barky.state

data class AuthenticationState(
    var email: String = "",
    val password: String = "",
    val isEmailValid: Boolean = false,
    val isPasswordValid: Boolean = false,
)
