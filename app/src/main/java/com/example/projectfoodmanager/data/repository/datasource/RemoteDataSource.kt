package com.example.projectfoodmanager.data.repository.datasource


import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryRequest
import com.example.projectfoodmanager.data.model.modelRequest.comment.CommentDTO
import com.example.projectfoodmanager.data.model.modelResponse.recipe.comment.Comment
import com.example.projectfoodmanager.data.model.modelResponse.recipe.comment.CommentList
import com.example.projectfoodmanager.data.model.modelRequest.user.UserRequest
import com.example.projectfoodmanager.data.model.modelRequest.user.goal.GoalDTO
import com.example.projectfoodmanager.data.model.modelRequest.recipe.RecipeRequest
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryCheckListRequest
import com.example.projectfoodmanager.data.model.modelRequest.calender.shoppingList.ShoppingListRequest
import com.example.projectfoodmanager.data.model.modelRequest.geral.IdListRequest
import com.example.projectfoodmanager.data.model.modelRequest.recipe.rating.RecipeRatingRequest
import com.example.projectfoodmanager.data.model.modelResponse.Id
import com.example.projectfoodmanager.data.model.modelResponse.PaginatedList
import com.example.projectfoodmanager.data.model.modelResponse.auth.RefreshToken
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderDatedEntryList
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntry
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntryList
import com.example.projectfoodmanager.data.model.modelResponse.follows.FollowerRequest
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
import com.example.projectfoodmanager.data.model.modelResponse.recipe.rating.RecipeRating
import com.example.projectfoodmanager.data.model.modelResponse.recipe.rating.RecipeRatingList
import com.example.projectfoodmanager.data.model.user.goal.FitnessReport
import com.example.projectfoodmanager.data.model.user.goal.Goal
import retrofit2.Response

interface RemoteDataSource {

	/**
	 * User
	 * */

	suspend fun registerUser(user: UserRequest) : Response<Unit>
	suspend fun loginUser(email: String, password: String) : Response<AuthToken>
	suspend fun logoutUser(logoutRequest: RefreshToken) : Response<Unit>
	suspend fun getUserAuth() : Response<User>
	suspend fun getUserById(userId: Int): Response<UserProfile>
	suspend fun updateUser(user: UserRequest): Response<User>
	suspend fun deleteUser(): Response<String>
	suspend fun getUserRecipesBackground(): Response<UserRecipesBackground>

	/**
	 * Recipe
	 * */

	suspend fun createRecipe(recipe : RecipeRequest): Response<Recipe>
	suspend fun getRecipe(recipeId: Int): Response<Recipe>

	suspend fun getRecipes(userId: Int?,by: String, searchString: String, searchTag: String, page: Int,pageSize: Int): Response<RecipeList>

	suspend fun updateRecipe(recipeId: Int,recipe: RecipeRequest): Response<Recipe>
	suspend fun deleteRecipe(recipeId: Int): Response<String>

	/** Like Function */
	suspend fun getLikedRecipes(page: Int,pageSize: Int,searchString: String,searchTag:String): Response<RecipeList>
	suspend fun addLike(recipeId: Int): Response<Recipe>
	suspend fun removeLike(recipeId: Int): Response<Recipe>

	/** Save Function */
	suspend fun addSave(recipeId: Int): Response<Recipe>
	suspend fun removeSave(recipeId: Int): Response<Recipe>

	/** Rate Function */
	suspend	fun getRecipeRatings(page: Int, pageSize: Int): Response<RecipeRatingList>
	suspend fun postRecipeRating(recipeId: Int, rating: RecipeRatingRequest): Response<RecipeRating>
	suspend fun deleteRecipeRating(recipeId: Int): Response<RecipeRating>
	

	/**
	 * Comments
	 * */

	/** General */
	suspend fun getComment(commentId: Int): Response<Comment>
	suspend fun createComments(recipeId: Int,comment: CommentDTO): Response<Comment>
	suspend fun patchComment(commentId: Int, comment: CommentDTO): Response<Comment>
	suspend fun deleteComment(commentId: Int): Response<Comment>

	suspend fun getComments(recipeId: Int?,clientId: Int?, page: Int, pageSize: Int): Response<CommentList>

	/** Like Function */
	suspend fun deleteLikeOnComment(commentId: Int): Response<Comment>
	suspend fun postLikeOnComment(commentId: Int): Response<Comment>


	/**
	 * Calender
	 * */
	suspend fun createCalenderEntry(comment : CalenderEntryRequest): Response<CalenderEntry>
	suspend fun getEntryOnCalender(date: String):  Response<CalenderEntryList>
	suspend fun getEntryOnCalender(fromDate: String, toDate: String):  Response<CalenderDatedEntryList>
	suspend fun getCalenderIngredients(fromDate: String, toDate: String): Response<ShoppingListSimplified>
	suspend fun deleteCalenderEntry(calenderEntryId: Int): Response<Int>
	suspend fun patchCalenderEntry(calenderEntryId: Int, calenderPatchRequest : CalenderEntryRequest): Response<CalenderEntry>
	suspend fun checkCalenderEntries(calenderEntryCheckListRequest: CalenderEntryCheckListRequest): Response<Unit>

	//followers
	suspend fun createFollower( userSenderId: Int, userReceiverId: Int): Response<Unit>
	suspend fun getFollowers(page: Int?,pageSize: Int?,userId: Int?,searchString: String?): Response<UserList>
	suspend fun getFollows(page: Int?,pageSize: Int?,userId: Int?,searchString: String?): Response<UserList>
	suspend fun getFollowRequests(pageSize: Int): Response<PaginatedList<FollowerRequest>>
	suspend fun postAcceptFollowRequest(userId: Int): Response<Unit>
    suspend fun deleteFollowerRequest( followerId: Int): Response<Unit>
    suspend fun deleteFollowRequest(  followId: Int): Response<Unit>
    suspend fun deleteFollower(userId: Int): Response<Unit>
	suspend fun deleteFollow(userId: Int): Response<Unit>
	suspend fun postFollowRequest(userId: Int): Response<Unit>
	suspend fun getUsersToFollow(searchString:String?,page: Int?,pageSize:Int?): Response<UsersToFollowList>

	/** Shopping list */
	suspend fun getShoppingList() : Response<ListOfShoppingLists>
	suspend fun getShoppingList(shoppingListId: Int): Response<ShoppingList>
	suspend fun postShoppingList(shoppingListRequest: ShoppingListRequest): Response<ShoppingList>
	suspend fun putShoppingList(shoppingListId: Int, shoppingListRequest: ShoppingListRequest): Response<ShoppingList>
	suspend fun deleteShoppingList(shoppingListId: Int): Response<Id>

	/** Notifications */
	suspend fun getNotifications(page: Int?, pageSize: Int?, lastId: Int?): Response<NotificationList>
	suspend fun getNotification(id: Int?): Response<Notification>
	suspend fun putNotification(id: Int?, notification: Notification): Response<Unit>
	suspend fun putNotifications(idListRequest: IdListRequest): Response<Unit>
	suspend fun deleteNotification(id: Int?): Response<Unit>
	suspend fun deleteNotifications(idListRequest: IdListRequest): Response<Unit>

	/** Application report */
	suspend fun postAppReport(applicationReport: ApplicationReport): Response<Unit>

	/** Goal */
	suspend fun getFitnessReport(): Response<FitnessReport>
	suspend fun createFitnessGoal(goalDTO: GoalDTO): Response<Goal>



}