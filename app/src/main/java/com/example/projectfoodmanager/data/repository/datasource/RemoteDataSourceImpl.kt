package com.example.projectfoodmanager.data.repository.datasource


import com.example.projectfoodmanager.data.api.ApiInterface
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryRequest
import com.example.projectfoodmanager.data.model.modelRequest.RecipeRequest
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryPatchRequest
import com.example.projectfoodmanager.data.model.modelRequest.comment.CreateCommentRequest
import com.example.projectfoodmanager.data.model.modelResponse.FollowerResponse
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderDatedEntryList
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntry
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntryList
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderIngredientList
import com.example.projectfoodmanager.data.model.modelResponse.comment.Comment
import com.example.projectfoodmanager.data.model.modelResponse.comment.CommentList
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

	override suspend fun getRecipesPaginatedSorted(page: Int,by:String): Response<RecipeList> {
		return apiInterface.getRecipePaginatedSorted(page = page,by = by)
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

	override suspend fun getUserLikedRecipes(): Response<RecipeList> {
		return apiInterface.getUserLikedRecipes()
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
	override suspend fun createComments(recipeId: Int,comment : CreateCommentRequest): Response<Comment> {
		return apiInterface.createComments(recipeId= recipeId,comment = comment)
	}
	override suspend fun getCommentsByRecipePaginated(recipeId: Int,page: Int): Response<CommentList> {
		return apiInterface.getCommentsByRecipe(recipeId = recipeId,page = page)
	}
	override suspend fun getCommentsByUser(userId: Int): Response<Comment> {
		return apiInterface.getCommentsByUser(userId = userId)
	}
	override suspend fun updateComment(commentId: Int,comment : Comment): Response<Comment> {
		return apiInterface.updateComments(commentId=commentId,comment = comment)
	}
	override suspend fun deleteComment(commentId: Int): Response<Unit> {
		return apiInterface.deleteComments(commentId= commentId)
	}

	//Calender
	override suspend fun createCalenderEntry(recipeId: Int,comment : CalenderEntryRequest): Response<Unit> {
		return apiInterface.createCalenderEntry(recipeId= recipeId,calenderEntryRequest = comment)
	}

	override suspend fun getEntryOnCalender(fromDate: String,toDate: String): Response<CalenderDatedEntryList> {
		return apiInterface.getEntryOnCalender(fromDate = fromDate,toDate = toDate)
	}

	override suspend fun getEntryOnCalender(date: String): Response<CalenderEntryList> {
		return apiInterface.getEntryOnCalender(date = date)
	}

	override suspend fun getCalenderIngredients(fromDate: String, toDate: String): Response<CalenderIngredientList> {
		return apiInterface.getCalenderIngredients(fromDate = fromDate,toDate = toDate)
	}

	override suspend fun deleteCalenderEntry(calenderEntryId: Int): Response<Unit> {
		return apiInterface.deleteCalenderEntry(calenderEntryId)
	}

	override suspend fun pathCalenderEntry(calenderEntryId: Int,calenderPatchRequest : CalenderEntryPatchRequest): Response<CalenderEntry> {
		return apiInterface.pathCalenderEntry(calenderEntryId,calenderPatchRequest)
	}

	//Followers
	override suspend fun createFollower( userSenderId: Int, userReceiverId: Int): Response<FollowerResponse> {
		return apiInterface.createFollower(userSenderId = userSenderId,userReceiverId = userReceiverId)
	}
	override suspend fun getFollowers(userId: Int): Response<FollowList> {

		if(userId==-1)
			return apiInterface.getFollowers()

		return apiInterface.getFollowersByUser(userId)
	}
	override suspend fun getFolloweds(userId: Int): Response<FollowList> {
		if(userId==-1)
			return apiInterface.getFolloweds()

		return apiInterface.getFollowedsByUser(userId)
	}

	override suspend fun getFollowRequests(): Response<FollowList> {
		return apiInterface.getFollowRequests()
	}
}