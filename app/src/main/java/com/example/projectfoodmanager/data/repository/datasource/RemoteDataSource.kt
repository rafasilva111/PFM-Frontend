package com.example.projectfoodmanager.data.repository.datasource


import com.example.projectfoodmanager.data.model.modelRequest.CommentRequest
import com.example.projectfoodmanager.data.model.modelRequest.RecipeRequest
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.CommentResponse
import com.example.projectfoodmanager.data.model.modelResponse.FollowerResponse
import com.example.projectfoodmanager.data.model.modelResponse.comment.CommentList
import com.example.projectfoodmanager.data.model.modelResponse.follows.FollowList
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeList
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
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
	suspend fun updateUser(user: UserRequest): Response<User>
	suspend fun deleteUser(userId: Int): Response<String>

	//recipe
	suspend fun createRecipe(recipe : RecipeRequest): Response<Recipe>
	suspend fun getRecipe(recipeId: Int): Response<Recipe>
	suspend fun getRecipesPaginated(page: Int): Response<RecipeList>
	suspend fun getRecipesByTitleAndTags(string: String, page: Int): Response<RecipeList>
	suspend fun updateRecipe(recipeId: Int,recipe: RecipeRequest): Response<Recipe>
	suspend fun deleteRecipe(recipeId: Int): Response<String>
	suspend fun addLike(recipeId: Int): Response<Unit>
	suspend fun removeLike(recipeId: Int): Response<Unit>
	suspend fun addSave(recipeId: Int): Response<Unit>
	suspend fun removeSave(recipeId: Int): Response<Unit>

	//comments
	suspend fun createComments(comments: CommentRequest): Response<CommentResponse>
	suspend fun getCommentsByUser(userId: String): Response<CommentResponse>
	suspend fun getCommentsByRecipe(recipeId: Int): Response<CommentList>
	suspend fun updateComments(userId: String, comments : CommentRequest): Response<CommentResponse>
	suspend fun deleteComments(recipeId: String, userId: String): Response<String>

	//followers
	suspend fun createFollower( userSenderId: String, userReceiverId: String): Response<FollowerResponse>
	suspend fun getFollowers(): Response<FollowList>
	suspend fun getFolloweds(): Response<FollowList>

}