package com.example.projectfoodmanager.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.UserResponse
import com.example.projectfoodmanager.data.repository.datasource.RemoteDataSource
import com.example.projectfoodmanager.util.NetworkResult
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject


class AuthRepositoryImp @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : AuthRepository {
    private val TAG:String = "AuthRepositoryImp"

    private val _userResponseLiveData = MutableLiveData<NetworkResult<UserResponse>>()
    override val userResponseLiveData: LiveData<NetworkResult<UserResponse>>
        get() = _userResponseLiveData

    private val _userLogoutResponseLiveData = MutableLiveData<NetworkResult<String>>()
    override val userLogoutResponseLiveData: LiveData<NetworkResult<String>>
        get() = _userLogoutResponseLiveData

    override suspend fun registerUser(userRequest: UserRequest) {
        _userResponseLiveData.postValue(NetworkResult.Loading())
        val response = remoteDataSource.registerUser(userRequest)
        handleUserResponse(response)
    }

    override suspend fun loginUser(email: String, password: String) {
        _userResponseLiveData.postValue(NetworkResult.Loading())
        Log.i(TAG, "loginUser: making login request.")
        val response =remoteDataSource.loginUser(email,password)
        handleUserResponse(response)
    }

    override suspend fun logoutUser() {
        _userLogoutResponseLiveData.postValue(NetworkResult.Loading())
        Log.i(TAG, "loginUser: making login request.")
        val response =remoteDataSource.logoutUser()

        if (response.isSuccessful && response.body() != null) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            _userLogoutResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null){
            Log.i(TAG, "handleResponse: request made was not sucessfull."+response.errorBody()!!.charStream().readText())
            val errorObj = response.errorBody()!!.charStream().readText()
            _userLogoutResponseLiveData.postValue(NetworkResult.Error(errorObj))
        }
        else{
            _userLogoutResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }

    private fun handleUserResponse(response: Response<UserResponse>) {
        if (response.isSuccessful && response.body() != null) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            _userResponseLiveData.postValue(NetworkResult.Success(response.body()!!))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "handleResponse: request made was sucessfull."+errorObj)
            _userResponseLiveData.postValue(NetworkResult.Error(errorObj))
        }
        else{
            _userResponseLiveData.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    }
}