package hr.algebra.barky.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import hr.algebra.barky.api.DogsApi
import hr.algebra.barky.db.DogDatabase
import hr.algebra.barky.model.Dog
import hr.algebra.barky.model.DogRemoteKeys
import hr.algebra.barky.state.Filter

@ExperimentalPagingApi
class DogsRemoteMediator(
    private val dogsApi: DogsApi,
    private val dogDatabase: DogDatabase,
) : RemoteMediator<Int, Dog>() {
    private val dogDao = dogDatabase.dogDao()
    private val dogRemoteKeysDao = dogDatabase.dogsRemoteKeysDao()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Dog>): MediatorResult {
        Log.d("MEDIATOR",loadType.toString())

        return try {

            val currentPage = when(loadType){
                LoadType.REFRESH -> {
                    val dogRemoteKeys : DogRemoteKeys? = getDogRemoteKeysClosestToCurrentPosition(state)
                    dogRemoteKeys?.nextPage?.minus(1) ?: 1
                }
                LoadType.PREPEND -> {
                    val dogRemoteKeys : DogRemoteKeys? = getDogRemoteKeysForFirstItem(state)
                    val prevPage = dogRemoteKeys?.prevPage?: return MediatorResult.Success(dogRemoteKeys!=null)
                    prevPage
                }
                LoadType.APPEND -> {
                    val dogRemoteKeys : DogRemoteKeys? = getDogRemoteKeysForLastItem(state)
                    val nextPage = dogRemoteKeys?.nextPage ?: return MediatorResult.Success(dogRemoteKeys!=null)
                    nextPage
                }
            }
            //val response = dogsApi.getDogs(page = currentPage, search = "Hound Dogs")
            val filter=Filter.value
            val response = dogsApi.getDogs(page = currentPage, search = filter)
            Log.d("Filter",filter)

            val endOfPaginationReached = response.dogs.isEmpty()

            val prevPage=if(currentPage==1) null else currentPage -1
            val nextPage=if(endOfPaginationReached) null else currentPage +1

            dogDatabase.withTransaction {
                if(loadType==LoadType.REFRESH){
                    dogDao.deleteDogs()
                    dogRemoteKeysDao.deleteDogRemoteKeys()
                }

                val dogRemoteKeys=response.dogs.map { dog ->
                    DogRemoteKeys(
                        id = dog.id,
                        prevPage = prevPage,
                        nextPage = nextPage
                    )
                }
                dogRemoteKeysDao.addDogRemoteKeys(dogRemoteKeys = dogRemoteKeys)
                dogDao.addDogs(dogs = response.dogs)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception){
            Log.e("Error",e.toString())
            MediatorResult.Error(e)
        }
    }

    private suspend fun getDogRemoteKeysForFirstItem(state: PagingState<Int, Dog>): DogRemoteKeys? {
        return state.pages.firstOrNull(){ it.data.isNotEmpty() }?.data?.firstOrNull()?.let { dog ->
            dogRemoteKeysDao.getDogRemoteKeys(id = dog.id)
        }
    }

    private suspend fun getDogRemoteKeysForLastItem(state: PagingState<Int, Dog>): DogRemoteKeys? {
        return state.pages.lastOrNull(){ it.data.isNotEmpty() }?.data?.lastOrNull()?.let { dog ->
            dogRemoteKeysDao.getDogRemoteKeys(id = dog.id)
        }
    }

    private suspend fun getDogRemoteKeysClosestToCurrentPosition(state: PagingState<Int, Dog>): DogRemoteKeys? {
        return state.anchorPosition?.let { position->
            state.closestItemToPosition(position)?.id?.let { id->
                dogRemoteKeysDao.getDogRemoteKeys(id = id)
            }
        }
    }
}