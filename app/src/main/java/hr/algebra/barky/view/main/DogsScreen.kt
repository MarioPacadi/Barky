package hr.algebra.barky.view.main

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.ExperimentalPagingApi
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import hr.algebra.barky.model.Dog
import hr.algebra.barky.R
import hr.algebra.barky.repository.firebase.AuthenticationRepository
import hr.algebra.barky.state.DogsState
import hr.algebra.barky.state.Filter
import hr.algebra.barky.util.getRandomDate
import hr.algebra.barky.util.randomNameGenerator
import hr.algebra.barky.view.common.SearchBar
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import androidx.compose.runtime.Composable
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import hr.algebra.barky.model.media.User
import hr.algebra.barky.repository.firebase.StorageRepository
import hr.algebra.barky.util.aspect.LikeButtonClick

@ExperimentalPagingApi
@Composable
fun DogsScreen(
    modifier: Modifier = Modifier,
    dogsState: DogsState,
    user: User,
    onUpdate: (Dog) -> Unit,
    onDelete: (Dog) -> Unit,
) {
    val stateOfDog by remember {
        mutableStateOf(dogsState.dogs)
    }
    val dogs=stateOfDog.collectAsLazyPagingItems()
    val context=LocalContext.current

    Scaffold(topBar = {
        SearchBar(showSearchButton = true) { searchText->
            Toast.makeText(
                context,
                "searchButtonClicked, text=$searchText",
                Toast.LENGTH_SHORT
            ).show()
            Filter.value=searchText
            dogs.refresh()
        }
    }) {
        var temp=it.toString()
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(all = 12.dp)
        ) {
            if (dogs.itemCount != 0) {
                //items(count=dogs.itemCount, contentType = {dog->dog.})
                items(
                    count = dogs.itemCount,
                    key = dogs.itemKey(key = { dog -> dog.id }
                    ),
                    contentType = dogs.itemContentType()
                ) { index ->
                    val item = dogs[index]
                    item?.let {dog->
                        DogItem(
                            modifier,
                            dog = dog,
                            user,
                            onUpdate,
                            onDelete,
                        )
                    }
                }
            } else {
                item {
                    Image(
                        painter = painterResource(id = R.drawable.zero_results),
                        contentDescription = "no_results"
                    )
                }
            }
        }
    }
}

@Composable
fun DogItem(
    modifier: Modifier,
    dog: Dog,
    user : User,
    onUpdate: (Dog) -> Unit,
    onDelete: (Dog) -> Unit,
) {
    val context= LocalContext.current
    var dialogState by remember { mutableStateOf(false) }

    if (dialogState) {
        ShowDialog(dog,{dialogState=false})
    }

    val update = SwipeAction(
        icon = {
            Icon(
                modifier = modifier.size(150.dp),
                imageVector = Icons.Default.Preview,
                contentDescription = stringResource(R.string.preview),
                tint = Color.White
            )
        },
        background = MaterialTheme.colors.primaryVariant,
        onSwipe = { dialogState=true }
    )

    val delete = SwipeAction(
        icon = {
            Icon(
                modifier = modifier.size(150.dp),
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete),
                tint = Color.White
            )
        },
        background = Color.Red,
        onSwipe = { onDelete(dog) }
    )

    val mustBeAdmin = SwipeAction(
        icon = {
            Icon(
                modifier = modifier.size(150.dp),
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete),
                tint = Color.White
            )
        },
        background = Color.Gray,
        onSwipe = { Toast.makeText(context,"Must be admin to use!",Toast.LENGTH_SHORT).show() }
    )

    SwipeableActionsBox(
        startActions = listOf(update),
        endActions = if(user.isAdmin) {listOf(delete)} else {listOf(mustBeAdmin)}
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
        ) {
            Column(modifier = modifier.padding(vertical = 5.dp)) {
                Box {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(dog.imgThumb)
                            .crossfade(true)
                            .build(),
                        contentDescription = dog.breedName,
                        placeholder = painterResource(id = R.drawable.watchdog),
                        error = painterResource(id = R.drawable.watchdog),
                        contentScale = ContentScale.FillBounds,
                        modifier = modifier
                            .height(450.dp)
                            .clip(RoundedCornerShape(20.dp))
                    )
                    Button(
                        modifier= modifier
                            .align(Alignment.BottomEnd)
                            .border(0.dp, Color.Transparent)
                            .background(color = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        elevation = null,
                        colors = ButtonDefaults.outlinedButtonColors(backgroundColor = Color.Transparent),
                        border = BorderStroke(0.dp, Color.Transparent),
                        onClick = @LikeButtonClick {onUpdate(dog)}
                    ){
                        Icon(
                            modifier = modifier
                                .size(100.dp),
                            imageVector = if (dog.liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = stringResource(id = R.string.preview),
                            tint = Color.Red
                        )
                    }
                }

                Text(
                    modifier = modifier.padding(top = 6.dp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.secondary,
                    text = "${dog.breedName} | ${dog.breedType}"
                )
                Text(text = if (dog.breedDescription.isNullOrEmpty()) "no description available" else dog.breedDescription)
            }
        }
    }
}

