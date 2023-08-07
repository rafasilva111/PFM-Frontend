package com.example.projectfoodmanager.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.follows.FollowList
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
        get() = repository.userAuthLiveData

    fun loginUser(email: String, password: String){
        viewModelScope.launch {
            repository.loginUser(email,password)
        }
    }

    val userResponseLiveData: LiveData<Event<NetworkResult<User>>>
        get() = repository.userLiveData

    fun getUserSession(){
        viewModelScope.launch {
            repository.getUserSession()
        }
    }


    val userUpdateResponseLiveData: LiveData<Event<NetworkResult<User>>>
        get() = repository.userUpdateLiveData

    fun updateUser(userRequest: UserRequest){
        viewModelScope.launch {
            repository.updateUser(userRequest)
        }
    }


    val userLogoutResponseLiveData: LiveData<Event<NetworkResult<String>>>
        get() = repository.userLogoutLiveData

    fun logoutUser(){
        viewModelScope.launch {
            repository.logoutUser()
        }
    }

    val getUserFollowersLiveData: LiveData<Event<NetworkResult<FollowList>>>
        get() = repository.userFollowersLiveData

    fun getFollowers(id_user: Int) {
        viewModelScope.launch {
            repository.getUserFollowers(id_user)
        }
    }

    val getUserFollowedsLiveData: LiveData<Event<NetworkResult<FollowList>>>
        get() = repository.userFollowedsLiveData

    fun getFolloweds(id_user: Int){
        viewModelScope.launch {
            repository.getUserFolloweds(id_user)
        }
    }


    val getUserFollowRequestsLiveData: LiveData<Event<NetworkResult<FollowList>>>
        get() = repository.userFollowRequestsLiveData

    fun getFollowRequests(){
        viewModelScope.launch {
            repository.getUserFollowRequests()
        }
    }

}