package com.example.projectfoodmanager.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.projectfoodmanager.data.model.dtos.user.UserDTO
import com.example.projectfoodmanager.data.model.modelRequest.geral.IdListRequest
import com.example.projectfoodmanager.data.model.modelResponse.follows.UsersToFollowList
import com.example.projectfoodmanager.data.model.modelResponse.notifications.Notification
import com.example.projectfoodmanager.data.model.modelResponse.notifications.NotificationList
import com.example.projectfoodmanager.data.model.user.UserList
import com.example.projectfoodmanager.data.model.user.UserAuthResponse
import com.example.projectfoodmanager.data.model.user.User
import com.example.projectfoodmanager.data.model.user.UserRecipeBackgrounds
import com.example.projectfoodmanager.data.model.util.ValidationError
import com.example.projectfoodmanager.data.repository.datasource.RemoteDataSource
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.FirebaseMessagingTopics.NOTIFICATION_USER_TOPIC_BASE
import com.example.projectfoodmanager.util.NetworkResult
import com.example.projectfoodmanager.util.SharedPreference
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Response
import java.net.SocketTimeoutException
import javax.inject.Inject
import com.google.gson.Gson

class UserRepositoryImp @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val firebaseMessaging: FirebaseMessaging,
    private val sharedPreference: SharedPreference,
    private val gson: Gson
) : UserRepository {



    private val TAG:String = "AuthRepositoryImp"

    /**
     * Generic function to handle API requests and responses.
     *
     * @param liveData The LiveData where the result will be posted.
     * @param saveSharedPreferences Flag to determine if the result should be saved in SharedPreferences.
     * @param request The API call to be executed.
     */
    private suspend fun <T> handleApiResponse(
        liveData: MutableLiveData<Event<NetworkResult<T>>>,
        saveSharedPreferences: Boolean = false,
        deleteSharedPreferences: Boolean = false,
        request:   suspend () -> Response<T>
    ) {
        try {
            // Post a loading state to indicate the request is in progress
            liveData.postValue(Event(NetworkResult.Loading()))
            Log.i(TAG, "Making API request.")

            // Invoke the API call
            val response = request.invoke()

            if (response.isSuccessful) {
                // API request was successful
                val responseBody = response.body()
                if (responseBody != null) {
                    // Post a success result with the response body
                    liveData.postValue(Event(NetworkResult.Success(responseBody)))



                    if (deleteSharedPreferences) {
                        sharedPreference.deleteSession()
                    }
                } else {
                    // Handle the case where the response body is null
                    liveData.postValue(Event(NetworkResult.Error("Response body is null")))
                }
            } else if (response.errorBody() != null) {
                // Handle the case where the API request was not successful and has an error body
                val errorObj = response.errorBody()!!.charStream().readText()
                Log.i(TAG, "API request was not successful. Error: \n$errorObj")
                liveData.postValue(Event(NetworkResult.Error(errorObj)))
            } else {
                // Handle the case where something went wrong without an error body
                liveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
            }
        } catch (e: Exception) {
            // Handle exceptions here if needed
            Log.e(TAG, "API request failed with exception: ${e.message}")
            // Post an error result for exceptions
            liveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }




    /**
     * User
     *  */

    private val _userRegisterLiveData = MutableLiveData<Event<NetworkResult<String>>>()
    override val userRegisterLiveData: LiveData<Event<NetworkResult<String>>>
        get() = _userRegisterLiveData


    override suspend fun registerUser(user: UserDTO) {
        _userRegisterLiveData.postValue(Event(NetworkResult.Loading()))
        val response = remoteDataSource.registerUser(user)
        if (response.isSuccessful && response.code() == 201) {
            Log.i(TAG, "loginUser: request made was sucessfull.")
            _userRegisterLiveData.postValue(Event(NetworkResult.Success(response.code().toString())))
        }
        else if(response.errorBody()!=null){
            val errorTxt = response.errorBody()?.string()
            Log.i(TAG, "loginUser: request made was not sucessfull: $errorTxt")
            val errorObj = gson.fromJson(errorTxt, ValidationError::class.java)
            Log.i(TAG, "loginUser: request made was not sucessfull: $errorObj")
            _userRegisterLiveData.postValue(Event(NetworkResult.Error(error = errorObj)))
        }
        else{
            _userRegisterLiveData.postValue(Event(NetworkResult.Error(message ="Something Went Wrong")))
        }

    }

    private val _userAuthResponseLiveData = MutableLiveData<Event<NetworkResult<UserAuthResponse>>>()
    override val userAuthLiveData: LiveData<Event<NetworkResult<UserAuthResponse>>>
        get() = _userAuthResponseLiveData

    override suspend fun loginUser(email: String, password: String) {
        _userAuthResponseLiveData.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making login request.")
        try {
            val response =remoteDataSource.loginUser(email,password)
            if (response.isSuccessful && response.body() != null) {
                Log.i(TAG, "loginUser: request made was sucessfull.")
                _userAuthResponseLiveData.postValue(Event(NetworkResult.Success(response.body()!!)))
            }
            else if(response.errorBody()!=null){
                val errorObj = response.errorBody()!!.charStream().readText()
                Log.i(TAG, "loginUser: request made was sucessfull. \n"+errorObj)
                _userAuthResponseLiveData.postValue(Event(NetworkResult.Error(errorObj)))
            }
            else{
                _userAuthResponseLiveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
            }
        } catch (e:SocketTimeoutException){
            _userAuthResponseLiveData.postValue(Event(NetworkResult.Error("No connection to host server...")))
            return
        }
    }

    private val _userLiveData = MutableLiveData<Event<NetworkResult<User>>>()
    override val userLiveData: LiveData<Event<NetworkResult<User>>>
        get() = _userLiveData

    override suspend fun getUserSession(preventDeleteRecipesBackgrounds: Boolean) {
        _userLiveData.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "getUserSession: making login request.")

        try {
            val response =remoteDataSource.getUserAuth()
            if (response.isSuccessful && response.body() != null) {
                Log.i(TAG, "getUserSession: request made was sucessfull.")
                sharedPreference.saveUserSession(response.body()!!, preventDeleteRecipesBackgrounds )
                _userLiveData.postValue(Event(NetworkResult.Success(response.body()!!)))
            }
            else if(response.errorBody()!=null){
                Log.i(TAG, "getUserSession: request made was not sucessfull."+response.errorBody()!!.charStream().readText())
                val errorObj = response.errorBody()!!.charStream().readText()
                _userLiveData.postValue(Event(NetworkResult.Error(errorObj)))
            }
            else{
                _userLiveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
            }
        }catch (e:SocketTimeoutException){
            _userLiveData.postValue(Event(NetworkResult.Error("No connection to host server...")))
            return
        }
    }


    private val _userLogoutResponseLiveData = MutableLiveData<Event<NetworkResult<String>>>()
    override val userLogoutLiveData: LiveData<Event<NetworkResult<String>>>
        get() = _userLogoutResponseLiveData

    override suspend fun logoutUser() {
        _userLogoutResponseLiveData.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "logoutUser: making login request.")
        val response =remoteDataSource.logoutUser()

        if (response.isSuccessful && response.code() == 204) {
            Log.i(TAG, "logoutUser: request made was sucessfull.")
            sharedPreference.deleteSession()
            _userLogoutResponseLiveData.postValue(Event(NetworkResult.Success(response.code().toString())))
        }
        else if(response.errorBody()!=null){
            Log.i(TAG, "logoutUser: request made was not sucessfull."+response.errorBody()!!.charStream().readText())
            val errorObj = response.errorBody()!!.charStream().readText()
            _userLogoutResponseLiveData.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _userLogoutResponseLiveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }

    private val _userUpdateResponseLiveData = MutableLiveData<Event<NetworkResult<User>>>()
    override val userUpdateLiveData: LiveData<Event<NetworkResult<User>>>
        get() = _userUpdateResponseLiveData

    override suspend fun updateUser(userDTO: UserDTO) {
        _userUpdateResponseLiveData.postValue(Event(NetworkResult.Loading()))
        val response = remoteDataSource.updateUser(userDTO)
        if (response.isSuccessful) {
            Log.i(TAG, "updateUser: request made was sucessfull.")
            sharedPreference.updateUserSession(response.body()!!)
            _userUpdateResponseLiveData.postValue(Event(NetworkResult.Success(response.body()!!)))

        }
        else if(response.errorBody()!=null){
            val errorTxt = response.errorBody()?.string()
            Log.i(TAG, "updateUser: request made was not sucessfull: $errorTxt")
            val errorObj = gson.fromJson(errorTxt, ValidationError::class.java)
            _userUpdateResponseLiveData.postValue(Event(NetworkResult.Error(error = errorObj)))
        }
        else{
            _userUpdateResponseLiveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }

    }

    private val _getUserRecipesBackgroundLiveData = MutableLiveData<Event<NetworkResult<UserRecipeBackgrounds>>>()
    override val getUserRecipesBackground: LiveData<Event<NetworkResult<UserRecipeBackgrounds>>>
        get() = _getUserRecipesBackgroundLiveData

    override suspend fun getUserRecipesBackground() {
        _getUserRecipesBackgroundLiveData.postValue(Event(NetworkResult.Loading()))
        val response = remoteDataSource.getUserRecipesBackground()
        if (response.isSuccessful) {
            sharedPreference.saveUserRecipesSession(response.body()!!)
            Log.i(TAG, "updateUser: request made was sucessfull.")
            _getUserRecipesBackgroundLiveData.postValue(Event(NetworkResult.Success(response.body()!!)))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "updateUser: request made was not sucessfull: $errorObj")

            _getUserRecipesBackgroundLiveData.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _getUserRecipesBackgroundLiveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }

    }


    private val _deleteUserAccount = MutableLiveData<Event<NetworkResult<String>>>()
    override val deleteUserAccount: LiveData<Event<NetworkResult<String>>>
        get() = _deleteUserAccount

    override suspend fun deleteUserAccount() {
        handleApiResponse(_deleteUserAccount, deleteSharedPreferences = true) {
            remoteDataSource.deleteUser()
        }
    }


    private val _getUserAccount = MutableLiveData<Event<NetworkResult<User>>>()
    override val getUserAccount: LiveData<Event<NetworkResult<User>>>
        get() = _getUserAccount

    override suspend fun getUserAccount(userId: Int) {
        handleApiResponse(_getUserAccount) {
            remoteDataSource.getUserById(userId)
        }
    }

    /**
     * Follows
     * */

    private val _userFollowersResponseLiveData = MutableLiveData<Event<NetworkResult<UserList>>>()
    override val getUserFollowers: LiveData<Event<NetworkResult<UserList>>>
        get() = _userFollowersResponseLiveData

    override suspend fun getUserFollowers(userId: Int) {
        _userFollowersResponseLiveData.postValue(Event(NetworkResult.Loading()))
        val response = remoteDataSource.getFollowers(userId)
        if (response.isSuccessful) {
            Log.i(TAG, "updateUser: request made was sucessfull.")
            _userFollowersResponseLiveData.postValue(Event(NetworkResult.Success(response.body()!!)))

        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "updateUser: request made was not sucessfull: $errorObj")

            _userFollowersResponseLiveData.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _userFollowersResponseLiveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }


    private val _userFollowedsResponseLiveData = MutableLiveData<Event<NetworkResult<UserList>>>()
    override val getUserFolloweds: LiveData<Event<NetworkResult<UserList>>>
        get() = _userFollowedsResponseLiveData

    override suspend fun getUserFollows(userId: Int) {
        _userFollowedsResponseLiveData.postValue(Event(NetworkResult.Loading()))
        val response = remoteDataSource.getFolloweds(userId)
        if (response.isSuccessful && response.code() == 200) {
            Log.i(TAG, "updateUser: request made was sucessfull.")
            _userFollowersResponseLiveData.postValue(Event(NetworkResult.Success(response.body()!!)))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "updateUser: request made was not sucessfull: $errorObj")

            _userFollowedsResponseLiveData.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _userFollowedsResponseLiveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }


    private val _functionUserFollowRequestsResponseLiveData = MutableLiveData<Event<NetworkResult<UserList>>>()
    override val getFollowRequests: LiveData<Event<NetworkResult<UserList>>>
        get() = _functionUserFollowRequestsResponseLiveData


    override suspend fun getFollowRequests(pageSize: Int) {
        _userFollowedsResponseLiveData.postValue(Event(NetworkResult.Loading()))
        val response = remoteDataSource.getFollowRequests(pageSize)
        if (response.isSuccessful) {
            Log.i(TAG, "updateUser: request made was sucessfull.")
            _functionUserFollowRequestsResponseLiveData.postValue(Event(NetworkResult.Success(response.body()!!)))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "updateUser: request made was not sucessfull: $errorObj")

            _functionUserFollowRequestsResponseLiveData.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _functionUserFollowRequestsResponseLiveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }



    private val _functionPostUserAcceptFollowRequest = MutableLiveData<Event<NetworkResult<Int>>>()
    override val postUserAcceptFollowRequest: LiveData<Event<NetworkResult<Int>>>
        get() = _functionPostUserAcceptFollowRequest

    override suspend fun postUserAcceptFollowRequest(userId: Int) {
        _functionPostUserAcceptFollowRequest.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making addLikeOnRecipe request.")
        val response =remoteDataSource.postAcceptFollowRequest(userId)
        if (response.isSuccessful) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            _functionPostUserAcceptFollowRequest.postValue(Event(NetworkResult.Success(response.code())))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "handleResponse: request made was sucessfull. \n$errorObj")
            _functionPostUserAcceptFollowRequest.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _functionPostUserAcceptFollowRequest.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }

    private val _functionPostUserFollowRequest = MutableLiveData<Event<NetworkResult<Int>>>()
    override val postUserFollowRequest: LiveData<Event<NetworkResult<Int>>>
        get() = _functionPostUserFollowRequest

    override suspend fun postUserFollowRequest(userId: Int) {
        _functionPostUserFollowRequest.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making addLikeOnRecipe request.")
        val response =remoteDataSource.postFollowRequest(userId)
        if (response.isSuccessful) {

            val code = response.code()

            if (code == 201) {
                // Followed
                firebaseMessaging.subscribeToTopic("$NOTIFICATION_USER_TOPIC_BASE/$userId")
            }
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            _functionPostUserFollowRequest.postValue(Event(NetworkResult.Success(response.code())))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "handleResponse: request made was sucessfull. \n$errorObj")
            _functionPostUserFollowRequest.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _functionPostUserFollowRequest.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }

    private val _functionDeleteFollowRequest = MutableLiveData<Event<NetworkResult<Int>>>()
    override val deleteFollowRequest: LiveData<Event<NetworkResult<Int>>>
        get() = _functionDeleteFollowRequest

    override suspend fun deleteFollowRequest(userId: Int) {
        _functionDeleteFollowRequest.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making addLikeOnRecipe request.")
        val response =remoteDataSource.deleteFollowRequest(userId)
        if (response.isSuccessful) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            _functionDeleteFollowRequest.postValue(Event(NetworkResult.Success(response.code())))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "handleResponse: request made was sucessfull. \n$errorObj")
            _functionDeleteFollowRequest.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _functionDeleteFollowRequest.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }

    private val _functionDeleteFollower = MutableLiveData<Event<NetworkResult<Int>>>()
    override val deleteFollower: LiveData<Event<NetworkResult<Int>>>
        get() = _functionDeleteFollower

    override suspend fun deleteFollower(userId: Int) {
        _functionDeleteFollower.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making addLikeOnRecipe request.")
        val response =remoteDataSource.deleteFollower(userId)
        if (response.isSuccessful) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            _functionDeleteFollower.postValue(Event(NetworkResult.Success(response.code())))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "handleResponse: request made was sucessfull. \n$errorObj")
            _functionDeleteFollower.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _functionDeleteFollower.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }


    private val _functionDeleteFollow = MutableLiveData<Event<NetworkResult<Int>>>()
    override val deleteFollow: LiveData<Event<NetworkResult<Int>>>
        get() = _functionDeleteFollow

    override suspend fun deleteFollow(userId: Int) {
        _functionDeleteFollow.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making addLikeOnRecipe request.")
        val response =remoteDataSource.deleteFollow(userId)
        if (response.isSuccessful) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")


            firebaseMessaging.unsubscribeFromTopic("$NOTIFICATION_USER_TOPIC_BASE/$userId")

            _functionDeleteFollow.postValue(Event(NetworkResult.Success(response.code())))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "handleResponse: request made was sucessfull. \n$errorObj")
            _functionDeleteFollow.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _functionDeleteFollow.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }

    private val _functionGetUsersToFollow = MutableLiveData<Event<NetworkResult<UsersToFollowList>>>()
    override val getUsersToFollow: LiveData<Event<NetworkResult<UsersToFollowList>>>
        get() = _functionGetUsersToFollow


    override suspend fun getUsersToFollow(searchString:String?,page: Int?,pageSize:Int?) {
        handleApiResponse(_functionGetUsersToFollow) {
            remoteDataSource.getUsersToFollow(searchString,page,pageSize)
        }
    }


    /** Notifications */
    private val _getNotificationsResponseLiveData = MutableLiveData<Event<NetworkResult<NotificationList>>>()
    override val getNotificationsResponseLiveData: LiveData<Event<NetworkResult<NotificationList>>>
        get() = _getNotificationsResponseLiveData

    private val _getNotificationResponseLiveData = MutableLiveData<Event<NetworkResult<Notification>>>()
    override val getNotificationResponseLiveData: LiveData<Event<NetworkResult<Notification>>>
        get() = _getNotificationResponseLiveData

    private val _putNotificationResponseLiveData= MutableLiveData<Event<NetworkResult<Unit>>>()
    override val putNotificationResponseLiveData: LiveData<Event<NetworkResult<Unit>>>
        get() = _putNotificationResponseLiveData

    private val _putNotificationsResponseLiveData= MutableLiveData<Event<NetworkResult<Unit>>>()
    override val putNotificationsResponseLiveData: LiveData<Event<NetworkResult<Unit>>>
        get() = _putNotificationsResponseLiveData

    private val _deleteNotificationResponseLiveData = MutableLiveData<Event<NetworkResult<Unit>>>()
    override val deleteNotificationResponseLiveData: LiveData<Event<NetworkResult<Unit>>>
        get() = _deleteNotificationResponseLiveData

    private val _deleteNotificationsResponseLiveData = MutableLiveData<Event<NetworkResult<Unit>>>()
    override val deleteNotificationsResponseLiveData: LiveData<Event<NetworkResult<Unit>>>
        get() = _deleteNotificationsResponseLiveData

    override suspend fun getNotifications(page: Int?, pageSize: Int?) {
        handleApiResponse(_getNotificationsResponseLiveData) {
            remoteDataSource.getNotifications(page,pageSize)
        }
    }

    override suspend fun getNotification(id: Int?) {
        handleApiResponse(_getNotificationResponseLiveData) {
            remoteDataSource.getNotification(id)
        }
    }

    override suspend fun putNotification(id: Int?, notification: Notification) {
        handleApiResponse(_putNotificationResponseLiveData) {
            remoteDataSource.putNotification(id,notification)
        }
    }

    override suspend fun putNotifications(idListRequest: IdListRequest) {
        handleApiResponse(_putNotificationsResponseLiveData) {
            remoteDataSource.putNotifications(idListRequest)
        }
    }

    override suspend fun deleteNotification(id: Int?) {
        handleApiResponse(_deleteNotificationResponseLiveData) {
            remoteDataSource.deleteNotification(id)
        }
    }

    override suspend fun deleteNotifications(idListRequest: IdListRequest) {
        handleApiResponse(_deleteNotificationsResponseLiveData) {
            remoteDataSource.deleteNotifications(idListRequest)
        }
    }
}