package hr.algebra.barky.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hr.algebra.barky.model.FirestoreDatabase
import hr.algebra.barky.repository.firebase.FirestoreRepository
import hr.algebra.barky.model.media.Package
import hr.algebra.barky.state.PackageState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PackageViewModel() : ViewModel(){

    private val _packagesState = mutableStateOf(PackageState())

    val packagesState: State<PackageState>
        get() = _packagesState


    private val errorHandler= CoroutineExceptionHandler{
            _,e-> Log.e("PACKAGES_VIEWMODEL",e.toString(),e)
    }

    init {
        viewModelScope.launch(errorHandler) {
            getPackages()
        }
    }

    fun getPackages() = runBlocking {
        FirestoreRepository.getAllValuesFromCollection<Package>(FirestoreDatabase.PACKAGE,Package::class.java){
            _packagesState.value=_packagesState.value.copy(
                packages = it.values.toList(),
                selected = it.values.first()
            )
        }
    }
}