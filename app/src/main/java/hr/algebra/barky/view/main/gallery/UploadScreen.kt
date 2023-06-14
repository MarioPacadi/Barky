package hr.algebra.barky.view.main.gallery

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import hr.algebra.barky.model.Dog
import hr.algebra.barky.model.FirestoreDatabase
import hr.algebra.barky.model.media.HashTag
import hr.algebra.barky.model.media.UploadedFile
import hr.algebra.barky.model.media.User
import hr.algebra.barky.model.media.UserFile
import hr.algebra.barky.repository.firebase.FirestoreRepository
import hr.algebra.barky.repository.firebase.StorageRepository
import hr.algebra.barky.util.getCurrentTime
import java.util.UUID

//https://github.com/ahmedelbagory332/Compose-ChatApp/blob/master/data/src/main/java/com/example/data/repository/MessageRepositoryImpl.kt
//https://github.com/alexmamo/CloudStorageJetpackCompose

//NOTE: THIS FUNCTION ONLY UPLOADS 1 IMAGE
//Famous Dogs: https://www.greenfieldpuppies.com/blog/dog-breeds-of-famous-dogs/

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun UploadFileScreen(modifier: Modifier = Modifier, user: User, uploadSuccess: () -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState(0)
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var imageUrl by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var fileName by remember { mutableStateOf<String?>(null) }
    var newHashTag by remember { mutableStateOf<String>("") }
    var description by remember { mutableStateOf<String>("") }
    val hashTags by remember {
        mutableStateOf(mutableListOf<String>())
    }

    var dogName by remember { mutableStateOf<String>("") }
    var dogType by remember { mutableStateOf<String>("") }
    var origin by remember { mutableStateOf<String>("") }
    var weight by remember { mutableStateOf<Double>(0.0) }
    var height by remember { mutableStateOf<Double>(0.0) }

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUrl = uri
            Log.i("FileUri", uri.toString())
        }

    fun startUploadOfFile() {
        StorageRepository.uploadFile(
            FirestoreDatabase.IMAGE_FOLDER,
            imageUrl!!,
            onSuccess = { fileId ->
                fileName = fileId

                StorageRepository.getFileUrl(FirestoreDatabase.IMAGE_FOLDER, fileId) { fileUrl ->
                    val file =
                        UploadedFile(fileName, fileUrl.toString(), getCurrentTime(), description)
                    FirestoreRepository.addClassToCollection(FirestoreDatabase.FILES, file)

                    val userFile = UserFile(user.userId, fileName!!)
                    FirestoreRepository.addClassToCollection(FirestoreDatabase.USER_FILES, userFile)

                    hashTags.forEach { tag ->
                        val hashTag = HashTag(UUID.randomUUID().toString(), tag, fileName!!)
                        FirestoreRepository.addClassToCollection(
                            FirestoreDatabase.HASH_TAG,
                            hashTag
                        )
                    }

                    FirestoreRepository.getCountOfDocuments(FirestoreDatabase.DOG){count->
                        val dog=Dog(
                            id=count,
                            dogId = if (count==0) 1 else count,
                            breedName = dogName,
                            breedType = dogType,
                            breedDescription = description,
                            origin = origin,
                            minHeightInches = height,
                            minWeightPounds = weight,
                            imgThumb = fileUrl.toString(),
                            userId = user.userId,
                            date_of_upload = getCurrentTime(),
                            furColor = hashTags.toString(),
                            imgAttribution = null,
                            imgCreativeCommons = null,
                            imgSourceURL = null,
                            maxHeightInches = null,
                            maxLifeSpan = null,
                            maxWeightPounds = null,
                            minLifeSpan = null
                        )
                        FirestoreRepository.addClassToCollection(FirestoreDatabase.DOG, dog)
                    }

                    Toast.makeText(context, "Upload successful", Toast.LENGTH_LONG).show()
                }
            },
            onFailure = {
                Toast.makeText(context, "Failed to upload", Toast.LENGTH_LONG).show()
            })
    }

    Column(
        modifier = modifier
            .verticalScroll(scrollState, true),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        if (imageUrl != null) {
            bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, imageUrl)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, imageUrl!!)
                ImageDecoder.decodeBitmap(source)
            }
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = "nullll",
                modifier = modifier.size(400.dp)
            )
        }
        Spacer(modifier = modifier.padding(20.dp))
        Button(
            onClick = {
                launcher.launch("image/*")
            }
        ) {
            Text(text = "Select Image!")
        }
        Spacer(modifier = modifier.padding(20.dp))
        if (imageUrl != null) {

            //region DogSpecificInfo
            OutlinedTextField(
                value = dogName,
                onValueChange = { newValue ->
                    dogName = newValue
                },
                label = { Text(text = "Dog name") },
                placeholder = { Text("Write name of dog") },
                singleLine = true,
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.moveFocus(FocusDirection.Down)
                        keyboardController?.hide()
                    }
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text
                ),
            )
            Spacer(modifier = modifier.padding(20.dp))

            OutlinedTextField(
                value = dogType,
                onValueChange = { newValue ->
                    dogType = newValue
                },
                label = { Text(text = "Dog Type") },
                placeholder = { Text("Write type of dog") },
                singleLine = true,
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.moveFocus(FocusDirection.Down)
                        keyboardController?.hide()
                    }
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text
                ),
            )
            Spacer(modifier = modifier.padding(20.dp))

            OutlinedTextField(
                value = origin,
                onValueChange = { newValue ->
                    origin = newValue
                },
                label = { Text(text = "Origin") },
                placeholder = { Text("Write origin of dog") },
                singleLine = true,
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.moveFocus(FocusDirection.Down)
                        keyboardController?.hide()
                    }
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text
                ),
            )
            Spacer(modifier = modifier.padding(20.dp))

            Row(horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                OutlinedTextField(
                    value = weight.toString(),
                    onValueChange = { newValue ->
                        weight = newValue.toDouble()
                    },
                    label = { Text(text = "Weight") },
                    placeholder = { Text("Write weight of dog") },
                    singleLine = true,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.moveFocus(FocusDirection.Down)
                            keyboardController?.hide()
                        }
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Number
                    ),
                )
                Spacer(modifier = modifier.padding(10.dp))
                OutlinedTextField(
                    value = height.toString(),
                    onValueChange = { newValue ->
                        height = newValue.toDouble()
                    },
                    label = { Text(text = "Height") },
                    placeholder = { Text("Write height of dog") },
                    singleLine = true,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.moveFocus(FocusDirection.Down)
                            keyboardController?.hide()
                        }
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Number
                    ),
                )
            }
            //endregion

            OutlinedTextField(
                value = description,
                onValueChange = { newValue ->
                    description = newValue
                },
                label = { Text(text = "Description") },
                placeholder = { Text("Write description for image") },
                singleLine = true,
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.moveFocus(FocusDirection.Down)
                        keyboardController?.hide()
                    }
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text
                ),
            )
            Spacer(modifier = modifier.padding(20.dp))
            OutlinedTextField(
                value = newHashTag,
                onValueChange = { newValue ->
                    newHashTag = newValue
                },
                label = { Text(text = "Add Hash Tag") },
                placeholder = { Text("Write hash tag...") },
                singleLine = true,
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.moveFocus(FocusDirection.Down)
                        if (newHashTag.isNotEmpty() && !hashTags.contains(newHashTag)) {
                            hashTags.add(newHashTag)
                        }
                        newHashTag = ""
                        keyboardController?.hide()
                    }
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Text
                ),
            )
            Spacer(modifier = modifier.padding(20.dp))
            if (hashTags.isNotEmpty()) {
                Text(text = "Hash Tags: ")
                OutlinedTextField(
                    modifier = modifier,
                    value = hashTags.toString(),
                    onValueChange = { hashTags.toString() },
                    label = { Text("Hash Tags") },
                    readOnly = true,
                    enabled = false
                )
            }
            Spacer(modifier = modifier.padding(20.dp))
            Button(
                onClick = {
                    startUploadOfFile()
                    uploadSuccess()
                }
            ) {
                Text(text = "Upload Image!")
            }
        }
        Spacer(modifier = modifier.padding(60.dp))
    }
}

//https://github.com/shivangchopra11/Picturesque/tree/master/app/src/main/java/com/example/shivang/picturesque/PhotoEditor
//https://stackoverflow.com/questions/67797295/image-picker-from-gallery-for-jetpack-compose-android-kotlin

