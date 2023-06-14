package hr.algebra.barky.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.IgnoreExtraProperties

@Entity(tableName="dogs_table")
@kotlinx.serialization.Serializable
@IgnoreExtraProperties
data class Dog(
    @PrimaryKey(autoGenerate = true)
    @kotlinx.serialization.Transient
    var dogId: Int =0,
    val id: Int=0,
    val breedName: String="",
    val breedType: String?="",
    val breedDescription: String?="",
    val furColor: String?="",
    val origin: String?="",
    val minHeightInches: Double?=0.0,
    val maxHeightInches: Double?=0.0,
    val minWeightPounds: Double?=0.0,
    val maxWeightPounds: Double?=0.0,
    val minLifeSpan: Double?=0.0,
    val maxLifeSpan: Double?=0.0,
    val imgThumb: String?="",
    var imgSourceURL: String?="",
    var imgAttribution: String?="",
    var imgCreativeCommons: Boolean?=false,
    @kotlinx.serialization.Transient
    var liked: Boolean = false,
    @kotlinx.serialization.Transient
    var userId: String = "",
    @kotlinx.serialization.Transient
    var date_of_upload: Long=0,
)
