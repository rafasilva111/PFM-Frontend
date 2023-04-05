package com.example.projectfoodmanager.data.repository.datasource


import com.example.projectfoodmanager.data.api.ApiInterface
import com.example.projectfoodmanager.data.model.modelRequest.CommentRequest
import com.example.projectfoodmanager.data.model.modelRequest.RecipeRequest
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.CommentResponse
import com.example.projectfoodmanager.data.model.modelResponse.FollowerResponse
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeListResponse
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeResult

import com.example.projectfoodmanager.data.model.modelResponse.user.UserAuthResponse
import com.example.projectfoodmanager.data.model.modelResponse.user.UserResponse
import com.example.projectfoodmanager.data.repository.datasource.RemoteDataSource
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
	private val apiInterface: ApiInterface
) : RemoteDataSource {

	//User
	override suspend fun registerUser(user: UserRequest): Response<UserAuthResponse> {
		return apiInterface.createUser(user = user)
	}

	override suspend fun loginUser(email: String, password: String): Response<UserAuthResponse> {
		return apiInterface.loginUser(UserRequest(email = email, password = password))
	}

	override suspend fun logoutUser(): Response<String> {
		return apiInterface.logoutUser()
	}

	override suspend fun getUserAuth(): Response<UserResponse> {
		return apiInterface.getUserSession()
	}

	override suspend fun getUserById(userId: Int): Response<UserAuthResponse> {
		return apiInterface.getUser(userId = userId)
	}
	override suspend fun updateUser(userId: Int,user: UserRequest): Response<UserAuthResponse> {
		return apiInterface.updateUser(userId = userId, user = user )
	}
	override suspend fun deleteUser(userId: Int): Response<String> {
		return apiInterface.deleteUser(userId = userId)
	}


	//Recipe
	override suspend fun createRecipe(recipe : RecipeRequest): Response<RecipeResult> {
		return apiInterface.createRecipe(recipe = recipe)
	}
	override suspend fun getRecipe(recipeId: Int): Response<RecipeResult> {
		return apiInterface.getRecipe(recipeId = recipeId)
	}
	override suspend fun getRecipesPaginated(page: Int): Response<RecipeListResponse> {
		return apiInterface.getRecipePaginated(page = page)
	}

	override suspend fun getRecipesByTitleAndTags(
		string: String,
		page: Int
	): Response<RecipeListResponse> {
		return apiInterface.getRecipesByTitleAndTags(string = string,page = page)
	}

	override suspend fun updateRecipe(recipeId: Int, recipe: RecipeRequest): Response<RecipeResult> {
		return apiInterface.updateRecipe(recipeId=recipeId, recipe = recipe )
	}
	override suspend fun deleteRecipe(recipeId: Int): Response<String> {
		return apiInterface.deleteRecipe(recipeId = recipeId)
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