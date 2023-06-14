package hr.algebra.barky.model.media

data class HashTag(
    val id : String = "",
    val tag : String? = "",
    val fileId : String=""
){
    override fun toString(): String {
        return "# $tag"
    }
}
