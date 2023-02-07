package com.example.projectfoodmanager.data.repository.datasource


import com.example.projectfoodmanager.data.model.modelRequest.CommentRequest
import com.example.projectfoodmanager.data.model.modelRequest.FollowerRequest
import com.example.projectfoodmanager.data.model.modelRequest.RecipeRequest
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.CommentResponse
import com.example.projectfoodmanager.data.model.modelResponse.FollowerResponse
import com.example.projectfoodmanager.data.model.modelResponse.RecipeResponse
import com.example.projectfoodmanager.data.model.modelResponse.UserResponse
import retrofit2.Response
import retrofit2.http.*

interface RemoteDataSource {

	//user
	suspend fun registerUser(user: UserRequest) : Response<UserResponse>
	suspend fun getUser(userId: String): Response<UserResponse>
	suspend fun updateUser(userId: String,user: UserRequest): Response<UserResponse>
	suspend fun deleteUser(userId: String): Response<String>

	//recipe
	suspend fun createRecipe(userId: String,recipe : RecipeRequest): Response<RecipeResponse>
	suspend fun getRecipe(recipeId: String): Response<RecipeResponse>
	suspend fun updateRecipe(userId: String,recipeId: String,recipe: RecipeRequest): Response<RecipeResponse>
	suspend fun deleteRecipe(userId: String,recipeId: String): Response<String>

	//comments
	suspend fun createComments(comments: CommentRequest): Response<CommentResponse>
	suspend fun getCommentsByUser(userId: String): Response<CommentResponse>
	suspend fun getCommentsByRecipe(recipeId: String): Response<CommentResponse>
	suspend fun updateComments(userId: String, comments : CommentRequest): Response<CommentResponse>
	suspend fun deleteComments(recipeId: String, userId: String): Response<String>

	//followers
	suspend fun createFollower( userSenderId: String, userReceiverId: String): Response<FollowerResponse>
	suspend fun getFollowers(userSenderId: String): Response<FollowerResponse>
	suspend fun getFollowes( userReceiverId: String): Response<FollowerResponse>
}