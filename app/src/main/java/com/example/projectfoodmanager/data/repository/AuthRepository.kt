package com.example.projectfoodmanager.data.repository

import androidx.lifecycle.LiveData
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.follows.FollowList
import com.example.projectfoodmanager.data.model.modelResponse.user.UserAuthResponse
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.data.model.modelResponse.user.UserRecipeBackgrounds
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult

interface AuthRepository {


    val userRegisterLiveData: LiveData<Event<NetworkResult<String>>>
    val userAuthLiveData: LiveData<Event<NetworkResult<UserAuthResponse>>>
    val userLogoutLiveData: LiveData<Event<NetworkResult<String>>>
    val userLiveData: LiveData<Event<NetworkResult<User>>>
    val getUserFollowers: LiveData<Event<NetworkResult<FollowList>>>
    val getUserFolloweds: LiveData<Event<NetworkResult<FollowList>>>
    val userUpdateLiveData: LiveData<Event<NetworkResult<User>>>
    val getUserFollowRequests: LiveData<Event<NetworkResult<FollowList>>>
    val postUserAcceptFollowRequest: LiveData<Event<NetworkResult<Int>>>
    val postUserFollowRequest: LiveData<Event<NetworkResult<Int>>>
    val deleteUserFollowRequest: LiveData<Event<NetworkResult<Int>>>


    //User
    suspend fun registerUser(user : UserRequest)
    suspend fun loginUser(email: String, password: String)
    suspend fun getUserSession()
    suspend fun logoutUser()
    suspend fun updateUser(userRequest: UserRequest)
    suspend fun getUserFollowers(idUser: Int)
    suspend fun getUserFolloweds(idUser: Int)
    suspend fun getUserFollowRequests()
    suspend fun postUserAcceptFollowRequest(idUser: Int)
    suspend fun deleteUserFollowRequest(followType: Int, userId: Int)
    suspend fun postUserFollowRequest(userId: Int)
}