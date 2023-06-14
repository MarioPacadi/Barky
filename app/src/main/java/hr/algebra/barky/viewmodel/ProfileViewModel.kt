package hr.algebra.barky.viewmodel

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.algebra.barky.model.FirestoreDatabase
import hr.algebra.barky.model.media.User
import hr.algebra.barky.repository.firebase.AuthenticationRepository
import hr.algebra.barky.repository.firebase.FirestoreRepository
import hr.algebra.barky.state.AuthenticationState
import hr.algebra.barky.state.PackageState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Calendar

class ProfileViewModel : ViewModel() {

    private val _profileState = mutableStateOf(User())
    private var _postCount= mutableStateOf(0)

    val profileState: State<User>
        get() = _profileState
    val postCount: State<Int>
        get() = _postCount

    private val errorHandler= CoroutineExceptionHandler{
            _,e-> Log.e("PROFILE_VIEWMODEL",e.toString(),e)
    }

    init {
        viewModelScope.launch(errorHandler) {
            if (AuthenticationRepository.loggedIn())
            getProfileData(AuthenticationRepository.auth.currentUser!!.uid)
        }
    }

    fun getProfileData(userId: String) = runBlocking {

        FirestoreRepository.getClassFromCollection<User>(FirestoreDatabase.USER,FirestoreDatabase.USER_ID,userId, User::class.java){hashmap->
                val user=hashmap.values.firstOrNull()
                user?.let {
                    updateProfileState(it)
                }
                Log.w("GotUser",user.toString())
        }

        FirestoreRepository.getCountOfDocuments(FirestoreDatabase.DOG,FirestoreDatabase.USER_ID,userId){
            _postCount= mutableStateOf(it)
        }

    }

    fun registerUserData(userId:String, packageState: PackageState){
        val pack=packageState.selected
        val mail=AuthenticationRepository.auth.currentUser!!.email

        val user=User(
            userId=userId,
            mail=mail.toString(),
            isAdmin = false,
            packageID = pack.Name!!,
            consumption = pack.Upload_Limit!!.toLong(),
            package_change = false,
            consumption_timer = 0,
            package_changed_timer = 0
        )

        FirestoreRepository.checkIfValueExists(FirestoreDatabase.USER,FirestoreDatabase.USER_ID,userId){ userExists->
            if (!userExists){
                FirestoreRepository.addClassToCollection(FirestoreDatabase.USER,user){
                    updateProfileState(user)
                }
            }
        }

    }

    fun updateUserData(user: User, packageState: PackageState, authState: AuthenticationState) {
        val pack=packageState.selected
        val newEmail=authState.email

        val updatedUser=User(
            userId=user.userId,
            mail=newEmail,
            isAdmin = user.isAdmin,
            packageID = pack.Name!!,
            consumption = _profileState.value.consumption,
            package_change = true,
            consumption_timer = 0,
            package_changed_timer = Calendar.getInstance().timeInMillis
        )

        FirestoreRepository.update(FirestoreDatabase.USER,FirestoreDatabase.USER_ID,updatedUser.userId,user){
            Log.i("User Updated:",it.toString())
            getProfileData(AuthenticationRepository.auth.currentUser!!.uid)
        }
    }

    fun updateUserData(editedUser: User) {
        val userId=editedUser.userId

        FirestoreRepository.update(FirestoreDatabase.USER,FirestoreDatabase.USER_ID,userId,editedUser){
            Log.i("User Updated:",it.toString())
            getProfileData(AuthenticationRepository.auth.currentUser!!.uid)
        }
    }

    private fun updateProfileState(user: User) {
        _profileState.value=_profileState.value.copy(
            userId=user.userId,
            mail = user.mail,
            isAdmin = user.isAdmin,
            packageID = user.packageID,
            package_change = user.package_change,
            package_changed_timer = user.package_changed_timer,
            consumption = user.consumption,
            consumption_timer = user.consumption_timer,
        )
    }

}