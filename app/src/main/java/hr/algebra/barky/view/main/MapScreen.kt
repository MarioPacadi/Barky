package hr.algebra.barky.view.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.google.maps.android.compose.*
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.maps.model.CameraPosition
import hr.algebra.barky.R
import hr.algebra.barky.model.Point
import hr.algebra.barky.state.MapState
import hr.algebra.barky.util.bitmapDescriptorFromVector

@Composable
fun MapScreen(modifier: Modifier = Modifier, mapState: MapState) {

    if (mapState.loading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            CircularProgressIndicator()
        }
    } else {

        val points = mapState.points
        //val points = listOf<Point>()

        //region MapScreenVariables
        var pointSelected by remember {
            mutableStateOf(points.first())
        }

        val showDialog = remember {
            mutableStateOf(false)
        }

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(pointSelected.latLng(), 2f)
        }

        val uiSetting by remember {
            mutableStateOf(MapUiSettings(zoomControlsEnabled = true))
        }

        val properties by remember {
            mutableStateOf(MapProperties(mapType = MapType.HYBRID, isTrafficEnabled = true))
        }
        //endregion

        GoogleMap(
            modifier = modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSetting,
            properties = properties
        ) {
            points.forEach { point ->
                Marker(
                    icon = bitmapDescriptorFromVector(
                        LocalContext.current,
                        R.drawable.reel
                    ),
                    state= MarkerState(point.latLng()),
                    onClick = {
                        pointSelected = point
                        showDialog.value = true
                        true
                    }
                )
            }
        }

        if (showDialog.value) {
            PointDialog(
                modifier = modifier,
                point = pointSelected,
                showDialog = showDialog
            )
        }
    }
}

@Composable
fun PointDialog(modifier: Modifier, point: Point, showDialog: MutableState<Boolean>) {

    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        confirmButton = {
            Button(onClick = { showDialog.value = false }) {
                Text(text = stringResource(R.string.close))
            }
        },
        title = {
            Text(text = point.title)
        },
        text = {
            Column {
                Text(text = point.address)
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(point.image)
                        .crossfade(durationMillis = 1000)
                        .build(),
                    contentDescription = point.title,
                    placeholder = painterResource(id = R.drawable.reel),
                    contentScale = ContentScale.FillBounds,
                    modifier = modifier
                        .padding(top = 20.dp)
                        .size(270.dp)
                        .clip(CircleShape)
                )
            }
        }
    )
}
