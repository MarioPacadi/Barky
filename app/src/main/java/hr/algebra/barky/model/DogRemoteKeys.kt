package hr.algebra.barky.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dog_remote_keys_table")
data class DogRemoteKeys(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val prevPage: Int?,
    val nextPage: Int?,
){
    fun getPageNumber(url: String): Int {
        val pattern = ".*page=(\\d+).*".toRegex()
        val result = pattern.matchEntire(url)
        if (result != null) return result.groupValues[1].toInt()
        return -1
    }

//    val nextPage: Int
//        get() = if (next != null) getPageNumber(next!!) else -1
//
//    val prevPage: Int
//        get() = if (previous != null) getPageNumber(previous!!) else -1
}
