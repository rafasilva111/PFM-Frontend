package com.example.projectfoodmanager.data.api

import com.example.projectfoodmanager.data.model.modelRequest.CommentRequest
import com.example.projectfoodmanager.data.model.modelRequest.RecipeRequest
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.CommentResponse
import com.example.projectfoodmanager.data.model.modelResponse.FollowerResponse
import com.example.projectfoodmanager.data.model.modelResponse.user.UserAuthResponse
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeListResponse
import com.example.projectfoodmanager.data.model.modelResponse.user.UserResponse
import retrofit2.Response
import retrofit2.http.*
const val API_V1_BASE_URL = "api/v1"

interface ApiInterface {

    //user
    @POST("$API_V1_BASE_URL/user")
    suspend fun createUser(@Body user : UserRequest): Response<UserAuthResponse>

    @POST("$API_V1_BASE_URL/auth/login")
    suspend fun loginUser(@Body user : UserRequest): Response<UserAuthResponse>

    @DELETE("$API_V1_BASE_URL/auth/logout")
    suspend fun logoutUser(): Response<String>

    @GET("$API_V1_BASE_URL/auth")
    suspend fun getUserSession(): Response<UserResponse>

    @GET("/user")
    suspend fun getUser(@Query("userId") userId: Int): Response<UserAuthResponse>

    @PUT("/user")
    suspend fun updateUser(@Query("userId") userId: Int,@Body user : UserRequest): Response<UserAuthResponse>

    @DELETE("/user")
    suspend fun deleteUser(@Query("userId") userId: Int): Response<String>

    //recipe
    @POST("$API_V1_BASE_URL/recipe")
    suspend fun createRecipe(@Body recipe : RecipeRequest): Response<com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeResult>

    @GET("$API_V1_BASE_URL/recipe/list")
    suspend fun getRecipe(@Query("recipeId") recipeId: Int): Response<com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeResult>

    @GET("$API_V1_BASE_URL/recipe/list")
    suspend fun getRecipePaginated(@Query("page") page: Int): Response<RecipeListResponse>

    @GET("$API_V1_BASE_URL/recipe/list")
    suspend fun getRecipesByTitleAndTags(@Query("string")string: String,@Query("page") page: Int): Response<RecipeListResponse>

    @PUT("$API_V1_BASE_URL/recipe")
    suspend fun updateRecipe(@Query("recipeId") recipeId: Int,@Body recipe : RecipeRequest): Response<com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeResult>

    @DELETE("$API_V1_BASE_URL/recipe")
    suspend fun deleteRecipe(@Query("recipeId") recipeId: Int): Response<String>


    //comments
    @POST("/comments")
    suspend fun createComments(@Body comment : CommentRequest): Response<CommentResponse>

    @GET("/comments")
    suspend fun getCommentsByRecipe(@Query("recipeId") recipeId: String): Response<CommentResponse>

    @GET("/comments")
    suspend fun getCommentsByUser(@Query("userId") userId: String): Response<CommentResponse>

    @PUT("/comments")
    suspend fun updateComments(@Query("userId") userId: String,@Body comment : CommentRequest): Response<CommentResponse>

    @DELETE("/comments")
    suspend fun deleteComments(@Query("recipeId") recipeId: String,@Query("userId") userId: String): Response<String>

    //followers
    @POST("/followers")
    suspend fun createFollower(@Query("userSenderId") userSenderId: String,@Query("userReceiverId") userReceiverId: String): Response<FollowerResponse>

    @GET("/followers")
    suspend fun getFollowers(@Query("userSenderId") userSenderId: String): Response<FollowerResponse>

    @GET("/followers")
    suspend fun getFollowes(@Query("userReceiverId") userReceiverId: String): Response<FollowerResponse>



}