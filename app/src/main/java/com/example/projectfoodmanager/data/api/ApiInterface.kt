package com.example.projectfoodmanager.data.api

import com.example.projectfoodmanager.data.model.modelRequest.CommentRequest
import com.example.projectfoodmanager.data.model.modelRequest.RecipeRequest
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.CommentResponse
import com.example.projectfoodmanager.data.model.modelResponse.FollowerResponse
import com.example.projectfoodmanager.data.model.modelResponse.user.UserAuthResponse
import com.example.projectfoodmanager.data.model.modelResponse.recipe.list.RecipeListResponse
import com.example.projectfoodmanager.data.model.modelResponse.recipe.list.RecipeResult
import com.example.projectfoodmanager.data.model.modelResponse.user.UserResponse
import retrofit2.Response
import retrofit2.http.*


interface ApiInterface {

    //user
    @POST("/user")
    suspend fun createUser(@Body user : UserRequest): Response<UserAuthResponse>

    @POST("/user/login")
    suspend fun loginUser(@Body user : UserRequest): Response<UserAuthResponse>

    @DELETE("/user/logout")
    suspend fun logoutUser(): Response<String>

    @GET("/user/auth")
    suspend fun getUserSession(): Response<UserResponse>

    @GET("/user")
    suspend fun getUser(@Query("userId") userId: String): Response<UserAuthResponse>

    @GET("/user")
    suspend fun getUserByUUID(@Query("userUUID") userUUID: String): Response<UserAuthResponse>

    @PUT("/user")
    suspend fun updateUser(@Query("userId") userId: String,@Body user : UserRequest): Response<UserAuthResponse>

    @DELETE("/user")
    suspend fun deleteUser(@Query("userId") userId: String): Response<String>

    //recipe
    @POST("/recipe")
    suspend fun createRecipe(@Query("userId") userId: String,@Body recipe : RecipeRequest): Response<RecipeResult>

    @GET("/recipe")
    suspend fun getRecipe(@Query("recipeId") recipeId: String): Response<RecipeResult>

    @GET("/recipe")
    suspend fun getRecipePaginated(@Query("page") page: Int): Response<RecipeListResponse>

    @GET("/recipe")
    suspend fun getRecipesByTitleAndTags(@Query("string")string: String,@Query("page") page: Int): Response<RecipeListResponse>

    @PUT("/recipe")
    suspend fun updateRecipe(@Query("recipeId") recipeId: String,@Query("userId") userId: String,@Body recipe : RecipeRequest): Response<RecipeResult>

    @DELETE("/recipe")
    suspend fun deleteRecipe(@Query("recipeId") recipeId: String,@Query("userId") userId: String): Response<String>


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