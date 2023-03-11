package com.example.projectfoodmanager.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.user.UserAuthResponse
import com.example.projectfoodmanager.data.model.modelResponse.user.UserResponse
import com.example.projectfoodmanager.data.repository.datasource.RemoteDataSource
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult
import com.example.projectfoodmanager.util.SharedPreference
import retrofit2.Response
import javax.inject.Inject


class AuthRepositoryImp @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val sharedPreference: SharedPreference
) : AuthRepository {
    private val TAG:String = "AuthRepositoryImp"

    private val _userAuthResponseLiveData = MutableLiveData<Event<NetworkResult<UserAuthResponse>>>()
    override val userAuthResponseLiveData: LiveData<Event<NetworkResult<UserAuthResponse>>>
        get() = _userAuthResponseLiveData

    override suspend fun registerUser(userRequest: UserRequest) {
        _userAuthResponseLiveData.postValue(Event(NetworkResult.Loading()))
        val response = remoteDataSource.registerUser(userRequest)
        handleUserResponse(response)
    }

    override suspend fun loginUser(email: String, password: String) {
        _userAuthResponseLiveData.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making login request.")
        val response =remoteDataSource.loginUser(email,password)
        handleUserResponse(response)
    }

    private val _userResponseLiveData = MutableLiveData<Event<NetworkResult<UserResponse>>>()
    override val userResponseLiveData: LiveData<Event<NetworkResult<UserResponse>>>
        get() = _userResponseLiveData

    override suspend fun getUserSession() {
        _userResponseLiveData.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making login request.")
        val response =remoteDataSource.getUserAuth()

        if (response.isSuccessful && response.body() != null) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            _userResponseLiveData.postValue(Event(NetworkResult.Success(response.body()!!)))
        }
        else if(response.errorBody()!=null){
            Log.i(TAG, "handleResponse: request made was not sucessfull."+response.errorBody()!!.charStream().readText())
            val errorObj = response.errorBody()!!.charStream().readText()
            _userResponseLiveData.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _userResponseLiveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }

    private val _userLogoutResponseLiveData = MutableLiveData<Event<NetworkResult<String>>>()
    override val userLogoutResponseLiveData: LiveData<Event<NetworkResult<String>>>
        get() = _userLogoutResponseLiveData

    override suspend fun logoutUser() {
        _userLogoutResponseLiveData.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making login request.")
        val response =remoteDataSource.logoutUser()

        if (response.isSuccessful && response.body() != null) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            _userLogoutResponseLiveData.postValue(Event(NetworkResult.Success(response.body()!!)))
        }
        else if(response.errorBody()!=null){
            Log.i(TAG, "handleResponse: request made was not sucessfull."+response.errorBody()!!.charStream().readText())
            val errorObj = response.errorBody()!!.charStream().readText()
            _userLogoutResponseLiveData.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _userLogoutResponseLiveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }

    private fun handleUserResponse(response: Response<UserAuthResponse>) {
        if (response.isSuccessful && response.body() != null) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            _userAuthResponseLiveData.postValue(Event(NetworkResult.Success(response.body()!!)))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "handleResponse: request made was sucessfull. \n"+errorObj)
            _userAuthResponseLiveData.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _userAuthResponseLiveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }
}