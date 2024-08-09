package com.example.projectfoodmanager.data.repository.datasource


import com.example.projectfoodmanager.data.api.ApiInterface
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryRequest
import com.example.projectfoodmanager.data.model.modelRequest.comment.CommentDTO
import com.example.projectfoodmanager.data.model.modelResponse.recipe.comment.Comment
import com.example.projectfoodmanager.data.model.modelResponse.recipe.comment.CommentList
import com.example.projectfoodmanager.data.model.modelRequest.user.UserRequest
import com.example.projectfoodmanager.data.model.modelRequest.user.goal.GoalDTO
import com.example.projectfoodmanager.data.model.modelRequest.RecipeRequest
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryCheckListRequest
import com.example.projectfoodmanager.data.model.modelRequest.calender.shoppingList.ShoppingListRequest
import com.example.projectfoodmanager.data.model.modelRequest.geral.IdListRequest
import com.example.projectfoodmanager.data.model.modelResponse.FollowerResponse
import com.example.projectfoodmanager.data.model.modelResponse.IdResponse
import com.example.projectfoodmanager.data.model.modelResponse.auth.RefreshToken
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderDatedEntryList
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntry
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntryList
import com.example.projectfoodmanager.data.model.modelResponse.follows.UsersToFollowList
import com.example.projectfoodmanager.data.model.modelResponse.miscellaneous.ApplicationReport
import com.example.projectfoodmanager.data.model.notification.Notification
import com.example.projectfoodmanager.data.model.notification.NotificationList
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeList
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ListOfShoppingLists
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ShoppingList
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ShoppingListSimplified
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.data.model.modelResponse.user.auth.AuthToken
import com.example.projectfoodmanager.data.model.modelResponse.user.profile.UserProfile
import com.example.projectfoodmanager.data.model.user.UserList
import com.example.projectfoodmanager.data.model.modelResponse.user.recipeBackground.UserRecipesBackground
import com.example.projectfoodmanager.data.model.user.goal.FitnessReport
import com.example.projectfoodmanager.data.model.user.goal.Goal
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
	private val apiInterface: ApiInterface
) : RemoteDataSource {

	//User
	override suspend fun registerUser(user: UserRequest): Response<Unit> {
		return apiInterface.createUser(user = user)
	}

	override suspend fun loginUser(email: String, password: String): Response<AuthToken> {
		return apiInterface.loginUser(UserRequest(email = email, password = password))
	}

	override suspend fun logoutUser(logoutRequest: RefreshToken): Response<Unit> {
		return apiInterface.logoutUser(logoutRequest)
	}

	override suspend fun getUserAuth(): Response<User> {
		return apiInterface.getUserSession()
	}

	override suspend fun getUserById(userId: Int): Response<UserProfile> {
		return apiInterface.getUser(userId = userId)
	}
	override suspend fun updateUser(user: UserRequest): Response<User> {
		return apiInterface.updateUser(user = user )
	}
	override suspend fun deleteUser(): Response<String> {
		return apiInterface.deleteUser()
	}

	override suspend fun getUserRecipesBackground(): Response<UserRecipesBackground> {
		return apiInterface.getUserRecipesBackground()
	}

	//Recipe
	override suspend fun createRecipe(recipe : RecipeRequest): Response<Recipe> {
		return apiInterface.createRecipe(recipe = recipe)
	}
	override suspend fun getRecipe(recipeId: Int): Response<Recipe> {
		return apiInterface.getRecipe(recipeId = recipeId)
	}

	override suspend fun getRecipes(userId: Int?,by: String, searchString: String, searchTag: String, page: Int,pageSize: Int): Response<RecipeList> {
		return apiInterface.getRecipePaginated(userId = userId,page = page,searchString = searchString, searchTag=searchTag,by=by,pageSize = pageSize)
	}

	override suspend fun updateRecipe(recipeId: Int, recipe: RecipeRequest): Response<Recipe> {
		return apiInterface.updateRecipe(recipeId=recipeId, recipe = recipe )
	}
	override suspend fun deleteRecipe(recipeId: Int): Response<String> {
		return apiInterface.deleteRecipe(recipeId = recipeId)
	}

	override suspend fun getLikedRecipes(page: Int,pageSize: Int,searchString: String, searchTag: String): Response<RecipeList> {
		return apiInterface.getLikedRecipes(page,pageSize,searchString,searchTag)
	}

	override suspend fun addLike(recipeId: Int): Response<Recipe> {
		return apiInterface.addLike(recipeId = recipeId)
	}

	override suspend fun removeLike(recipeId: Int): Response<Recipe> {
		return apiInterface.removeLike(recipeId = recipeId)
	}

	override suspend fun addSave(recipeId: Int): Response<Recipe> {
		return apiInterface.addSave(recipeId = recipeId)
	}

	override suspend fun removeSave(recipeId: Int): Response<Recipe> {
		return apiInterface.removeSave(recipeId = recipeId)
	}

	/**
	 * Comments
	 * */

	/** General */

	override suspend fun getComment(commentId: Int): Response<Comment> {
		return apiInterface.getComment(commentId= commentId)
	}

	override suspend fun createComments(recipeId: Int,comment : CommentDTO): Response<Comment> {
		return apiInterface.createComments(recipeId= recipeId,comment = comment)
	}

	override suspend fun patchComment(commentId: Int,comment : CommentDTO): Response<Comment> {
		return apiInterface.patchComment(commentId=commentId,comment = comment)
	}
	override suspend fun deleteComment(commentId: Int): Response<Comment> {
		return apiInterface.deleteComment(commentId= commentId)
	}

	override suspend fun getComments(recipeId: Int?,clientId: Int?, page: Int, pageSize: Int): Response<CommentList> {
		return apiInterface.getComments(recipeId = recipeId, clientId = clientId,page = page,pageSize = pageSize)
	}



	/** Like Function */
	override suspend fun postLikeOnComment(commentId: Int): Response<Comment> {
		return apiInterface.postLikeOnComment(commentId= commentId)
	}

	override suspend fun deleteLikeOnComment(commentId: Int): Response<Comment> {
		return apiInterface.deleteLikeOnComment(commentId= commentId)
	}





	//Calender
	override suspend fun createCalenderEntry(comment : CalenderEntryRequest): Response<CalenderEntry> {
		return apiInterface.createCalenderEntry(calenderEntryRequest = comment)
	}

	override suspend fun getEntryOnCalender(fromDate: String,toDate: String): Response<CalenderDatedEntryList> {
		return apiInterface.getEntryOnCalender(fromDate = fromDate,toDate = toDate)
	}

	override suspend fun getEntryOnCalender(date: String): Response<CalenderEntryList> {
		return apiInterface.getEntryOnCalender(date = date)
	}

	override suspend fun getCalenderIngredients(fromDate: String, toDate: String): Response<ShoppingListSimplified> {
		return apiInterface.getCalenderIngredients(fromDate = fromDate,toDate = toDate)
	}

	override suspend fun deleteCalenderEntry(calenderEntryId: Int): Response<Int> {
		return apiInterface.deleteCalenderEntry(calenderEntryId)
	}

	override suspend fun patchCalenderEntry(calenderEntryId: Int, calenderPatchRequest : CalenderEntryRequest): Response<CalenderEntry> {
		return apiInterface.patchCalenderEntry(calenderEntryId,calenderPatchRequest)
	}

	override suspend fun checkCalenderEntries(calenderEntryCheckListRequest: CalenderEntryCheckListRequest): Response<Unit> {
		return apiInterface.checkCalenderEntries(calenderEntryCheckListRequest)
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

	override suspend fun getNotifications(page: Int?, pageSize: Int?, lastId: Int?): Response<NotificationList> {
		return apiInterface.getNotifications(page,pageSize,lastId)
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

	override suspend fun getFitnessReport(): Response<FitnessReport> {
		return apiInterface.getFitnessReport()
	}

	override suspend fun createFitnessGoal(goalDTO: GoalDTO): Response<Goal> {
		return apiInterface.createFitnessGoal(goalDTO)
	}
}