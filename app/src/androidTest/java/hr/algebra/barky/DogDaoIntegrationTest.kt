package hr.algebra.barky

import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import hr.algebra.barky.dao.DogDao
import hr.algebra.barky.db.DogDatabase
import hr.algebra.barky.model.Dog
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DogDaoIntegrationTest {
    private lateinit var dogDatabase: DogDatabase
    private lateinit var dogDao: DogDao

    @Before
    fun setup() {
        dogDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            DogDatabase::class.java
        ).build()

        dogDao = dogDatabase.dogDao()
    }

    @After
    fun tearDown() {
        dogDatabase.close()
    }

    @Test
    fun testAddAndRetrieveDogs() = runBlocking {
        val mockDogs: List<Dog> = listOf(
            Dog(dogId = 1,id = 1, breedName = "Breed 1"),
            Dog(dogId = 2, id = 2, breedName = "Breed 2")
        )

        withContext(Dispatchers.IO) {
            dogDao.addDogs(mockDogs)
            withContext(Dispatchers.IO) {
                dogDao.addDogs(mockDogs)
                val loadParams = PagingSource.LoadParams.Refresh<Int>(null, 10, false)
                val loadResult = dogDao.getDogs().load(loadParams) as PagingSource.LoadResult.Page

                val retrievedDogs = loadResult.data

                TestCase.assertEquals(mockDogs, retrievedDogs)
            }

        }
    }

    @Test
    fun testUpdateDog() = runBlocking {
        var mockDog: Dog = Dog(dogId = 1,id = 1, breedName = "Breed 1")

        withContext(Dispatchers.IO) {
            dogDao.addDogs(listOf(mockDog))

            // Update the dog
            mockDog=mockDog.copy(dogId = 1,breedName = "Updated Breed")

            dogDao.update(mockDog)

            val loadParams = PagingSource.LoadParams.Refresh<Int>(null, 10, false)
            val loadResult = dogDao.getDogs().load(loadParams) as PagingSource.LoadResult.Page

            val retrievedDog = loadResult.data.firstOrNull { it.id == mockDog.id }

            TestCase.assertEquals(mockDog, retrievedDog)
        }
    }

    @Test
    fun testDeleteDog() = runBlocking {
        val mockDog: Dog = Dog(dogId = 1,id = 1, breedName = "Breed 1")

        withContext(Dispatchers.IO) {
            dogDao.addDogs(listOf(mockDog))

            // Delete the dog
            dogDao.delete(mockDog)

            val loadParams = PagingSource.LoadParams.Refresh<Int>(null, 10, false)
            val loadResult = dogDao.getDogs().load(loadParams) as PagingSource.LoadResult.Page

            val retrievedDog = loadResult.data.firstOrNull { it.id == mockDog.id }

            TestCase.assertNull(retrievedDog)
        }
    }
}