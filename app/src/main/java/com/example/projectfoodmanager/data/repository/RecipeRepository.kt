package com.example.projectfoodmanager.data.repository

import androidx.lifecycle.LiveData
import com.example.projectfoodmanager.data.model.modelRequest.comment.CreateCommentRequest
import com.example.projectfoodmanager.data.model.modelResponse.comment.Comment
import com.example.projectfoodmanager.data.model.modelResponse.comment.CommentList
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeList

import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult

interface RecipeRepository {




    // general
    val recipes: LiveData<Event<NetworkResult<RecipeList>>>
    val recipesCommentedByUser: LiveData<Event<NetworkResult<RecipeList>>>

    suspend fun getRecipes(page: Int,searchString:String,searchTag: String, by:String)
    suspend fun getRecipesCommentedByUser(page: Int, clientId: Int, searchString:String?)

    // like
    val userLikedRecipes: LiveData<Event<NetworkResult<RecipeList>>>
    val functionLikeOnRecipe: LiveData<Event<NetworkResult<Int>>>
    val functionRemoveLikeOnRecipe: LiveData<Event<NetworkResult<Int>>>

    suspend fun getUserLikedRecipes()
    suspend fun addLikeOnRecipe(recipeId: Int)
    suspend fun removeLikeOnRecipe(recipeId: Int)

    // save
    val functionAddSaveOnRecipe: LiveData<Event<NetworkResult<Int>>>
    val functionRemoveSaveOnRecipe: LiveData<Event<NetworkResult<Int>>>

    suspend fun removeSaveOnRecipe(recipeId: Int)
    suspend fun addSaveOnRecipe(recipeId: Int)

    // comment
    val functionPostCommentOnRecipe: LiveData<Event<NetworkResult<Comment>>>
    val functionGetCommentsOnRecipePaginated: LiveData<Event<NetworkResult<CommentList>>>
    val functionGetSizedCommentsOnRecipePaginated: LiveData<Event<NetworkResult<CommentList>>>
    val functionGetCommentsByClientPaginated: LiveData<Event<NetworkResult<CommentList>>>

    suspend fun createCommentOnRecipe(recipeId: Int,comment: CreateCommentRequest)
    suspend fun getCommentsByRecipePaginated(recipeId: Int, page: Int)
    suspend fun getSizedCommentsByRecipePaginated(recipeId: Int,page: Int,pageSize:Int)
    suspend fun getCommentsByClientPaginated(clientId: Int, page: Int)


}