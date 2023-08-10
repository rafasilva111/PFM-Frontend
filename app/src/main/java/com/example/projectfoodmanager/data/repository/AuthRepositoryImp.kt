package com.example.projectfoodmanager.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.follows.FollowList
import com.example.projectfoodmanager.data.model.modelResponse.user.UserAuthResponse
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.data.repository.datasource.RemoteDataSource
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult
import com.example.projectfoodmanager.util.SharedPreference
import java.net.SocketTimeoutException
import javax.inject.Inject


class AuthRepositoryImp @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val sharedPreference: SharedPreference
) : AuthRepository {
    private val TAG:String = "AuthRepositoryImp"

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
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "loginUser: request made was not sucessfull: $errorObj")

            _userRegisterLiveData.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _userRegisterLiveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
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

    override suspend fun getUserSession() {
        _userLiveData.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "getUserSession: making login request.")

        try {
            val response =remoteDataSource.getUserAuth()
            if (response.isSuccessful && response.body() != null) {
                Log.i(TAG, "getUserSession: request made was sucessfull.")
                sharedPreference.saveUserSession(response.body()!!)
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

    override suspend fun updateUser(userRequest: UserRequest) {
        _userUpdateResponseLiveData.postValue(Event(NetworkResult.Loading()))
        val response = remoteDataSource.updateUser(userRequest)
        if (response.isSuccessful && response.code() == 200) {
            Log.i(TAG, "updateUser: request made was sucessfull.")
            sharedPreference.updateUserSession(response.body()!!)
            _userUpdateResponseLiveData.postValue(Event(NetworkResult.Success(response.body()!!)))

        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "updateUser: request made was not sucessfull: $errorObj")

            _userUpdateResponseLiveData.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _userUpdateResponseLiveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }

    }


    private val _userFollowersResponseLiveData = MutableLiveData<Event<NetworkResult<FollowList>>>()
    override val getUserFollowers: LiveData<Event<NetworkResult<FollowList>>>
        get() = _userFollowersResponseLiveData

    override suspend fun getUserFollowers(idUser: Int) {
        _userFollowersResponseLiveData.postValue(Event(NetworkResult.Loading()))
        val response = remoteDataSource.getFollowers(idUser)
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


    private val _userFollowedsResponseLiveData = MutableLiveData<Event<NetworkResult<FollowList>>>()
    override val getUserFolloweds: LiveData<Event<NetworkResult<FollowList>>>
        get() = _userFollowedsResponseLiveData

    override suspend fun getUserFolloweds(userId: Int) {
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


    private val _functionUserFollowRequestsResponseLiveData = MutableLiveData<Event<NetworkResult<FollowList>>>()
    override val getUserFollowRequests: LiveData<Event<NetworkResult<FollowList>>>
        get() = _functionUserFollowRequestsResponseLiveData


    override suspend fun getUserFollowRequests() {
        _userFollowedsResponseLiveData.postValue(Event(NetworkResult.Loading()))
        val response = remoteDataSource.getFollowRequests()
        if (response.isSuccessful && response.code() == 200) {
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

    override suspend fun postUserAcceptFollowRequest(userID: Int) {
        _functionPostUserAcceptFollowRequest.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making addLikeOnRecipe request.")
        val response =remoteDataSource.postAcceptFollowRequest(userID)
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

    private val _functionDeleteUserFollowRequest = MutableLiveData<Event<NetworkResult<Int>>>()
    override val deleteUserFollowRequest: LiveData<Event<NetworkResult<Int>>>
        get() = _functionDeleteUserFollowRequest

    override suspend fun deleteUserFollowRequest(followType:Int ,userId: Int) {
        _functionDeleteUserFollowRequest.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making addLikeOnRecipe request.")
        val response =remoteDataSource.deleteFollowRequest(followType,userId)
        if (response.isSuccessful) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            _functionDeleteUserFollowRequest.postValue(Event(NetworkResult.Success(response.code())))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "handleResponse: request made was sucessfull. \n$errorObj")
            _functionDeleteUserFollowRequest.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _functionDeleteUserFollowRequest.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }



}