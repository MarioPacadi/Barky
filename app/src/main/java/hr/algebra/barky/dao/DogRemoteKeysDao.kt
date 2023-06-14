package hr.algebra.barky.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hr.algebra.barky.model.DogRemoteKeys

@Dao
interface DogRemoteKeysDao {
    @Query("SELECT * FROM dog_remote_keys_table WHERE id=:id")
    suspend fun getDogRemoteKeys(id : Int) : DogRemoteKeys

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDogRemoteKeys(dogRemoteKeys: List<DogRemoteKeys>)     //suspends is the same as async (gonna work with background fun)

    @Query("DELETE FROM dog_remote_keys_table")
    suspend fun deleteDogRemoteKeys()
}