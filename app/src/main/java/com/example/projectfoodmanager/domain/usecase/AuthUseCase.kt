package com.example.projectfoodmanager.domain.usecase

import android.util.Log
import com.example.projectfoodmanager.data.model.User
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.UserResponse
import com.example.projectfoodmanager.data.util.Resource
import com.example.projectfoodmanager.data.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


class AuthUseCase @Inject constructor(
	private val authRepository: AuthRepository
) {

	fun registerUser(userRequest: UserRequest) : Flow<Resource<UserResponse>> = flow {
		emit(Resource.Loading())
		//create a demo user and upload
		try {
			val response = authRepository.registerUser(user = userRequest)
			emit(response)
		}catch (e : HttpException){
			Log.i("AuthUseCase", e.localizedMessage!!)
			emit (Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
		}
		catch (e : IOException){
			Log.i("AuthUseCase", e.localizedMessage!!)
			emit (Resource.Error("Couldn't reach server. Check your internet connection."))
		}
	}

/*	fun loginUser(email: String,password: String) : Flow<Resource<UserResponse>> = flow {
		emit(Resource.Loading())
		//create a demo user and upload
		try {
			val response = authRepository.loginUser(email,password)
			Log.i("AuthUseCase", "I dey here, ${response.data}")
			emit(response)
		}catch (e : HttpException){
			Log.i("AuthUseCase", e.localizedMessage!!)
			emit (Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
		}
		catch (e : IOException){
			Log.i("AuthUseCase", e.localizedMessage!!)
			emit (Resource.Error("Couldn't reach server. Check your internet connection."))
		}
	}*/


	suspend fun getUserSession() : Resource<User> {
		return authRepository.getUserSession()
	}



}