package com.example.projectfoodmanager.data.repository.datasource


import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.UserResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface UserRemoteDataSource {


	suspend fun registerUser(user: UserRequest) : Response<UserResponse>
	suspend fun getUser(userUUID: String): Response<UserResponse>

}