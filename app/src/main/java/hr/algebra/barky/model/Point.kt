package hr.algebra.barky.model

import com.google.android.gms.maps.model.LatLng

@kotlinx.serialization.Serializable
data class Point(
    val title:String,
    val address:String,
    val lat:Double,
    val lng:Double,
    val image:String
){
    fun latLng()=LatLng(lat,lng)
}
