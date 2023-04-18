package com.example.projectfoodmanager.data.repository.datasource


import com.example.projectfoodmanager.data.model.modelRequest.CommentRequest
import com.example.projectfoodmanager.data.model.modelRequest.RecipeRequest
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.CommentResponse
import com.example.projectfoodmanager.data.model.modelResponse.FollowerResponse
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeListResponse
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeResponse
import com.example.projectfoodmanager.data.model.modelResponse.user.UserAuthResponse
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import retrofit2.Response

interface RemoteDataSource {

	//user
	suspend fun registerUser(user: UserRequest) : Response<Unit>
	suspend fun loginUser(email: String, password: String) : Response<UserAuthResponse>
	suspend fun logoutUser() : Response<String>
	suspend fun getUserAuth() : Response<User>
	suspend fun getUserById(userId: Int): Response<UserAuthResponse>
	suspend fun updateUser(userId: Int,user: UserRequest): Response<UserAuthResponse>
	suspend fun deleteUser(userId: Int): Response<String>

	//recipe
	suspend fun createRecipe(recipe : RecipeRequest): Response<RecipeResponse>
	suspend fun getRecipe(recipeId: Int): Response<RecipeResponse>
	suspend fun getRecipesPaginated(page: Int): Response<RecipeListResponse>
	suspend fun getRecipesByTitleAndTags(string: String, page: Int): Response<RecipeListResponse>
	suspend fun updateRecipe(recipeId: Int,recipe: RecipeRequest): Response<RecipeResponse>
	suspend fun deleteRecipe(recipeId: Int): Response<String>
	suspend fun addLike(recipeId: Int): Response<Unit>
	suspend fun removeLike(recipeId: Int): Response<Unit>

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