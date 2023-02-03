package com.example.projectfoodmanager.data.repository

import android.util.Log
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.UserResponse
import com.example.projectfoodmanager.data.repository.datasource.UserRemoteDataSource
import com.example.projectfoodmanager.data.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import retrofit2.Response
import javax.inject.Inject


class AuthRepositoryImp @Inject constructor(
    private val userRemoteDataSource: UserRemoteDataSource,
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {
    private val TAG:String = "AuthRepositoryImp"
    override var currentUser: FirebaseUser? = null
    private fun responseToUserResult(response : Response<UserResponse>) : Resource<UserResponse>{
        if (response.isSuccessful){
            response.body()?.let { result->
                return Resource.Success(result)
            }
        }
        return Resource.Error(message = "${response.errorBody()?.string()}")
    }

    private fun responseToNothingResult(response : Response<Nothing>) : Resource<Nothing>{
        if (response.isSuccessful){
            response.body()?.let { result->
                return Resource.Success(result)
            }
        }
        return Resource.Error(message = "${response.errorBody()?.string()}")
    }

    override suspend fun registerUser(user: UserRequest): Resource<UserResponse> {
         try {
            val result = firebaseAuth.createUserWithEmailAndPassword(user.email, user.password).await()
            if (result.user != null){
                var userWhitId = user
                currentUser = result.user
                userWhitId.uuid = currentUser!!.uid.toString()
                return responseToUserResult(userRemoteDataSource.registerUser(user = userWhitId))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Resource.Error(message = "$e")
        }
        return Resource.Error(message = "Something went wrong.")
    }

    override suspend fun loginUser(email: String, password: String): Resource<UserResponse> {
        try {
            val result = firebaseAuth.signInWithEmailAndPassword(email,password).await()

            if (result != null){

                Log.i(TAG, "loginUser: User uuid is "+result.user!!.uid)
                return responseToUserResult(userRemoteDataSource.getUser(userUUID = result.user!!.uid))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Resource.Error(message = "$e")
        }
        return Resource.Error(message = "Something went wrong.")
    }

    override suspend fun getUser(): Resource<UserResponse> {
        try {
            return responseToUserResult(userRemoteDataSource.getUser(userUUID = this.firebaseAuth.uid.toString()))
        } catch (e: Exception) {
            e.printStackTrace()
            return Resource.Error(message = "$e")
        }
        return Resource.Error(message = "Something went wrong.")
    }

}