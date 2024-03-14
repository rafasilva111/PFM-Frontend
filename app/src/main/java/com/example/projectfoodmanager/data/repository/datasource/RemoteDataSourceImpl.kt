package com.example.projectfoodmanager.data.repository.datasource


import com.example.projectfoodmanager.data.api.ApiInterface
import com.example.projectfoodmanager.data.model.dtos.user.UserDTO
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryRequest
import com.example.projectfoodmanager.data.model.modelRequest.RecipeRequest
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryListUpdate
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryPatchRequest
import com.example.projectfoodmanager.data.model.modelRequest.calender.shoppingList.ShoppingListRequest
import com.example.projectfoodmanager.data.model.modelRequest.comment.CreateCommentRequest
import com.example.projectfoodmanager.data.model.modelRequest.geral.IdListRequest
import com.example.projectfoodmanager.data.model.modelResponse.FollowerResponse
import com.example.projectfoodmanager.data.model.modelResponse.IdResponse
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderDatedEntryList
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntry
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntryList
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ShoppingList
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ListOfShoppingLists
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ShoppingListSimplefied
import com.example.projectfoodmanager.data.model.modelResponse.comment.Comment
import com.example.projectfoodmanager.data.model.modelResponse.comment.CommentList
import com.example.projectfoodmanager.data.model.modelResponse.follows.UsersToFollowList
import com.example.projectfoodmanager.data.model.modelResponse.miscellaneous.ApplicationReport
import com.example.projectfoodmanager.data.model.modelResponse.notifications.Notification
import com.example.projectfoodmanager.data.model.modelResponse.notifications.NotificationList
import com.example.projectfoodmanager.data.model.user.UserList
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeList
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe

