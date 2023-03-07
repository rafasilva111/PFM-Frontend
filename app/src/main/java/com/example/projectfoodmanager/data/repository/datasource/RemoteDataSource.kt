package com.example.projectfoodmanager.data.repository.datasource


import com.example.projectfoodmanager.data.model.modelRequest.CommentRequest
import com.example.projectfoodmanager.data.model.modelRequest.RecipeRequest
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.CommentResponse
import com.example.projectfoodmanager.data.model.modelResponse.FollowerResponse
import com.example.projectfoodmanager.data.model.modelResponse.UserResponse
import com.example.projectfoodmanager.data.model.modelResponse.recipe.list.RecipeListResponse
import com.example.projectfoodmanager.data.model.modelResponse.recipe.list.RecipeResult
import retrofit2.Response

interface RemoteDataSource {

	//user
	suspend fun registerUser(user: UserRequest) : Response<UserResponse>
	suspend fun loginUser(email: String, password: String) : Response<UserResponse>
	suspend fun logoutUser() : Response<String>
	suspend fun getUserAuth() : Response<UserResponse>
	suspend fun getUserByUUID(userUUID: String): Response<UserResponse>
	suspend fun getUserById(userId: String): Response<UserResponse>
	suspend fun updateUser(userId: String,user: UserRequest): Response<UserResponse>
	suspend fun deleteUser(userId: String): Response<String>

	//recipe
	suspend fun createRecipe(userId: String,recipe : RecipeRequest): Response<RecipeResult>
	suspend fun getRecipe(recipeId: String): Response<RecipeResult>
	suspend fun getRecipesPaginated(page: Int): Response<RecipeListResponse>
	suspend fun updateRecipe(userId: String,recipeId: String,recipe: RecipeRequest): Response<RecipeResult>
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