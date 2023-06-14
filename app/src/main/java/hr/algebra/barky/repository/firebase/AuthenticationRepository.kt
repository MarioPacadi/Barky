package hr.algebra.barky.repository.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object AuthenticationRepository {

    public val auth by lazy {
        Firebase.auth
    }

    fun logIn(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFail: () -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess()
                } else {
                    onFail()
                    Log.e("LOGIN", "Wrong credentials")
                }
            }
    }

    fun register(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFail: () -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess()
                } else {
                    onFail()
                    Log.e("REGISTER", "Unable to register")
                }
            }
    }

    fun loggedIn(): Boolean {
        return auth.currentUser != null
    }

    fun logout() {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null)
            auth.signOut()
    }

    fun update(
        newEmail: String,
        newPassword: String,
        onSuccess: () -> Unit,
        onFail: () -> Unit
    ) {
        val user = auth.currentUser!!
        user.updateEmail(newEmail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("UpdateEmail", "User email updated.")
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { passwordTask ->
                            if (passwordTask.isSuccessful) {
                                Log.d("UpdatePassword", "User password updated.")
                                onSuccess()
                            } else {
                                Log.d("UpdatePassword", "Error updating password.")
                                onFail()
                            }
                        }
                } else {
                    Log.d("UpdateEmail", "Error updating email.")
                    onFail()
                }
            }
    }
}