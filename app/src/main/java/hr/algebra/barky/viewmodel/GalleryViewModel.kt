package hr.algebra.barky.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.algebra.barky.model.Dog
import hr.algebra.barky.model.FirestoreDatabase
import hr.algebra.barky.model.media.HashTag
import hr.algebra.barky.model.media.UploadedFile
import hr.algebra.barky.model.media.User
import hr.algebra.barky.model.media.UserFile
import hr.algebra.barky.repository.firebase.AuthenticationRepository
import hr.algebra.barky.repository.firebase.FirestoreRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class GalleryViewModel : ViewModel(){

    private val _galleryState = mutableStateListOf<UploadedFile>()
    private val _hashTagState= mutableStateListOf<Pair<String,List<HashTag>>>()
    private var _dogsState= mutableStateListOf<Dog>()

    val galleryState: SnapshotStateList<UploadedFile>
        get() = _galleryState
    val hashTagState: SnapshotStateList<Pair<String, List<HashTag>>>
        get() = _hashTagState
    val dogsState: SnapshotStateList<Dog>
        get() = _dogsState

    private val errorHandler= CoroutineExceptionHandler{
            _,e-> Log.e("GALLERY_VIEWMODEL",e.toString(),e)
    }

    init {
        viewModelScope.launch(errorHandler) {
            if (AuthenticationRepository.loggedIn())
            getGalleryData(AuthenticationRepository.auth.currentUser!!.uid)
        }
    }

    private fun getGalleryData(userId: String) = runBlocking {
        FirestoreRepository.getClassFromCollection<UserFile>(FirestoreDatabase.USER_FILES,FirestoreDatabase.USER_ID,userId, User::class.java){ userFiles ->
            userFiles.values.forEach{userFile ->
                //Get UploadedFile
                getUploadedFile(userFile)

                //Get HashTags
                getHashTags(userFile)
            }
        }

        getDogsState(userId)
    }

    fun delete(dog: Dog, userId: String) {
        FirestoreRepository.delete(FirestoreDatabase.DOG,FirestoreDatabase.BREED_NAME,dog.breedName){
            getDogsState(userId)
        }
    }

    private fun getUploadedFile(userFile: UserFile) {
        FirestoreRepository.getClassFromCollection<UploadedFile>(FirestoreDatabase.FILES,FirestoreDatabase.FILE_ID,userFile.fileID, UploadedFile::class.java){ files ->
            files.values.forEach{file ->
                _galleryState.add(file)
            }
        }
    }

    private fun getHashTags(userFile: UserFile) {
        FirestoreRepository.getClassFromCollection<HashTag>(FirestoreDatabase.HASH_TAG,FirestoreDatabase.FILE_ID,userFile.fileID,HashTag::class.java){ hashTags->
            //Pair<FileID,listOf(HashTags)>
            _hashTagState.add(Pair(userFile.userID,hashTags.values.toList()))
        }
    }

    private fun getDogsState(userId: String) {
        FirestoreRepository.getClassFromCollection<Dog>(FirestoreDatabase.DOG,FirestoreDatabase.USER_ID,userId, Dog::class.java){ files ->
            files.values.forEach{file ->
                if (!_dogsState.contains(file)){
                    _dogsState.add(file)
                }
            }
            Log.i("FirestoreDogs",files.values.toString())
        }
    }

    fun refreshDogState(userId: String){
        getDogsState(userId)
    }
}