@Composable
fun ShowDialog(dog: Dog, onDismiss: () -> Unit, modifier: Modifier=Modifier) {
    val scrollState = rememberScrollState()
    val context=LocalContext.current
    AlertDialog(
        title = {
            Text(
                text = dog.breedName,
                style = typography.h6,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.secondary,
            )},
        text = {
            Column(modifier=modifier.verticalScroll(scrollState)) {
                Text(text="Owner: ${randomNameGenerator().trim()}", modifier = modifier.padding(bottom = 8.dp))
                Text(text="Date uploaded:\n ${getRandomDate(2010,2022).trim()}", modifier = modifier.padding(bottom = 8.dp))
                Box {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(dog.imgThumb)
                            .crossfade(true)
                            .build(),
                        contentDescription = dog.breedName,
                        placeholder = painterResource(id = R.drawable.watchdog),
                        error = painterResource(id = R.drawable.watchdog),
                        contentScale = ContentScale.FillBounds,
                        modifier = modifier
                            .height(200.dp)
                            .clip(RoundedCornerShape(20.dp))
                    )
                    if (AuthenticationRepository.loggedIn()) {
                        Button(
                            modifier = modifier
                                .align(Alignment.BottomEnd)
                                .border(0.dp, Color.Transparent)
                                .background(color = Color.Transparent),
                            contentPadding = PaddingValues(0.dp),
                            elevation = null,
                            colors = ButtonDefaults.outlinedButtonColors(backgroundColor = Color.Transparent),
                            border = BorderStroke(0.dp, Color.Transparent),
                            onClick = {
                                StorageRepository.downloadFile(fileName = dog.breedName, url = dog.imgThumb!!){
                                    Toast.makeText(context, "File successfully saved!", Toast.LENGTH_SHORT).show()
                                    Log.d("LocalSaveFile","File successfully saved!")
                                }
                            }) {
                            Icon(
                                modifier = modifier
                                    .size(50.dp),
                                imageVector = Icons.Default.Download,
                                contentDescription = stringResource(id = R.string.preview),
                                tint = MaterialTheme.colors.primary
                            )
                        }
                    }
                }
                Text(text="Type of Breed: ${dog.breedType}", modifier = modifier.padding(bottom = 8.dp))
                Text(text="Place of origin: ${dog.origin}", modifier = modifier.padding(bottom = 8.dp))
                Text(text="Weight and Height: ${dog.minWeightPounds} pounds, ${dog.minHeightInches} inches", modifier = modifier.padding(bottom = 8.dp))
                Spacer(modifier = modifier.padding(5.dp),)
                Text(
                    text = "Description: \n"+ if (dog.breedDescription.isNullOrEmpty()) "no description available" else dog.breedDescription,
                    style = typography.subtitle2
                )
                Spacer(modifier = modifier.padding(5.dp),)

                Text(text = "Tags:", fontWeight = FontWeight.Bold,color=Color.Magenta)
                if (!dog.furColor.isNullOrEmpty()){
                    val split= dog.furColor.split(",")
                    split.forEach {
                        Text(text = "# ${it.trim()}",color=Color.Magenta)
                    }
                }
                else{
                    Text(text = "No tags",modifier=modifier.padding(end = 5.dp),color=Color.Magenta)
                }
                Spacer(modifier = modifier.padding(30.dp))
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = "Ok")
            }
        },
        onDismissRequest = onDismiss,
    )
}
