package com.example.projectfoodmanager.util.network

import com.example.projectfoodmanager.data.model.util.ValidationError

sealed class NetworkResult<T>(val data: T? = null, val message: String? = null, val error: ValidationError? = null) {

    class Success<T>(data: T) : NetworkResult<T>(data)
    class Error<T>(message: String? = null, error: ValidationError? = null) : NetworkResult<T>(message = message,error=error)
    class Loading<T> : NetworkResult<T>()

}