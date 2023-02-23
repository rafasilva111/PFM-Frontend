package com.example.projectfoodmanager.data.repository.datasourImp


import com.example.projectfoodmanager.data.api.ApiInterface
import com.example.projectfoodmanager.data.model.modelRequest.CommentRequest
import com.example.projectfoodmanager.data.model.modelRequest.RecipeRequest
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.CommentResponse
import com.example.projectfoodmanager.data.model.modelResponse.FollowerResponse
import com.example.projectfoodmanager.data.model.modelResponse.RecipeResponse
import com.example.projectfoodmanager.data.model.modelResponse.UserResponse
import com.example.projectfoodmanager.data.repository.datasource.RemoteDataSource
import retrofit2.Response
import retrofit2.http.Query
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
	private val apiInterface: ApiInterface
) : RemoteDataSource {

	//User
	override suspend fun registerUser(user: UserRequest): Response<UserResponse> {
		return apiInterface.createUser(user = user)
	}
	override suspend fun getUserByUUID(userUUID: String): Response<UserResponse> {
		 apiInterface.getUserByUUID(userUUID = userUUID).let {
			 return it
		}
	}
	override suspend fun getUserById(userId: String): Response<UserResponse> {
		return apiInterface.getUser(userId = userId)
	}
	override suspend fun updateUser(userId: String,user: UserRequest): Response<UserResponse> {
		return apiInterface.updateUser(userId = userId, user = user )
	}
	override suspend fun deleteUser(userId: String): Response<String> {
		return apiInterface.deleteUser(userId = userId)
	}


	//Recipe
	override suspend fun createRecipe(userId: String, recipe : RecipeRequest): Response<RecipeResponse> {
		return apiInterface.createRecipe(userId = userId,recipe = recipe)
	}
	override suspend fun getRecipe(recipeId: String): Response<RecipeResponse> {
		return apiInterface.getRecipe(recipeId = recipeId)
	}
	override suspend fun updateRecipe(userId: String,recipeId: String,recipe: RecipeRequest): Response<RecipeResponse> {
		return apiInterface.updateRecipe(recipeId=recipeId,userId = userId, recipe = recipe )
	}
	override suspend fun deleteRecipe(userId: String,recipeId: String): Response<String> {
		return apiInterface.deleteRecipe(userId=userId,recipeId = recipeId)
	}


	//Comments
	override suspend fun createComments(comment : CommentRequest): Response<CommentResponse> {
		return apiInterface.createComments(comment = comment)
	}
	override suspend fun getCommentsByRecipe(recipeId: String): Response<CommentResponse> {
		return apiInterface.getCommentsByRecipe(recipeId = recipeId)
	}
	override suspend fun getCommentsByUser(userId: String): Response<CommentResponse> {
		return apiInterface.getCommentsByUser(userId = userId)
	}
	override suspend fun updateComments(userId: String,comment : CommentRequest): Response<CommentResponse> {
		return apiInterface.updateComments(userId=userId,comment = comment)
	}
	override suspend fun deleteComments(userId: String,recipeId : String): Response<String> {
		return apiInterface.deleteComments(userId = userId, recipeId = recipeId)
	}

	//Followers
	override suspend fun createFollower( userSenderId: String, userReceiverId: String): Response<FollowerResponse> {
		return apiInterface.createFollower(userSenderId = userSenderId,userReceiverId = userReceiverId)
	}
	override suspend fun getFollowers(userSenderId: String): Response<FollowerResponse> {
		return apiInterface.getFollowers(userSenderId = userSenderId)
	}
	override suspend fun getFollowes( userReceiverId: String): Response<FollowerResponse> {
		return apiInterface.getFollowes(userReceiverId = userReceiverId)
	}
}