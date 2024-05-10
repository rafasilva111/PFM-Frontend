package com.example.projectfoodmanager.data.api


import com.example.projectfoodmanager.data.model.dtos.calender.CalenderEntryDTO
import com.example.projectfoodmanager.data.model.dtos.recipe.comment.CommentDTO
import com.example.projectfoodmanager.data.model.dtos.user.UserDTO
import com.example.projectfoodmanager.data.model.dtos.user.goal.GoalDTO
import com.example.projectfoodmanager.data.model.modelRequest.RecipeRequest
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryListUpdate
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryPatchRequest
import com.example.projectfoodmanager.data.model.modelRequest.calender.shoppingList.ShoppingListRequest
import com.example.projectfoodmanager.data.model.modelRequest.geral.IdListRequest
import com.example.projectfoodmanager.data.model.modelResponse.FollowerResponse
import com.example.projectfoodmanager.data.model.modelResponse.IdResponse
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderDatedEntryList
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntry
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntryList
import com.example.projectfoodmanager.data.model.modelResponse.follows.UsersToFollowList
import com.example.projectfoodmanager.data.model.modelResponse.miscellaneous.ApplicationReport
import com.example.projectfoodmanager.data.model.notification.Notification
import com.example.projectfoodmanager.data.model.notification.NotificationList
import com.example.projectfoodmanager.data.model.recipe.Recipe
import com.example.projectfoodmanager.data.model.recipe.RecipeList
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ListOfShoppingLists
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ShoppingList
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ShoppingListSimplefied
import com.example.projectfoodmanager.data.model.recipe.comment.Comment
import com.example.projectfoodmanager.data.model.recipe.comment.CommentList
import com.example.projectfoodmanager.data.model.user.User
import com.example.projectfoodmanager.data.model.user.UserAuthResponse
import com.example.projectfoodmanager.data.model.user.UserList
import com.example.projectfoodmanager.data.model.user.UserRecipeBackgrounds
import com.example.projectfoodmanager.data.model.user.goal.FitnessReport
import com.example.projectfoodmanager.data.model.user.goal.Goal
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    companion object{
        const val API_V1_BASE_URL = "api/v1"
    }

    //user
    @POST("$API_V1_BASE_URL/auth")
    suspend fun createUser(@Body user : UserDTO): Response<Unit>

    @POST("$API_V1_BASE_URL/auth/login")
    suspend fun loginUser(@Body user : UserDTO): Response<UserAuthResponse>

    @DELETE("$API_V1_BASE_URL/auth")
    suspend fun logoutUser(): Response<String>

    @GET("$API_V1_BASE_URL/auth")
    suspend fun getUserSession(): Response<User>

    @POST("$API_V1_BASE_URL/user")
    suspend fun patchUser(@Body user : UserDTO): Response<Unit>

    @GET("$API_V1_BASE_URL/user")
    suspend fun getUser(@Query("id") userId: Int): Response<User>

    @PATCH("$API_V1_BASE_URL/user")
    suspend fun updateUser(@Body user : UserDTO): Response<User>

    @DELETE("$API_V1_BASE_URL/user")
    suspend fun deleteUser(): Response<String>


    @GET("$API_V1_BASE_URL/auth/recipes")
    suspend fun getUserRecipesBackground(): Response<UserRecipeBackgrounds>


    //recipe
    @POST("$API_V1_BASE_URL/recipe")
    suspend fun createRecipe(@Body recipe : RecipeRequest): Response<Recipe>

    @GET("$API_V1_BASE_URL/recipe/list")
    suspend fun getRecipe(@Query("id") recipeId: Int): Response<Recipe>

    @GET("$API_V1_BASE_URL/recipe/list")
    suspend fun getRecipePaginated(@Query("user_id")userId: Int,@Query("by")by: String,@Query("searchString") searchString: String,
                                   @Query("searchTag") searchTag: String,@Query("page") page: Int,@Query("page_size") pageSize: Int): Response<RecipeList>

    @GET("$API_V1_BASE_URL/recipe/list")
    suspend fun getRecipesByClientPaginated(@Query("commented_by") clientId: Int,@Query("page") page: Int): Response<RecipeList>

    @GET("$API_V1_BASE_URL/recipe/list")
    suspend fun getRecipesByClientSearchPaginated(@Query("commented_by") clientId: Int,@Query("string") string: String,@Query("page") page: Int): Response<RecipeList>


    @PUT("$API_V1_BASE_URL/recipe")
    suspend fun updateRecipe(@Query("id") recipeId: Int,@Body recipe : RecipeRequest): Response<Recipe>

    @DELETE("$API_V1_BASE_URL/recipe")
    suspend fun deleteRecipe(@Query("id") recipeId: Int): Response<String>

    @GET("$API_V1_BASE_URL/recipe/likes")
    suspend fun getUserLikedRecipes(): Response<RecipeList>

    @POST("$API_V1_BASE_URL/recipe/like")
    suspend fun addLike(@Query("id") recipeId: Int): Response<Unit>

    @DELETE("$API_V1_BASE_URL/recipe/like")
    suspend fun removeLike(@Query("id") recipeId: Int): Response<Unit>

    @POST("$API_V1_BASE_URL/recipe/save")
    suspend fun addSave(@Query("id") recipeId: Int): Response<Unit>

    @DELETE("$API_V1_BASE_URL/recipe/save")
    suspend fun removeSave(@Query("id") recipeId: Int): Response<Unit>



    /**
     * Comments
     * */


    /** General */

    @GET("$API_V1_BASE_URL/comment")
    suspend fun getComment(@Query("id") commentId: Int): Response<Comment>

    @POST("$API_V1_BASE_URL/comment")
    suspend fun createComments(@Query("recipe_id") recipeId: Int,@Body comment : CommentDTO): Response<Comment>

    @PATCH("$API_V1_BASE_URL/comment")
    suspend fun patchComment(@Query("id") commentId: Int, @Body comment : CommentDTO): Response<Comment>

    @DELETE("$API_V1_BASE_URL/comment")
    suspend fun deleteComment(@Query("id") commentId: Int): Response<Comment>

    /** Special Gets */

    @GET("$API_V1_BASE_URL/comment/list")
    suspend fun getCommentsByRecipe(@Query("recipe_id") recipeId: Int,@Query("page") page: Int,@Query("page_size") pageSize: Int): Response<CommentList>

    @GET("$API_V1_BASE_URL/comment")
    suspend fun getCommentsByClient(@Query("user_id") clientId: Int, @Query("page") page: Int): Response<CommentList>

    /** Like Function */

    @POST("$API_V1_BASE_URL/comment/like")
    suspend fun postLikeOnComment(@Query("id") commentId: Int): Response<Comment>

    @DELETE("$API_V1_BASE_URL/comment/like")
    suspend fun deleteLikeOnComment(@Query("id") commentId: Int): Response<Comment>

    // calender

    @POST("$API_V1_BASE_URL/calendar")
    suspend fun createCalenderEntry(@Query("recipe_id") recipeId: Int,@Body calenderEntryRequest : CalenderEntryDTO): Response<CalenderEntry>

    @GET("$API_V1_BASE_URL/calendar/list")
    suspend fun getEntryOnCalender(@Query("date")date: String): Response<CalenderEntryList>

    @GET("$API_V1_BASE_URL/calendar/list")
    suspend fun getEntryOnCalender(@Query("from_date") fromDate:String,@Query("to_date") toDate: String): Response<CalenderDatedEntryList>

    @GET("$API_V1_BASE_URL/calendar/ingredients/list")
    suspend fun getCalenderIngredients(@Query("from_date") fromDate: String,@Query("to_date") toDate: String): Response<ShoppingListSimplefied>

    @DELETE("$API_V1_BASE_URL/calendar")
    suspend fun deleteCalenderEntry(@Query("id") calenderEntryId: Int): Response<Int>

    @PATCH("$API_V1_BASE_URL/calendar")
    suspend fun patchCalenderEntry(@Query("id") calenderEntryId: Int, @Body calenderPatchRequest : CalenderEntryPatchRequest): Response<CalenderEntry>

    @POST("$API_V1_BASE_URL/calendar/list/check")
    suspend fun checkCalenderEntries(@Body calenderEntryListUpdate: CalenderEntryListUpdate): Response<Unit>

    /** Follows */

    @GET("$API_V1_BASE_URL/follow/list/followers")
    suspend fun getFollowersByUser(@Query("user_id") userId: Int): Response<UserList>

    @GET("$API_V1_BASE_URL/follow/list/followers")
    suspend fun getFollowers(): Response<UserList>

    @GET("$API_V1_BASE_URL/follow/list/followeds")
    suspend fun getFolloweds(): Response<UserList>

    @GET("$API_V1_BASE_URL/follow/list/followeds")
    suspend fun getFollowedsByUser(@Query("user_id") userId: Int): Response<UserList>

    @POST("$API_V1_BASE_URL/followers")
    suspend fun createFollower(@Query("userSenderId") userSenderId: Int,@Query("userReceiverId") userReceiverId: Int): Response<FollowerResponse>

    @DELETE("$API_V1_BASE_URL/follow")
    suspend fun deleteFollower(@Query("user_follower_id") userId: Int): Response<Unit>

    @DELETE("$API_V1_BASE_URL/follow")
    suspend fun deleteFollow(@Query("user_follow_id") userId: Int): Response<Unit>



    /** Follows Requests */


    @GET("$API_V1_BASE_URL/follow/find")
    suspend fun getUsersToFollow(@Query("searchString") searchString:String?,@Query("page") page: Int?,@Query("page_size") pageSize: Int?): Response<UsersToFollowList>

    @GET("$API_V1_BASE_URL/follow/requests/list")
    suspend fun getFollowRequests(@Query("page_size") pageSize: Int): Response<UserList>

    @POST("$API_V1_BASE_URL/follow")
    suspend fun postFollowRequest(@Query("user_id") userId: Int): Response<Unit>

    // accept follow request
    @POST("$API_V1_BASE_URL/follow/requests")
    suspend fun postAcceptFollowRequest(@Query("user_follower_id") userId: Int): Response<Unit>

    // decline follow request
    @DELETE("$API_V1_BASE_URL/follow/requests")
    suspend fun deleteFollowerRequest(@Query("user_follower_id") userId: Int): Response<Unit>

    // decline follower request
    @DELETE("$API_V1_BASE_URL/follow/requests")
    suspend fun deleteFollowRequest(@Query("user_follow_id") userId: Int): Response<Unit>



    /** Shopping List */

    @POST("$API_V1_BASE_URL/shopping_list")
    suspend fun postShoppingList(@Body shoppingIngredientList: ShoppingListRequest): Response<ShoppingList>

    @GET("$API_V1_BASE_URL/shopping_list")
    suspend fun getShoppingList(): Response<ListOfShoppingLists>

    @GET("$API_V1_BASE_URL/shopping_list")
    suspend fun getShoppingList(@Query("id") shoppingListId: Int): Response<ShoppingList>

    @PUT("$API_V1_BASE_URL/shopping_list")
    suspend fun putShoppingList(@Query("id")shoppingListId: Int,@Body shoppingListRequest: ShoppingListRequest): Response<ShoppingList>

    @DELETE("$API_V1_BASE_URL/shopping_list")
    suspend fun deleteShoppingList(@Query("id")shoppingListId: Int): Response<IdResponse>

    /** Notifications */

    @GET("$API_V1_BASE_URL/notification/list")
    suspend fun getNotifications(@Query("page") page: Int?, @Query("page_size") pageSize: Int?,@Query("last_id") lastId: Int?): Response<NotificationList>

    @GET("$API_V1_BASE_URL/notification")
    suspend fun getNotification(@Query("id")id: Int?): Response<Notification>

    @PUT("$API_V1_BASE_URL/notification")
    suspend fun putNotification(@Query("id")id: Int?, @Body notification: Notification): Response<Unit>

    @POST("$API_V1_BASE_URL/notification/list/seen")
    suspend fun putNotifications(@Body idListRequest: IdListRequest): Response<Unit>

    @DELETE("$API_V1_BASE_URL/notification")
    suspend fun deleteNotification(@Query("id")id: Int?) : Response<Unit>

    @POST("$API_V1_BASE_URL/notification/list/delete")
    suspend fun deleteNotifications(@Body idListRequest: IdListRequest): Response<Unit>

    /** Application report */

    @POST("$API_V1_BASE_URL/app/report")
    suspend fun postAppReport(@Body applicationReport: ApplicationReport): Response<Unit>

    /** Goal */

    @GET("$API_V1_BASE_URL/goals/full_model")
    suspend fun getFitnessReport(): Response<FitnessReport>

    @POST("$API_V1_BASE_URL/goals")
    suspend fun createFitnessGoal(@Body goalDTO: GoalDTO): Response<Goal>



}