package com.example.projectfoodmanager.data.repository

import android.util.Log
import com.example.projectfoodmanager.data.model.User
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.UserResponse
import com.example.projectfoodmanager.data.repository.datasource.RemoteDataSource
import com.example.projectfoodmanager.data.util.Resource
import com.example.projectfoodmanager.util.ERROR_CODES
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
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

                return responseToUserResult(remoteDataSource.getUserByUUID(userUUID = result.user!!.uid))
            }
        }catch (e: FirebaseAuthInvalidCredentialsException){
            if (e.localizedMessage.contains("password is invalid"))
                return Resource.Error(message = "User's password is incorrect", code = ERROR_CODES.UNAUTORIZED)
            else
                return Resource.Error(message = "There is no user whit that password", code = ERROR_CODES.UNAUTORIZED)
        }
        catch (e: Exception) {
            e.printStackTrace()
            return Resource.Error(message = "$e")
        }
        return Resource.Error(message = "Something went wrong.")
    }

    override suspend fun logout(): Resource<Boolean> {
        try {
            val result = firebaseAuth.signOut()

            this.currentUser = null
            //todo delete shared preferences
            Log.i(TAG, "logout: $result")
            return Resource.Success(true)
        }catch (e: FirebaseAuthInvalidCredentialsException){
            Log.i(TAG, "logout: $e")
        }
        catch (e: Exception) {
            e.printStackTrace()
            return Resource.Error(message = "$e")
        }
        return Resource.Error(message = "Something went wrong.")
    }

    override suspend fun getUserSession(): Resource<User> {
        this.currentUser =firebaseAuth.currentUser
        if (this.currentUser == null){
            return Resource.Error(message = "Session invalid", code = ERROR_CODES.SESSION_INVALID)
        }
        return responseToUser(remoteDataSource.getUserByUUID(userUUID = this.currentUser!!.uid))
    }
    //todo make a validation to the shared preferences user

}