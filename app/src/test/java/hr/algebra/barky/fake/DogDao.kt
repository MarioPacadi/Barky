package hr.algebra.barky.fake

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import hr.algebra.barky.fake.Dog

@Dao
interface DogDao {
    @Query("SELECT * FROM dogs_table")
    fun getDogs() : PagingSource<Int, Dog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDogs(dogs : List<Dog>)     //suspends is the same as async (gonna work with background fun)

    @Query("DELETE FROM dogs_table")
    suspend fun deleteDogs()

    @Update
    suspend fun update(dog: Dog)

    @Delete
    suspend fun delete(dog: Dog)
}