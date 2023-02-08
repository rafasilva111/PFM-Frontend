package com.example.projectfoodmanager.data.api

import com.example.projectfoodmanager.data.model.User
import com.example.projectfoodmanager.data.model.modelRequest.CommentRequest
import com.example.projectfoodmanager.data.model.modelRequest.RecipeRequest
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.model.modelResponse.CommentResponse
import com.example.projectfoodmanager.data.model.modelResponse.FollowerResponse
import com.example.projectfoodmanager.data.model.modelResponse.RecipeResponse
import com.example.projectfoodmanager.data.model.modelResponse.UserResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    //user
    @POST("/user")
    suspend fun createUser(@Body user : UserRequest): Response<UserResponse>

    @GET("/user")
    suspend fun getUser(@Query("userId") userId: String): Response<UserResponse>

    @GET("/user")
    suspend fun getUserByUUID(@Query("userUUID") userUUID: String): Response<UserResponse>

    @PUT("/user")
    suspend fun updateUser(@Query("userId") userId: String,@Body user : UserRequest): Response<UserResponse>

    @DELETE("/user")
    suspend fun deleteUser(@Query("userId") userId: String): Response<String>

    //recipe
    @POST("/recipe")
    suspend fun createRecipe(@Query("userId") userId: String,@Body recipe : RecipeRequest): Response<RecipeResponse>

    @GET("/recipe")
    suspend fun getRecipe(@Query("recipeId") recipeId: String): Response<RecipeResponse>

    @PUT("/recipe")
    suspend fun updateRecipe(@Query("recipeId") recipeId: String,@Query("userId") userId: String,@Body recipe : RecipeRequest): Response<RecipeResponse>

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