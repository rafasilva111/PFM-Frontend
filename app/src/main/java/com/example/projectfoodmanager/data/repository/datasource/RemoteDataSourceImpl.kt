package com.example.projectfoodmanager.data.repository.datasource


import com.example.projectfoodmanager.data.api.ApiInterface
import com.example.projectfoodmanager.data.model.modelRequest.CommentRequest
import com.example.projectfoodmanager.data.model.modelRequest.RecipeRequest
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.CommentResponse
import com.example.projectfoodmanager.data.model.modelResponse.FollowerResponse
import com.example.projectfoodmanager.data.model.modelResponse.follows.FollowList
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeList
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe

import com.example.projectfoodmanager.data.model.modelResponse.user.UserAuthResponse
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
	private val apiInterface: ApiInterface
) : RemoteDataSource {

	//User
	override suspend fun registerUser(user: UserRequest): Response<Unit> {
		return apiInterface.createUser(user = user)
	}

	override suspend fun loginUser(email: String, password: String): Response<UserAuthResponse> {
		return apiInterface.loginUser(UserRequest(email = email, password = password))
	}

	override suspend fun logoutUser(): Response<String> {
		return apiInterface.logoutUser()
	}

	override suspend fun getUserAuth(): Response<User> {
		return apiInterface.getUserSession()
	}

	override suspend fun getUserById(userId: Int): Response<UserAuthResponse> {
		return apiInterface.getUser(userId = userId)
	}
	override suspend fun updateUser(user: UserRequest): Response<User> {
		return apiInterface.updateUser(user = user )
	}
	override suspend fun deleteUser(userId: Int): Response<String> {
		return apiInterface.deleteUser(userId = userId)
	}


	//Recipe
	override suspend fun createRecipe(recipe : RecipeRequest): Response<Recipe> {
		return apiInterface.createRecipe(recipe = recipe)
	}
	override suspend fun getRecipe(recipeId: Int): Response<Recipe> {
		return apiInterface.getRecipe(recipeId = recipeId)
	}
	override suspend fun getRecipesPaginated(page: Int): Response<RecipeList> {
		return apiInterface.getRecipePaginated(page = page)
	}

	override suspend fun getRecipesByTitleAndTags(
		string: String,
		page: Int
	): Response<RecipeList> {
		return apiInterface.getRecipesByTitleAndTags(string = string,page = page)
	}

	override suspend fun updateRecipe(recipeId: Int, recipe: RecipeRequest): Response<Recipe> {
		return apiInterface.updateRecipe(recipeId=recipeId, recipe = recipe )
	}
	override suspend fun deleteRecipe(recipeId: Int): Response<String> {
		return apiInterface.deleteRecipe(recipeId = recipeId)
	}

	override suspend fun addLike(recipeId: Int): Response<Unit> {
		return apiInterface.addLike(recipeId = recipeId)
	}

	override suspend fun removeLike(recipeId: Int): Response<Unit> {
		return apiInterface.removeLike(recipeId = recipeId)
	}

	override suspend fun addSave(recipeId: Int): Response<Unit> {
		return apiInterface.addSave(recipeId = recipeId)
	}

	override suspend fun removeSave(recipeId: Int): Response<Unit> {
		return apiInterface.removeSave(recipeId = recipeId)
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
	override suspend fun getFollowers(): Response<FollowList> {
		return apiInterface.getFollowers()
	}
	override suspend fun getFolloweds(): Response<FollowList> {
		return apiInterface.getFolloweds()
	}
}