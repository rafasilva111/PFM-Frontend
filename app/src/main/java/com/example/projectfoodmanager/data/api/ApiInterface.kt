package com.example.projectfoodmanager.data.api


import com.example.projectfoodmanager.data.model.modelRequest.CalenderEntryRequest
import com.example.projectfoodmanager.data.model.modelRequest.RecipeRequest
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelRequest.comment.CreateCommentRequest
import com.example.projectfoodmanager.data.model.modelResponse.FollowerResponse
import com.example.projectfoodmanager.data.model.modelResponse.comment.Comment
import com.example.projectfoodmanager.data.model.modelResponse.comment.CommentList
import com.example.projectfoodmanager.data.model.modelResponse.follows.FollowList
import com.example.projectfoodmanager.data.model.modelResponse.user.UserAuthResponse
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeList
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import retrofit2.Response
import retrofit2.http.*
const val API_V1_BASE_URL = "api/v1"

interface ApiInterface {

    //user
    @POST("$API_V1_BASE_URL/auth")
    suspend fun createUser(@Body user : UserRequest): Response<Unit>

    @POST("$API_V1_BASE_URL/auth/login")
    suspend fun loginUser(@Body user : UserRequest): Response<UserAuthResponse>

    @DELETE("$API_V1_BASE_URL/auth/logout")
    suspend fun logoutUser(): Response<String>

    @GET("$API_V1_BASE_URL/auth")
    suspend fun getUserSession(): Response<User>

    @POST("$API_V1_BASE_URL/user")
    suspend fun patchUser(@Body user : UserRequest): Response<Unit>

    @GET("/user")
    suspend fun getUser(@Query("id") userId: Int): Response<UserAuthResponse>

    @PATCH("$API_V1_BASE_URL/user")
    suspend fun updateUser(@Body user : UserRequest): Response<User>

    @DELETE("/user")
    suspend fun deleteUser(@Query("id") userId: Int): Response<String>

    //recipe
    @POST("$API_V1_BASE_URL/recipe")
    suspend fun createRecipe(@Body recipe : RecipeRequest): Response<Recipe>

    @GET("$API_V1_BASE_URL/recipe/list")
    suspend fun getRecipe(@Query("id") recipeId: Int): Response<Recipe>

    @GET("$API_V1_BASE_URL/recipe/list")
    suspend fun getRecipePaginated(@Query("page") page: Int): Response<RecipeList>

    @GET("$API_V1_BASE_URL/recipe/list")
    suspend fun getRecipePaginatedSorted(@Query("page") page: Int,@Query("by") by: String): Response<RecipeList>

    @GET("$API_V1_BASE_URL/recipe/list")
    suspend fun getRecipesByTitleAndTags(@Query("string")string: String,@Query("page") page: Int): Response<RecipeList>

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



    //comments
    @POST("$API_V1_BASE_URL/comment")
    suspend fun createComments(@Query("recipe_id") recipeId: Int,@Body comment : CreateCommentRequest): Response<Comment>

    @GET("$API_V1_BASE_URL/comment")
    suspend fun getComment(@Query("userId") commentId: Int): Response<Comment>

    @GET("$API_V1_BASE_URL/comment/list")
    suspend fun getCommentsByRecipe(@Query("recipe_id") recipeId: Int,@Query("page") page: Int): Response<CommentList>

    @GET("$API_V1_BASE_URL/comment")
    suspend fun getCommentsByUser(@Query("userId") userId: Int): Response<Comment>

    @PUT("$API_V1_BASE_URL/comment")
    suspend fun updateComments(@Query("commentId") commentId: Int,@Body comment : Comment): Response<Comment>

    @DELETE("$API_V1_BASE_URL/comment")
    suspend fun deleteComments(@Query("commentId") commentId: Int): Response<Unit>

    // calender

    @POST("$API_V1_BASE_URL/calendar")
    suspend fun createCalenderEntry(@Query("recipe_id") recipeId: Int,@Body calenderEntryRequest : CalenderEntryRequest): Response<Unit>


    //followers
    @POST("$API_V1_BASE_URL/followers")
    suspend fun createFollower(@Query("userSenderId") userSenderId: Int,@Query("userReceiverId") userReceiverId: Int): Response<FollowerResponse>

    @GET("$API_V1_BASE_URL/follow/list/followers")
    suspend fun getFollowers(): Response<FollowList>

    @GET("$API_V1_BASE_URL/follow/list/followeds")
    suspend fun getFolloweds(): Response<FollowList>



}