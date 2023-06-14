package hr.algebra.barky.model

object FirestoreDatabase {

    const val APP_NAME: String="WatchDog"

    //Collections
    const val PACKAGE = "Package"
    const val USER = "User"
    const val DOG = "Dog"
    const val HASH_TAG = "HashTag"
    const val FILES = "UploadedFile"
    const val USER_FILES="UserFile"

    //Fields
    const val MAIL="mail"
    const val USER_ID="userId"
    const val FILE_ID="fileId"
    const val BREED_NAME="breedName"

    //Storage
    const val IMAGE_FOLDER="images"
    const val IMAGE_EXT=".png"
}