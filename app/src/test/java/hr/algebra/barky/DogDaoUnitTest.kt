package hr.algebra.barky

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import hr.algebra.barky.fake.Dog
import hr.algebra.barky.fake.DogDao
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import androidx.paging.PagingSource
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.Mockito.mock

/**
interface DogDao {
    @Query("SELECT * FROM dogs_table")
    fun getDogs() : PagingSource<Int,Dog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDogs(dogs : List<Dog>)     //suspends is the same as async (gonna work with background fun)

    @Query("DELETE FROM dogs_table")
    suspend fun deleteDogs()

    @Update
    suspend fun update(dog: Dog)

    @Delete
    suspend fun delete(dog: Dog)
}
 */

class DogDaoUnitTest {
    private lateinit var dogDao: DogDao

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        dogDao = mock(DogDao::class.java)
    }

    @Test
    fun testGetDogs_ReturnsPagingSource() = runBlocking {
        val mockPagingSource: PagingSource<Int, Dog> = mock(PagingSource::class.java) as PagingSource<Int, Dog>
        given(dogDao.getDogs()).willReturn(mockPagingSource)

        val result = dogDao.getDogs()

        assertEquals(mockPagingSource, result)
    }

    @Test
    fun testAddDogs_CallsInsertMethod() = runBlocking {
        val mockDogs: List<Dog> = listOf(mock(Dog::class.java))

        dogDao.addDogs(mockDogs)

        then(dogDao).should().addDogs(mockDogs)
    }

    @Test
    fun testDeleteDogs_CallsDeleteMethod() = runBlocking {
        dogDao.deleteDogs()

        then(dogDao).should().deleteDogs()
    }

    @Test
    fun testUpdate_CallsUpdateMethod() = runBlocking {
        val mockDog: Dog = mock(Dog::class.java)

        dogDao.update(mockDog)

        then(dogDao).should().update(mockDog)
    }

    @Test
    fun testDelete_CallsDeleteMethod() = runBlocking {
        val mockDog: Dog = mock(Dog::class.java)

        dogDao.delete(mockDog)

        then(dogDao).should().delete(mockDog)
    }
}