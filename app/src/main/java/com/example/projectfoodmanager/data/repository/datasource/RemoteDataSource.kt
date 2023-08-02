package com.example.projectfoodmanager.data.repository.datasource


import com.example.projectfoodmanager.data.model.modelRequest.CalenderEntryRequest
import com.example.projectfoodmanager.data.model.modelRequest.RecipeRequest
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelRequest.comment.CreateCommentRequest
import com.example.projectfoodmanager.data.model.modelResponse.FollowerResponse
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderDatedEntryList
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntryList
import com.example.projectfoodmanager.data.model.modelResponse.comment.Comment
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
	suspend fun getRecipesPaginatedSorted(page: Int,by:String): Response<RecipeList>
	suspend fun getRecipesByTitleAndTags(string: String, page: Int): Response<RecipeList>
	suspend fun updateRecipe(recipeId: Int,recipe: RecipeRequest): Response<Recipe>
	suspend fun deleteRecipe(recipeId: Int): Response<String>
	suspend fun getUserLikedRecipes(): Response<RecipeList>
	suspend fun addLike(recipeId: Int): Response<Unit>
	suspend fun removeLike(recipeId: Int): Response<Unit>
	suspend fun addSave(recipeId: Int): Response<Unit>
	suspend fun removeSave(recipeId: Int): Response<Unit>

	//comments
	suspend fun createComments(recipeId: Int,comment: CreateCommentRequest): Response<Comment>
	suspend fun getCommentsByUser(userId: Int): Response<Comment>
	suspend fun getCommentsByRecipePaginated(recipeId: Int,page: Int): Response<CommentList>
	suspend fun updateComment(commentId: Int, comment: Comment): Response<Comment>
	suspend fun deleteComment(commentId: Int): Response<Unit>

	//calender
	suspend fun createCalenderEntry(recipeId: Int,comment : CalenderEntryRequest): Response<Unit>
	suspend fun getEntryOnCalender(date: String):  Response<CalenderEntryList>
	suspend fun getEntryOnCalender(fromDate: String, toDate: String):  Response<CalenderDatedEntryList>

	//followers
	suspend fun createFollower( userSenderId: Int, userReceiverId: Int): Response<FollowerResponse>
	suspend fun getFollowers(): Response<FollowList>
	suspend fun getFolloweds(): Response<FollowList>




}