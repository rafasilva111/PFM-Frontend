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
    }

    private val _userOldLiveData = MutableLiveData<Event<NetworkResult<User>>>()
    override val userLiveData: LiveData<Event<NetworkResult<User>>>
        get() = _userOldLiveData

    override suspend fun getUserSession() {
        _userOldLiveData.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "getUserSession: making login request.")
        val response =remoteDataSource.getUserAuth()

        if (response.isSuccessful && response.body() != null) {
            Log.i(TAG, "getUserSession: request made was sucessfull.")
            _userOldLiveData.postValue(Event(NetworkResult.Success(response.body()!!)))
        }
        else if(response.errorBody()!=null){
            Log.i(TAG, "getUserSession: request made was not sucessfull."+response.errorBody()!!.charStream().readText())
            val errorObj = response.errorBody()!!.charStream().readText()
            _userOldLiveData.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _userOldLiveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
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
    override val userFollowersResponseLiveData: LiveData<Event<NetworkResult<FollowList>>>
        get() = _userFollowersResponseLiveData

    private val _userFolloweesResponseLiveData = MutableLiveData<Event<NetworkResult<FollowList>>>()
    override val userFolloweesResponseLiveData: LiveData<Event<NetworkResult<FollowList>>>
        get() = _userFolloweesResponseLiveData


    override suspend fun getUserFollowers() {
        _userFollowersResponseLiveData.postValue(Event(NetworkResult.Loading()))
        val response = remoteDataSource.getFollowers()
        if (response.isSuccessful && response.code() == 200) {
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

    override suspend fun getUserFollowees() {
        _userFolloweesResponseLiveData.postValue(Event(NetworkResult.Loading()))
        val response = remoteDataSource.getFolloweds()
        if (response.isSuccessful && response.code() == 200) {
            Log.i(TAG, "updateUser: request made was sucessfull.")
            _userFollowersResponseLiveData.postValue(Event(NetworkResult.Success(response.body()!!)))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "updateUser: request made was not sucessfull: $errorObj")

            _userFolloweesResponseLiveData.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _userFolloweesResponseLiveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }

}