package com.example.projectfoodmanager.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.projectfoodmanager.data.model.modelRequest.user.UserRequest
import com.example.projectfoodmanager.data.model.modelRequest.geral.IdListRequest
import com.example.projectfoodmanager.data.model.modelResponse.PaginatedList
import com.example.projectfoodmanager.data.model.modelResponse.auth.RefreshToken
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntry
import com.example.projectfoodmanager.data.model.modelResponse.follows.FollowerRequest
import com.example.projectfoodmanager.data.model.modelResponse.follows.UsersToFollowList
import com.example.projectfoodmanager.data.model.notification.Notification
import com.example.projectfoodmanager.data.model.notification.NotificationList
import com.example.projectfoodmanager.data.model.user.UserList
import com.example.projectfoodmanager.data.model.modelResponse.user.auth.AuthToken
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.data.model.modelResponse.user.profile.UserProfile
import com.example.projectfoodmanager.data.model.modelResponse.user.recipeBackground.UserRecipesBackground
import com.example.projectfoodmanager.data.model.util.ValidationError
import com.example.projectfoodmanager.data.repository.datasource.RemoteDataSource
import com.example.projectfoodmanager.util.network.Event
import com.example.projectfoodmanager.util.FirebaseMessagingTopics.NOTIFICATION_USER_TOPIC_BASE
import com.example.projectfoodmanager.util.network.NetworkResult
import com.example.projectfoodmanager.util.sharedpreferences.SharedPreference
import com.example.projectfoodmanager.util.sharedpreferences.TokenManager
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Response
import java.net.SocketTimeoutException
import javax.inject.Inject
import com.google.gson.Gson

