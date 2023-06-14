package hr.algebra.barky.db

import androidx.room.Database
import androidx.room.RoomDatabase
import hr.algebra.barky.dao.DogDao
import hr.algebra.barky.dao.DogRemoteKeysDao
import hr.algebra.barky.model.Dog
import hr.algebra.barky.model.DogRemoteKeys

//java -> : Object (Class)
//kotlin -> : Any (KClass)
@Database(entities = [Dog::class,DogRemoteKeys::class], version = 1, exportSchema = false)
abstract class DogDatabase : RoomDatabase() {
    abstract fun dogDao() : DogDao
    abstract fun dogsRemoteKeysDao() : DogRemoteKeysDao
}