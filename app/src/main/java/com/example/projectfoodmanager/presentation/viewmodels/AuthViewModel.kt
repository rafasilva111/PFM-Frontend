package com.example.projectfoodmanager.presentation.viewmodels

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.UserResponse
import com.example.projectfoodmanager.data.repository.AuthRepository
import com.example.projectfoodmanager.util.Helper
import com.example.projectfoodmanager.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val repository: AuthRepository
): ViewModel() {
    private val TAG:String ="AuthViewModel"

    val userResponseLiveData: LiveData<NetworkResult<UserResponse>>
        get() = repository.userResponseLiveData

    val userLogoutResponseLiveData: LiveData<NetworkResult<String>>
        get() = repository.userLogoutResponseLiveData

    fun registerUser(userRequest: UserRequest){
        viewModelScope.launch {
            repository.registerUser(userRequest)
        }
    }

    init {
        loginUser("","")
    }


    fun loginUser(email: String, password: String){
        viewModelScope.launch {
            repository.loginUser(email,password)
        }
    }

    fun logoutUser(){
        viewModelScope.launch {
            repository.logoutUser()
        }
    }

}