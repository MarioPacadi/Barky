package hr.algebra.barky.repository.firebase

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import hr.algebra.barky.model.FirestoreDatabase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


object StorageRepository {

    private val storageRef by lazy {
        Firebase.storage.reference
    }

    fun uploadFile(folderName:String?="", fileUrl: Uri, onSuccess:(String)->Unit, onFailure:()->Unit) {
        val uuid = UUID.randomUUID()
        val fileName = "$uuid"

        val fileRef : StorageReference = if (folderName.isNullOrEmpty()){
            storageRef.child(fileName)
        }
        else{
            storageRef.child(folderName).child(fileName)
        }

        fileRef.putFile(fileUrl)
            .addOnSuccessListener {
                onSuccess(fileName)
            }.addOnFailureListener {
                onFailure()
            }

        //If you want to check what the file type is (example: .txt,.png,etc...)
//        fileRef.metadata.addOnSuccessListener { metadata ->
//            val contentType = metadata.contentType
//            Log.d("Content Type", "Content Type: $contentType")
//        }.addOnFailureListener { exception ->
//            Log.w("Getting Content Type", "Error getting content type", exception)
//        }
    }

    fun getFileUrl(folderName:String?="", fileName: String,onSuccess: (Uri) -> Unit){

        val fileRef : StorageReference = if (folderName.isNullOrEmpty()){
            storageRef.child(fileName)
        }
        else{
            storageRef.child(folderName).child(fileName)
        }
        fileRef.downloadUrl.addOnSuccessListener {
            onSuccess(it)
        }
    }

    fun getAllUrlFiles(folderName:String="", onSuccess: (MutableList<Uri>) -> Unit){
        val fileRef : StorageReference = if (folderName.isEmpty()){
            storageRef
        }
        else{
            storageRef.child(folderName)
        }

        fileRef.listAll()
            .addOnSuccessListener {listResult->
                val urls= mutableListOf<Uri>()
                val urlsTask = mutableListOf<Task<Uri>>()

                for (resultref in listResult.items) {
                    urlsTask.add(resultref.downloadUrl)
                }

                Tasks.whenAllSuccess<Uri>(urlsTask)
                    .addOnSuccessListener {
                        for (downloadUrlTask in it) {
                            urls.add(downloadUrlTask)
                        }
                        onSuccess(urls)
                    }
                    .addOnFailureListener {
                        // Handle error
                    }
            }
            .addOnFailureListener {
                // Handle error
            }

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun downloadFile(folderName:String?="", fileName:String, url: String,onSuccess: () -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            val urlTrue = URL(url)
            val connection = urlTrue.openConnection() as HttpURLConnection
            connection.connect()

            val rootPath: File = if (folderName.isNullOrEmpty()) {
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), FirestoreDatabase.APP_NAME)
            } else {
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), folderName)
            }

            if (!rootPath.exists()) {
                rootPath.mkdirs()
            }

            val localFile: File = if (!hasFileExtension(fileName)) {
                File(rootPath, "$fileName.png")
            } else {
                File(rootPath, fileName)
            }

            val inputStream = connection.inputStream
            inputStream.use { input ->
                FileOutputStream(localFile).use { output ->
                    input.copyTo(output)
                }
            }
        }
        onSuccess()
    }

    fun firebaseDownloadFile(folderName:String?="", fileName: String, onSuccess: () -> Unit, onFailure:()->Unit) {
        val fileRef : StorageReference = if (folderName.isNullOrEmpty()){
            storageRef.child(fileName)
        }
        else{
            storageRef.child(folderName).child(fileName)
        }

        val rootPath: File = if(folderName.isNullOrEmpty()){
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), FirestoreDatabase.APP_NAME)
        }
        else{
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), folderName)
        }

        if (!rootPath.exists()) {
            rootPath.mkdirs()
        }

        fileRef.metadata.addOnSuccessListener { metadata ->
            val contentType = metadata.contentType
            Log.d("Content Type", "Content Type: $contentType")
            saveLocalFile(rootPath,fileRef,fileName,contentType,onSuccess,onFailure)
        }.addOnFailureListener { exception ->
            Log.w("Getting Content Type", "Error getting content type", exception)
        }
    }

    private fun saveLocalFile(
        rootPath: File,
        fileRef: StorageReference,
        fileName: String,
        contentType: String?,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val localFile : File = if (!hasFileExtension(fileName)){
            File(rootPath, "$fileName.${contentType!!.split("/")[1]}")
        }
        else{
            File(rootPath, fileName)
        }

        fileRef.getFile(localFile).addOnSuccessListener {
            onSuccess() //Function to say Task Complete
            //  updateDb(timestamp,localFile.toString(),position);
        }.addOnFailureListener { exception ->
            Log.e("Firebase ", "Local temp file not created $exception")
            onFailure()
        }
    }

    private fun hasFileExtension(fileName: String): Boolean {
        val index = fileName.lastIndexOf(".")
        return index != -1 && index != 0 && index != fileName.length - 1
    }

    fun getFileExtension(fileName: String): String {
        val lastIndexOfDot = fileName.lastIndexOf(".")
        return if (lastIndexOfDot != -1) {
            fileName.substring(lastIndexOfDot )
        } else {
            ""
        }
    }

    private fun getUriExtension(uri: Uri): String? {
        val filePath = uri.path
        val dotIndex = filePath!!.lastIndexOf(".")
        return if (dotIndex != -1) {
            filePath.substring(dotIndex+1)
        } else {
            null
        }
    }

    fun isImageFile(uri: Uri, context: Context): Boolean {
        val mimeType = context.contentResolver.getType(uri)
        return mimeType != null && mimeType.startsWith("image/")
    }

    private fun fetchMimeTypeFromUrl(url:String): String? {
        var type: String? = null
        try {
            val extension = MimeTypeMap.getFileExtensionFromUrl(url)
            if (extension != null) {
                type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            }
        } catch (e: Exception) {
            Log.e("FetchMimeType",e.toString())
        }
        return type
    }
}