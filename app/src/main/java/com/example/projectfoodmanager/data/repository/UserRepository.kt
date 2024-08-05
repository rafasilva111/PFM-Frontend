package com.example.projectfoodmanager.data.repository

import androidx.lifecycle.LiveData
import com.example.projectfoodmanager.data.model.dtos.user.UserDTO
import com.example.projectfoodmanager.data.model.modelRequest.geral.IdListRequest
import com.example.projectfoodmanager.data.model.modelResponse.auth.RefreshToken
import com.example.projectfoodmanager.data.model.modelResponse.follows.UsersToFollowList
import com.example.projectfoodmanager.data.model.notification.Notification
import com.example.projectfoodmanager.data.model.notification.NotificationList
import com.example.projectfoodmanager.data.model.user.UserList
import com.example.projectfoodmanager.data.model.modelResponse.user.auth.AuthToken
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.data.model.modelResponse.user.profile.UserProfile
import com.example.projectfoodmanager.data.model.modelResponse.user.recipeBackground.UserRecipesBackground
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult

interface UserRepository {


    /** User */

    val userRegisterLiveData: LiveData<Event<NetworkResult<String>>>
    val userAuthLiveData: LiveData<Event<NetworkResult<AuthToken>>>
    val userLogoutLiveData: LiveData<Event<NetworkResult<String>>>
    val userLiveData: LiveData<Event<NetworkResult<User>>>
    val getUserRecipesBackground: LiveData<Event<NetworkResult<UserRecipesBackground>>>
    val userUpdateLiveData: LiveData<Event<NetworkResult<User>>>
    val deleteUserAccount: LiveData<Event<NetworkResult<String>>>

    val getUserAccount: LiveData<Event<NetworkResult<UserProfile>>>

    suspend fun registerUser(user : UserDTO)
    suspend fun loginUser(email: String, password: String)
    suspend fun getUserSession()
    suspend fun logoutUser()
    suspend fun updateUser(userDTO: UserDTO)
    suspend fun getUserRecipesBackground()
    suspend fun deleteUserAccount()

    suspend fun getUserAccount(userId: Int)

    /** Follows */

    val getUserFollowers: LiveData<Event<NetworkResult<UserList>>>
    val getUserFolloweds: LiveData<Event<NetworkResult<UserList>>>
    val deleteFollower: LiveData<Event<NetworkResult<Int>>>
    val deleteFollow: LiveData<Event<NetworkResult<Int>>>


    suspend fun getUserFollowers(idUser: Int)
    suspend fun getUserFollows(idUser: Int)
    suspend fun deleteFollower(userId: Int)
    suspend fun deleteFollow(userId: Int)

    /** Follows Request */

    val getUsersToFollow: LiveData<Event<NetworkResult<UsersToFollowList>>>
    val getFollowRequests: LiveData<Event<NetworkResult<UserList>>>
    val postUserFollowRequest: LiveData<Event<NetworkResult<Int>>>
    val postUserAcceptFollowRequest: LiveData<Event<NetworkResult<Int>>>
    val deleteFollowRequest: LiveData<Event<NetworkResult<Int>>>

    suspend fun getUsersToFollow(searchString:String?,page: Int?,pageSize:Int?)
    suspend fun getFollowRequests(pageSize:Int)
    suspend fun postUserFollowRequest(userId: Int)
    suspend fun postUserAcceptFollowRequest(idUser: Int)
    suspend fun deleteFollowRequest(userId: Int)

    /** Notifications */

    val getNotificationsResponseLiveData: LiveData<Event<NetworkResult<NotificationList>>>
    val getNotificationResponseLiveData: LiveData<Event<NetworkResult<Notification>>>
    val putNotificationResponseLiveData: LiveData<Event<NetworkResult<Unit>>>
    val putNotificationsResponseLiveData: LiveData<Event<NetworkResult<Unit>>>
    val deleteNotificationResponseLiveData: LiveData<Event<NetworkResult<Unit>>>
    val deleteNotificationsResponseLiveData: LiveData<Event<NetworkResult<Unit>>>

    suspend fun getNotifications(page: Int?, pageSize: Int?, lastId: Int?)
    suspend fun getNotification(id:Int?)
    suspend fun putNotification(id:Int?, notification: Notification)
    suspend fun putNotifications(idListRequest: IdListRequest)
    suspend fun deleteNotification(id:Int?)
    suspend fun deleteNotifications(idListRequest: IdListRequest)


}