class UserRepositoryImp @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val firebaseMessaging: FirebaseMessaging,
    private val sharedPreference: SharedPreference,
    private val tokenManager: TokenManager,
    private val gson: Gson
) : UserRepository {



    private val TAG:String = "UserRepositoryImp"

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
                        when (responseBody) {
                            is CalenderEntry -> sharedPreference.deleteSession()
                            else -> Log.e(TAG, "Unable to save this type into shared preferences...")
                        }
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


    override suspend fun registerUser(user: UserRequest) {
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

    private val _AuthTokenLiveData = MutableLiveData<Event<NetworkResult<AuthToken>>>()
    override val userAuthLiveData: LiveData<Event<NetworkResult<AuthToken>>>
        get() = _AuthTokenLiveData

    override suspend fun loginUser(email: String, password: String) {
        _AuthTokenLiveData.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making login request.")
        try {
            val response =remoteDataSource.loginUser(email,password)
            if (response.isSuccessful && response.body() != null) {
                Log.i(TAG, "loginUser: request made was sucessfull.")
                _AuthTokenLiveData.postValue(Event(NetworkResult.Success(response.body()!!)))
            }
            else if(response.errorBody()!=null){
                tokenManager.deleteSession()

                val errorObj = response.errorBody()!!.charStream().readText()
                Log.i(TAG, "loginUser: request made was sucessfull. \n"+errorObj)
                _AuthTokenLiveData.postValue(Event(NetworkResult.Error(errorObj)))
            }
            else{
                _AuthTokenLiveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
            }
        } catch (e:SocketTimeoutException){
            _AuthTokenLiveData.postValue(Event(NetworkResult.Error("No connection to host server...")))
            return
        }
    }

    private val _userLiveData = MutableLiveData<Event<NetworkResult<User>>>()
    override val userLiveData: LiveData<Event<NetworkResult<User>>>
        get() = _userLiveData

    override suspend fun getUserSession() {
        _userLiveData.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "getUserSession: making login request.")

        try {
            val response =remoteDataSource.getUserAuth()
            if (response.isSuccessful && response.body() != null) {

                sharedPreference.saveUserSession(response.body()!!)
                _userLiveData.postValue(Event(NetworkResult.Success(response.body()!!)))
            }
            else if(response.errorBody()!=null){
                tokenManager.deleteSession()


                val errorObj = response.errorBody()!!.charStream().readText()
                _userLiveData.postValue(Event(NetworkResult.Error(errorObj)))
            }
            else{
                _userLiveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
            }
        }catch (e:SocketTimeoutException){
            _userLiveData.postValue(Event(NetworkResult.Error("No connection to host server...")))

        }
    }


    private val _userLogoutResponseLiveData = MutableLiveData<Event<NetworkResult<String>>>()
    override val userLogoutLiveData: LiveData<Event<NetworkResult<String>>>
        get() = _userLogoutResponseLiveData

    override suspend fun logoutUser() {
        _userLogoutResponseLiveData.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "logoutUser: making login request.")
        val response = remoteDataSource.logoutUser(RefreshToken(tokenManager.getRefreshToken()!!))

        if (response.isSuccessful && response.code() == 204) {
            Log.i(TAG, "logoutUser | request made was sucessfull.")
            sharedPreference.deleteSession()
            _userLogoutResponseLiveData.postValue(Event(NetworkResult.Success(response.code().toString())))
        }
        else if(response.errorBody()!=null){
            Log.w(TAG, "logoutUser | request made was unsuccessful.")
            val errorMessage = response.errorBody()!!.charStream().readText()
            Log.w(TAG, "logoutUser | errorMessage: $errorMessage")
            val errorObj = gson.fromJson(errorMessage, ValidationError::class.java)
            Log.w(TAG, "logoutUser | errorMessage: $errorObj")
            _userLogoutResponseLiveData.postValue(Event(NetworkResult.Error(error = errorObj)))
        }
        else{
            _userLogoutResponseLiveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }

    private val _userUpdateResponseLiveData = MutableLiveData<Event<NetworkResult<User>>>()
    override val userUpdateLiveData: LiveData<Event<NetworkResult<User>>>
        get() = _userUpdateResponseLiveData

    override suspend fun updateUser(userRequest: UserRequest) {
        _userUpdateResponseLiveData.postValue(Event(NetworkResult.Loading()))

        val response = remoteDataSource.updateUser(userRequest)
        if (response.isSuccessful) {
            Log.i(TAG, "updateUser: request made was sucessfull.")
            sharedPreference.saveUserSession(response.body()!!)
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

    private val _getUserRecipesBackgroundLiveData = MutableLiveData<Event<NetworkResult<UserRecipesBackground>>>()
    override val getUserRecipesBackground: LiveData<Event<NetworkResult<UserRecipesBackground>>>
        get() = _getUserRecipesBackgroundLiveData

    override suspend fun getUserRecipesBackground() {
        _getUserRecipesBackgroundLiveData.postValue(Event(NetworkResult.Loading()))
        val response = remoteDataSource.getUserRecipesBackground()
        if (response.isSuccessful) {
            sharedPreference.saveUserRecipesBackground(response.body()!!)
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


    private val _getUserAccount = MutableLiveData<Event<NetworkResult<UserProfile>>>()
    override val getUserAccount: LiveData<Event<NetworkResult<UserProfile>>>
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

    override suspend fun getUserFollowers(page: Int?,pageSize: Int?,userId: Int?,searchString: String?) {

        handleApiResponse(
            _userFollowersResponseLiveData
        ) {
            remoteDataSource.getFollowers(page,pageSize,userId,searchString)
        }

    }


    private val _userFollowsResponseLiveData = MutableLiveData<Event<NetworkResult<UserList>>>()
    override val getUserFollows: LiveData<Event<NetworkResult<UserList>>>
        get() = _userFollowsResponseLiveData

    override suspend fun getUserFollows(page: Int?, pageSize: Int?, userId: Int?, searchString: String?) {


        handleApiResponse(
            _userFollowsResponseLiveData
        ) {
            remoteDataSource.getFollows(page,pageSize,userId,searchString)
        }

    }


    private val _functionUserFollowRequestsResponseLiveData = MutableLiveData<Event<NetworkResult<PaginatedList<FollowerRequest>>>>()
    override val getFollowRequests: LiveData<Event<NetworkResult<PaginatedList<FollowerRequest>>>>
        get() = _functionUserFollowRequestsResponseLiveData


    override suspend fun getFollowRequests(pageSize: Int) {

        handleApiResponse(
            _functionUserFollowRequestsResponseLiveData
        ) {
            remoteDataSource.getFollowRequests(pageSize)
        }

    }



    private val _functionPostUserAcceptFollowRequest = MutableLiveData<Event<NetworkResult<Unit>>>()
    override val postUserAcceptFollowRequest: LiveData<Event<NetworkResult<Unit>>>
        get() = _functionPostUserAcceptFollowRequest

    override suspend fun postUserAcceptFollowRequest(userId: Int) {

        handleApiResponse(
            _functionPostUserAcceptFollowRequest
        ) {
            remoteDataSource.postAcceptFollowRequest(userId)
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

    private val _functionDeleteFollowerRequest = MutableLiveData<Event<NetworkResult<Unit>>>()
    override val deleteFollowerRequest: LiveData<Event<NetworkResult<Unit>>>
        get() = _functionDeleteFollowerRequest

    override suspend fun deleteFollowerRequest(userId: Int) {
        handleApiResponse(
            _functionDeleteFollowerRequest
        ) {
            remoteDataSource.deleteFollowRequest(followerId = userId)
        }

    }

    private val _functionDeleteFollowRequest = MutableLiveData<Event<NetworkResult<Unit>>>()
    override val deleteFollowRequest: LiveData<Event<NetworkResult<Unit>>>
        get() = _functionDeleteFollowRequest

    override suspend fun deleteFollowRequest(userId: Int) {
        handleApiResponse(
            _functionDeleteFollowRequest
        ) {
            remoteDataSource.deleteFollowRequest(followedId = userId)
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

    override suspend fun getNotifications(page: Int?, pageSize: Int?, lastId: Int?) {
        handleApiResponse(_getNotificationsResponseLiveData) {
            remoteDataSource.getNotifications(page,pageSize,lastId)
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