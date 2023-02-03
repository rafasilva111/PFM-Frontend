package com.example.projectfoodmanager.data.repository.datasourImp


import com.example.projectfoodmanager.data.api.ApiInterface
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.UserResponse
import com.example.projectfoodmanager.data.repository.datasource.UserRemoteDataSource
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject

class UserRemoteDataSourceImpl @Inject constructor(
	private val apiInterface: ApiInterface
) : UserRemoteDataSource {

	override suspend fun registerUser(user: UserRequest): Response<UserResponse> {
		return apiInterface.registerUser(user = user)
	}

	override suspend fun getUser(userUUID: String): Response<UserResponse> {
		return apiInterface.getUser(userUUID = userUUID)
	}
}