package hr.algebra.barky.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import dagger.hilt.android.lifecycle.HiltViewModel
import hr.algebra.barky.model.Dog
import hr.algebra.barky.repository.DogsRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalPagingApi
@HiltViewModel
class DogsViewModel @Inject constructor(private val repository: DogsRepository) : ViewModel()  {
    val dogs=repository.getDogs()

    fun update(dog: Dog){
        viewModelScope.launch {
            repository.update(dog)
        }
    }

    fun delete(dog: Dog){
        viewModelScope.launch {
            repository.delete(dog)
        }
    }
}