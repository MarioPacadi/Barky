package hr.algebra.barky.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.algebra.barky.model.Dog
import hr.algebra.barky.repository.DogsRepository
import hr.algebra.barky.util.formatDateTime
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class DogsViewModel @Inject constructor(private val repository: DogsRepository) : ViewModel()  {
    val dogs=repository.getDogs()

    fun update(dog: Dog){
        viewModelScope.launch {
            // Perform the update operation
            val startTime = System.currentTimeMillis()
            repository.update(dog)
            val endTime = System.currentTimeMillis()
            val updateDuration = endTime - startTime
            // Log or track the updateDuration metric for performance monitoring
            Log.i("UpdateDog Duration",updateDuration.formatDateTime())
        }
    }

    fun delete(dog: Dog){
        viewModelScope.launch {
            // Perform the delete operation
            val startTime = System.currentTimeMillis()
            repository.delete(dog)
            val endTime = System.currentTimeMillis()
            val updateDuration = endTime - startTime
            // Log or track the deleteDuration metric for performance monitoring
            Log.i("DeleteDog Duration",updateDuration.formatDateTime())
        }
    }
}