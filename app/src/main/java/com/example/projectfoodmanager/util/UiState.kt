package com.example.projectfoodmanager.util

sealed class UiState<out T>{
    //Loading, Sucess, Failure
    object Loading: UiState<Nothing>()
    data class Success<out T>(val data: T): UiState<T>()
    data class Failure(val error: String?): UiState<Nothing>()


}
