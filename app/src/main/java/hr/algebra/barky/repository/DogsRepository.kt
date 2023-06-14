package hr.algebra.barky.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import hr.algebra.barky.api.DogsApi
import hr.algebra.barky.db.DogDatabase
import hr.algebra.barky.model.Dog
import hr.algebra.barky.paging.DogsRemoteMediator
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ExperimentalPagingApi
class DogsRepository @Inject constructor(
    private val dogsApi: DogsApi,
    private val dogDatabase: DogDatabase
) {

    fun getDogs(): Flow<PagingData<Dog>>{
        val pagingSource = {dogDatabase.dogDao().getDogs()}

        return Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = DogsRemoteMediator(
                dogsApi,
                dogDatabase
            ),
            pagingSourceFactory = pagingSource
        ).flow
    }

    suspend fun update(dog: Dog) = dogDatabase.dogDao().update(dog)

    suspend fun delete(dog: Dog) = dogDatabase.dogDao().delete(dog)

}