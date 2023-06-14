package hr.algebra.barky.state

import androidx.paging.ExperimentalPagingApi
import hr.algebra.barky.viewmodel.DogsViewModel

@ExperimentalPagingApi
class DogsState(dogsViewModel: DogsViewModel) {
    val dogs=dogsViewModel.dogs
}