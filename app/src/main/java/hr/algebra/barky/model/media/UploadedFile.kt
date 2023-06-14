package hr.algebra.barky.model.media

data class UploadedFile(
    val fileId : String?="",
    val url : String?="",
//    val content_type : String?,
    val date_of_upload : Long=0,
    val description : String?=""
    )
