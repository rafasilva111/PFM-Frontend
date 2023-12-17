package com.example.projectfoodmanager.data.repository.datasource


import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryRequest
import com.example.projectfoodmanager.data.model.modelRequest.RecipeRequest
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
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
import com.example.projectfoodmanager.data.model.modelResponse.user.UserList
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeList
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.user.UserAuthResponse
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.data.model.modelResponse.user.UserRecipeBackgrounds
import retrofit2.Response

interface RemoteDataSource {

	//user
	suspend fun registerUser(user: UserRequest) : Response<Unit>
	suspend fun loginUser(email: String, password: String) : Response<UserAuthResponse>
	suspend fun logoutUser() : Response<String>
	suspend fun getUserAuth() : Response<User>
	suspend fun getUserById(userId: Int): Response<UserAuthResponse>
	suspend fun updateUser(user: UserRequest): Response<User>
	suspend fun deleteUser(): Response<String>
	suspend fun getUserRecipesBackground(): Response<UserRecipeBackgrounds>

	//recipe
	suspend fun createRecipe(recipe : RecipeRequest): Response<Recipe>
	suspend fun getRecipe(recipeId: Int): Response<Recipe>

	suspend fun getRecipes(by: String, searchString: String, searchTag: String, page: Int): Response<RecipeList>

	suspend fun updateRecipe(recipeId: Int,recipe: RecipeRequest): Response<Recipe>
	suspend fun deleteRecipe(recipeId: Int): Response<String>

	suspend fun getUserLikedRecipes(): Response<RecipeList>

	suspend fun addLike(recipeId: Int): Response<Unit>
	suspend fun removeLike(recipeId: Int): Response<Unit>
	suspend fun addSave(recipeId: Int): Response<Unit>
	suspend fun removeSave(recipeId: Int): Response<Unit>

	suspend fun getRecipesByClientPaginated(clientId: Int, page: Int): Response<RecipeList>
	suspend fun getRecipesByClientSearchPaginated(clientId: Int, string: String, page: Int): Response<RecipeList>


	//comments
	suspend fun createComments(recipeId: Int,comment: CreateCommentRequest): Response<Comment>
	suspend fun getCommentsByRecipePaginated(recipeId: Int,page: Int): Response<CommentList>
	suspend fun getSizedCommentsByRecipePaginated(recipeId: Int, page: Int, pageSize: Int): Response<CommentList>
	suspend fun updateComment(commentId: Int, comment: Comment): Response<Comment>
	suspend fun deleteComment(commentId: Int): Response<Unit>
	suspend fun getCommentsByClientPaginated(clientId: Int, page: Int): Response<CommentList>


	//calender
	suspend fun createCalenderEntry(recipeId: Int,comment : CalenderEntryRequest): Response<CalenderEntry>
	suspend fun getEntryOnCalender(date: String):  Response<CalenderEntryList>
	suspend fun getEntryOnCalender(fromDate: String, toDate: String):  Response<CalenderDatedEntryList>
	suspend fun getCalenderIngredients(fromDate: String, toDate: String): Response<ShoppingListSimplefied>
	suspend fun deleteCalenderEntry(calenderEntryId: Int): Response<Unit>
	suspend fun patchCalenderEntry(calenderEntryId: Int, calenderPatchRequest : CalenderEntryPatchRequest): Response<CalenderEntry>
	suspend fun checkCalenderEntries(calenderEntryListUpdate: CalenderEntryListUpdate): Response<Unit>

	//followers
	suspend fun createFollower( userSenderId: Int, userReceiverId: Int): Response<FollowerResponse>
	suspend fun getFollowers(userId: Int): Response<UserList>
	suspend fun getFolloweds(userId: Int): Response<UserList>
	suspend fun getFollowRequests(pageSize: Int): Response<UserList>
	suspend fun postAcceptFollowRequest(userId: Int): Response<Unit>
    suspend fun deleteFollowRequest( userId: Int): Response<Unit>
    suspend fun deleteFollower(userId: Int): Response<Unit>
	suspend fun deleteFollow(userId: Int): Response<Unit>
	suspend fun postFollowRequest(userId: Int): Response<Unit>
	suspend fun getUsersToFollow(searchString:String?,page: Int?,pageSize:Int?): Response<UsersToFollowList>

	/** Shopping list */
	suspend fun getShoppingList() : Response<ListOfShoppingLists>
	suspend fun getShoppingList(shoppingListId: Int): Response<ShoppingList>
	suspend fun postShoppingList(shoppingListRequest: ShoppingListRequest): Response<ShoppingList>
	suspend fun putShoppingList(shoppingListId: Int, shoppingListRequest: ShoppingListRequest): Response<ShoppingList>
	suspend fun deleteShoppingList(shoppingListId: Int): Response<IdResponse>

	/** Notifications */
	suspend fun getNotifications(page: Int?, pageSize: Int?): Response<NotificationList>
	suspend fun getNotification(id: Int?): Response<Notification>
	suspend fun putNotification(id: Int?, notification: Notification): Response<Unit>
	suspend fun putNotifications(idListRequest: IdListRequest): Response<Unit>
	suspend fun deleteNotification(id: Int?): Response<Unit>
	suspend fun deleteNotifications(idListRequest: IdListRequest): Response<Unit>

	/** Application report */
	suspend fun postAppReport(applicationReport: ApplicationReport): Response<Unit>



}