import com.example.projectfoodmanager.data.model.user.UserAuthResponse
import com.example.projectfoodmanager.data.model.user.User
import com.example.projectfoodmanager.data.model.user.UserRecipeBackgrounds
import com.example.projectfoodmanager.data.model.user.goal.FitnessReport
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
	private val apiInterface: ApiInterface
) : RemoteDataSource {

	//User
	override suspend fun registerUser(user: UserDTO): Response<Unit> {
		return apiInterface.createUser(user = user)
	}

	override suspend fun loginUser(email: String, password: String): Response<UserAuthResponse> {
		return apiInterface.loginUser(UserDTO(email = email, password = password))
	}

	override suspend fun logoutUser(): Response<String> {
		return apiInterface.logoutUser()
	}

	override suspend fun getUserAuth(): Response<User> {
		return apiInterface.getUserSession()
	}

	override suspend fun getUserById(userId: Int): Response<User> {
		return apiInterface.getUser(userId = userId)
	}
	override suspend fun updateUser(user: UserDTO): Response<User> {
		return apiInterface.updateUser(user = user )
	}
	override suspend fun deleteUser(): Response<String> {
		return apiInterface.deleteUser()
	}

	override suspend fun getUserRecipesBackground(): Response<UserRecipeBackgrounds> {
		return apiInterface.getUserRecipesBackground()
	}

	//Recipe
	override suspend fun createRecipe(recipe : RecipeRequest): Response<Recipe> {
		return apiInterface.createRecipe(recipe = recipe)
	}
	override suspend fun getRecipe(recipeId: Int): Response<Recipe> {
		return apiInterface.getRecipe(recipeId = recipeId)
	}

	override suspend fun getRecipesByClientPaginated(clientId: Int, page: Int): Response<RecipeList> {
		return apiInterface.getRecipesByClientPaginated(clientId = clientId,page = page)
	}

    override suspend fun getRecipesByClientSearchPaginated(clientId: Int, string: String, page: Int): Response<RecipeList> {
		return apiInterface.getRecipesByClientSearchPaginated(clientId = clientId, string =  string,page = page)
    }

	override suspend fun getRecipes(userId: Int,by: String, searchString: String, searchTag: String, page: Int,pageSize: Int): Response<RecipeList> {
		return apiInterface.getRecipePaginated(userId = userId,page = page,searchString = searchString, searchTag=searchTag,by=by,pageSize = pageSize)
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

    override suspend fun getSizedCommentsByRecipePaginated(recipeId: Int, page: Int, pageSize: Int): Response<CommentList> {
		return apiInterface.getSizedCommentsByRecipePaginated(recipeId = recipeId,page = page,pageSize=pageSize)
    }

	override suspend fun updateComment(commentId: Int,comment : Comment): Response<Comment> {
		return apiInterface.updateComments(commentId=commentId,comment = comment)
	}
	override suspend fun deleteComment(commentId: Int): Response<Unit> {
		return apiInterface.deleteComments(commentId= commentId)
	}

	override suspend fun getCommentsByClientPaginated(clientId: Int,page: Int): Response<CommentList> {
		return apiInterface.getCommentsByClientPaginated(clientId = clientId,page = page)
	}

	//Calender
	override suspend fun createCalenderEntry(recipeId: Int,comment : CalenderEntryRequest): Response<CalenderEntry> {
		return apiInterface.createCalenderEntry(recipeId= recipeId,calenderEntryRequest = comment)
	}

	override suspend fun getEntryOnCalender(fromDate: String,toDate: String): Response<CalenderDatedEntryList> {
		return apiInterface.getEntryOnCalender(fromDate = fromDate,toDate = toDate)
	}

	override suspend fun getEntryOnCalender(date: String): Response<CalenderEntryList> {
		return apiInterface.getEntryOnCalender(date = date)
	}

	override suspend fun getCalenderIngredients(fromDate: String, toDate: String): Response<ShoppingListSimplefied> {
		return apiInterface.getCalenderIngredients(fromDate = fromDate,toDate = toDate)
	}

	override suspend fun deleteCalenderEntry(calenderEntryId: Int): Response<Int> {
		return apiInterface.deleteCalenderEntry(calenderEntryId)
	}

	override suspend fun patchCalenderEntry(calenderEntryId: Int, calenderPatchRequest : CalenderEntryPatchRequest): Response<CalenderEntry> {
		return apiInterface.patchCalenderEntry(calenderEntryId,calenderPatchRequest)
	}

	override suspend fun checkCalenderEntries(calenderEntryListUpdate: CalenderEntryListUpdate): Response<Unit> {
		return apiInterface.checkCalenderEntries(calenderEntryListUpdate)
	}

	/** Follows  */

	override suspend fun createFollower( userSenderId: Int, userReceiverId: Int): Response<FollowerResponse> {
		return apiInterface.createFollower(userSenderId = userSenderId,userReceiverId = userReceiverId)
	}

	override suspend fun getFollowers(userId: Int): Response<UserList> {
		if(userId==-1)
			return apiInterface.getFollowers()

		return apiInterface.getFollowersByUser(userId)
	}
	override suspend fun getFolloweds(userId: Int): Response<UserList> {
		if(userId==-1)
			return apiInterface.getFolloweds()

		return apiInterface.getFollowedsByUser(userId)
	}

	override suspend fun deleteFollower(userId: Int): Response<Unit> {
		return apiInterface.deleteFollower(userId)
	}

	override suspend fun deleteFollow(userId: Int): Response<Unit> {
		return apiInterface.deleteFollow(userId)
	}

	/** Follows Requests */

	override suspend fun getUsersToFollow(searchString:String?,page: Int?,pageSize:Int?): Response<UsersToFollowList> {
		return apiInterface.getUsersToFollow(searchString,page,pageSize)
	}

	override suspend fun getFollowRequests(pageSize: Int): Response<UserList> {
		return apiInterface.getFollowRequests(pageSize)
	}

	override suspend fun postFollowRequest(userId: Int): Response<Unit> {
		return apiInterface.postFollowRequest(userId)
	}

	override suspend fun postAcceptFollowRequest(userId: Int): Response<Unit> {
		return apiInterface.postAcceptFollowRequest(userId)
	}

	override suspend fun deleteFollowRequest(userId: Int): Response<Unit> {
		return apiInterface.deleteFollowRequest(userId)
	}

	// shopping list
	//get
	override suspend fun getShoppingList(): Response<ListOfShoppingLists> {
		return apiInterface.getShoppingList()
	}

	override suspend fun getShoppingList(shoppingListId: Int): Response<ShoppingList> {
		return apiInterface.getShoppingList(shoppingListId)
	}

	// post
	override suspend fun postShoppingList(shoppingListRequest: ShoppingListRequest): Response<ShoppingList> {
		return apiInterface.postShoppingList(shoppingListRequest)
	}

	// put
	override suspend fun putShoppingList(shoppingListId: Int, shoppingListRequest: ShoppingListRequest): Response<ShoppingList> {
		return apiInterface.putShoppingList(shoppingListId,shoppingListRequest)
	}

	// delete
	override suspend fun deleteShoppingList(shoppingListId: Int): Response<IdResponse> {
		return apiInterface.deleteShoppingList(shoppingListId)
	}

	/** Notifications */

	override suspend fun getNotifications(page: Int?, pageSize: Int?): Response<NotificationList> {
		return apiInterface.getNotifications(page,pageSize)
	}

	override suspend fun getNotification(id: Int?) : Response<Notification>{
		return apiInterface.getNotification(id)
	}

	override suspend fun putNotification(id: Int?, notification: Notification): Response<Unit> {
		return apiInterface.putNotification(id,notification)
	}

	override suspend fun putNotifications(idListRequest: IdListRequest): Response<Unit> {
		return apiInterface.putNotifications(idListRequest)
	}

	override suspend fun deleteNotification(id: Int?):Response<Unit> {
		return apiInterface.deleteNotification(id)
	}
	override suspend fun deleteNotifications(idListRequest: IdListRequest):Response<Unit> {
		return apiInterface.deleteNotifications(idListRequest)
	}

	/** Application report */

	override suspend fun postAppReport(applicationReport: ApplicationReport): Response<Unit> {
		return apiInterface.postAppReport(applicationReport)
	}

	/** Goal */

	override suspend fun getIdealWeight(): Response<FitnessReport> {
		return apiInterface.getFitnessReport()
	}
}