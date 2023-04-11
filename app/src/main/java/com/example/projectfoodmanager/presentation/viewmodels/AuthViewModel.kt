package com.example.projectfoodmanager.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.user.UserAuthResponse
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.data.repository.AuthRepository
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult
import com.example.projectfoodmanager.util.SharedPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val repository: AuthRepository,
    val sharedPreference: SharedPreference
): ViewModel() {
    private val TAG:String ="AuthViewModel"


    val userRegisterLiveData: LiveData<Event<NetworkResult<String>>>
        get() = repository.userRegisterLiveData

    fun registerUser(userRequest: UserRequest){
        viewModelScope.launch {
            repository.registerUser(userRequest)
        }
    }

    val userAuthResponseLiveData: LiveData<Event<NetworkResult<UserAuthResponse>>>
        get() = repository.userAuthResponseLiveData

    fun loginUser(email: String, password: String){
        viewModelScope.launch {
            repository.loginUser(email,password)
        }
    }

    val userOldLiveData: LiveData<Event<NetworkResult<User>>>
        get() = repository.userOldLiveData

    fun getUserSession(){
        viewModelScope.launch {
            repository.getUserSession()
        }
    }


    val userLogoutResponseLiveData: LiveData<Event<NetworkResult<String>>>
        get() = repository.userLogoutResponseLiveData

    fun logoutUser(){
        viewModelScope.launch {
            repository.logoutUser()
        }
    }

}