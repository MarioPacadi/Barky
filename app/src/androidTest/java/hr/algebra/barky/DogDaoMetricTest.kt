package hr.algebra.barky

import android.util.Log
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import hr.algebra.barky.dao.DogDao
import hr.algebra.barky.db.DogDatabase
import hr.algebra.barky.model.Dog
import hr.algebra.barky.model.media.User
import hr.algebra.barky.state.DogsState
import hr.algebra.barky.view.main.DogsScreen
import hr.algebra.barky.viewmodel.DogsViewModel
import junit.framework.TestCase
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.system.measureTimeMillis

@OptIn(ExperimentalPagingApi::class)
@RunWith(AndroidJUnit4::class)
class DogDaoMetricTest {
//    @get:Rule
//    val composeTestRule = createAndroidComposeRule<MainActivity>()

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
    suspend fun testFetchDogs_Performance() {
        val mockDogs: List<Dog> = listOf(
            Dog(dogId = 1, id = 1, breedName = "Breed 1"),
            Dog(dogId = 2, id = 2, breedName = "Breed 2")
        )

        // Load and measure the time taken to fetch dogs from the database
        val fetchTimeMillis = measureTimeMillis {
            withContext(Dispatchers.IO) {
                dogDao.addDogs(mockDogs)
                withContext(Dispatchers.IO) {
                    dogDao.addDogs(mockDogs)
                    val loadParams = PagingSource.LoadParams.Refresh<Int>(null, 10, false)
                    val loadResult =
                        dogDao.getDogs().load(loadParams) as PagingSource.LoadResult.Page

                    val retrievedDogs = loadResult.data

                    TestCase.assertEquals(mockDogs, retrievedDogs)
                }
            }
        }
        // Log or assert the fetch time
        Log.d("PerformanceTest", "Fetch dogs time: $fetchTimeMillis ms")
        assertTrue("Fetch dogs time exceeds threshold", fetchTimeMillis < 1000)
    }
}