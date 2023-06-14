package hr.algebra.barky.model.media

data class Package(
    val Name : String?="",
    val Price: Double?=0.0,
    val Upload_Size:Double?=0.0,
    val Upload_Limit:Double?=0.0,

    val Color : Long=0xFF888888
)
