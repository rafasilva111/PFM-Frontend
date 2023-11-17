package com.example.projectfoodmanager.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelRequest.geral.IdListRequest
import com.example.projectfoodmanager.data.model.modelResponse.follows.UsersToFollowList
import com.example.projectfoodmanager.data.model.modelResponse.notifications.Notification
import com.example.projectfoodmanager.data.model.modelResponse.notifications.NotificationList
import com.example.projectfoodmanager.data.model.modelResponse.user.UserList
import com.example.projectfoodmanager.data.model.modelResponse.user.UserAuthResponse
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.data.model.modelResponse.user.UserRecipeBackgrounds
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


    val getUserRecipesBackground: LiveData<Event<NetworkResult<UserRecipeBackgrounds>>>
        get() = repository.getUserRecipesBackground

    fun getUserRecipesBackground() {
        viewModelScope.launch {
            repository.getUserRecipesBackground()
        }
    }

    val deleteUserAccount: LiveData<Event<NetworkResult<String>>>
        get() = repository.deleteUserAccount

    fun deleteUserAccount() {
        viewModelScope.launch {
            repository.deleteUserAccount()
        }
    }

    /**
     * Follows
     */

    //GET followers by userID or authenticated user
    val getUserFollowersLiveData: LiveData<Event<NetworkResult<UserList>>>
        get() = repository.getUserFollowers

    fun getFollowers(id_user: Int) {
        viewModelScope.launch {
            repository.getUserFollowers(id_user)
        }
    }

    //GET followeds by userID or authenticated user
    val getUserFollowedsLiveData: LiveData<Event<NetworkResult<UserList>>>
        get() = repository.getUserFolloweds

    fun getFolloweds(id_user: Int){
        viewModelScope.launch {
            repository.getUserFollows(id_user)
        }
    }

    //GET follow requests (authenticated user)
    val getFollowRequestsLiveData: LiveData<Event<NetworkResult<UserList>>>
        get() = repository.getFollowRequests

    fun getFollowRequests(pageSize: Int = 10){
        viewModelScope.launch {
            repository.getFollowRequests(pageSize)
        }
    }

    //POST accept follow requests by userID
    val postUserAcceptFollowRequestLiveData: LiveData<Event<NetworkResult<Int>>>
        get() = repository.postUserAcceptFollowRequest


    fun postAcceptFollowRequest(userId: Int) {
        viewModelScope.launch {
            repository.postUserAcceptFollowRequest(userId)
        }
    }

    //POST follow request by userID
    val postUserFollowRequestLiveData: LiveData<Event<NetworkResult<Int>>>
        get() = repository.postUserFollowRequest

    fun postFollowRequest(userId: Int) {
        viewModelScope.launch {
            repository.postUserFollowRequest(userId)
        }
    }


    // delete follow request

    val deleteFollowRequestLiveData: LiveData<Event<NetworkResult<Int>>>
        get() = repository.deleteFollowRequest


    fun deleteFollowRequest(userId: Int) {
        viewModelScope.launch {
            repository.deleteFollowRequest(userId)
        }
    }


    // delete follower

    val deleteFollowerLiveData: LiveData<Event<NetworkResult<Int>>>
        get() = repository.deleteFollower


    fun deleteFollower(userId: Int) {
        viewModelScope.launch {
            repository.deleteFollower(userId)
        }
    }

    val deleteFollowLiveData: LiveData<Event<NetworkResult<Int>>>
        get() = repository.deleteFollow


    fun deleteFollow(userId: Int) {
        viewModelScope.launch {
            repository.deleteFollow(userId)
        }
    }

    // GET users to follow
    val getUsersToFollow: LiveData<Event<NetworkResult<UsersToFollowList>>>
        get() = repository.getUsersToFollow


    fun getUsersToFollow(searchString:String? = null,page: Int? = null,pageSize:Int? = null) {
        viewModelScope.launch {
            repository.getUsersToFollow(searchString,page,pageSize)
        }
    }



    /**
     * Notifications
     */


    val getNotificationsResponseLiveData: LiveData<Event<NetworkResult<NotificationList>>>
        get() = repository.getNotificationsResponseLiveData

    fun getNotifications(page: Int? = null,pageSize: Int? = null){
        viewModelScope.launch {
            repository.getNotifications(page = page ,pageSize = pageSize)
        }
    }

    val getNotificationResponseLiveData: LiveData<Event<NetworkResult<Notification>>>
        get() = repository.getNotificationResponseLiveData

    fun getNotification(id: Int){
        viewModelScope.launch {
            repository.getNotification(id)
        }
    }

    val putNotificationResponseLiveData: LiveData<Event<NetworkResult<Unit>>>
        get() = repository.putNotificationResponseLiveData

    fun putNotification(id: Int, notification: Notification){
        viewModelScope.launch {
            repository.putNotification(id,notification)
        }
    }

    val putNotificationsResponseLiveData: LiveData<Event<NetworkResult<Unit>>>
        get() = repository.putNotificationsResponseLiveData

    fun putNotifications(idListRequest: IdListRequest){
        viewModelScope.launch {
            repository.putNotifications(idListRequest)
        }
    }

    val deleteNotificationResponseLiveData: LiveData<Event<NetworkResult<Unit>>>
        get() = repository.deleteNotificationResponseLiveData

    fun deleteNotification(id: Int){
        viewModelScope.launch {
            repository.deleteNotification(id)
        }
    }

    val deleteNotificationsResponseLiveData: LiveData<Event<NetworkResult<Unit>>>
        get() = repository.deleteNotificationsResponseLiveData

    fun deleteNotifications(idListRequest: IdListRequest) {
        viewModelScope.launch {
            repository.deleteNotifications(idListRequest)
        }
    }


}