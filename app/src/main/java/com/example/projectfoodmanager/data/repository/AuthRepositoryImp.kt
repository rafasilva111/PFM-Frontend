package com.example.projectfoodmanager.data.repository

import android.util.Log
import com.example.projectfoodmanager.data.model.User
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.UserResponse
import com.example.projectfoodmanager.data.repository.datasource.RemoteDataSource
import com.example.projectfoodmanager.data.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import retrofit2.Response
import javax.inject.Inject


class AuthRepositoryImp @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
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

    private fun responseToUser(response : Response<UserResponse>) : Resource<User>{
        if (response.isSuccessful){
            response.body()?.let { result->
                return Resource.Success(User(
                    id = result.id,
                    age = result.age,
                    first_name = result.first_name,
                    last_name = result.last_name,
                    birth_date = result.birth_date,
                    email = result.email,
                    img_source = result.img_source,
                    height = result.height,
                    weight = result.weight,
                    activity_level = result.activity_level,
                    sex = result.sex,
                ))
            }
        }
        return Resource.Error(message = "${response.errorBody()?.string()}")
    }

    private fun responseToNothingResult(response : Response<String>) : Resource<String>{
        if (response.isSuccessful){
            response.body()?.let { result->
                return Resource.Success("result")
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
                return responseToUserResult(remoteDataSource.registerUser(user = userWhitId))
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
                currentUser  = result.user
                Log.i(TAG, "loginUser: User uuid is "+result.user!!.uid)

                return responseToUserResult(remoteDataSource.getUser(userUUID = result.user!!.uid))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Resource.Error(message = "$e")
        }
        return Resource.Error(message = "Something went wrong.")
    }

    override suspend fun getUser(): Resource<User> {
        return responseToUser(remoteDataSource.getUser(userUUID = this.firebaseAuth.uid.toString()))

    }


}