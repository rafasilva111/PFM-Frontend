package com.example.projectfoodmanager.data.repository

import androidx.lifecycle.LiveData
import com.example.projectfoodmanager.data.model.modelRequest.comment.CreateCommentRequest
import com.example.projectfoodmanager.data.model.modelResponse.comment.Comment
import com.example.projectfoodmanager.data.model.modelResponse.comment.CommentList
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeList

import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult

interface RecipeRepository {


    val recipeResponseLiveData: LiveData<Event<NetworkResult<RecipeList>>>
    val recipeSearchByTitleAndTagsResponseLiveData: LiveData<Event<NetworkResult<RecipeList>>>

    // like
    val userLikedRecipes: LiveData<Event<NetworkResult<RecipeList>>>
    val functionLikeOnRecipe: LiveData<Event<NetworkResult<Int>>>
    val functionRemoveLikeOnRecipe: LiveData<Event<NetworkResult<Int>>>

    // save
    val functionAddSaveOnRecipe: LiveData<Event<NetworkResult<Int>>>
    val functionRemoveSaveOnRecipe: LiveData<Event<NetworkResult<Int>>>

    // comment
    val functionGetCommentsOnRecipePaginated: LiveData<Event<NetworkResult<CommentList>>>
    val functionCreateCommentOnRecipe: LiveData<Event<NetworkResult<Comment>>>

    suspend fun getRecipesPaginated(page: Int)
    suspend fun getRecipesByTitleAndTags(title: String, searchPage: Int)
    suspend fun addLikeOnRecipe(recipeId: Int)
    suspend fun removeLikeOnRecipe(recipeId: Int)
    suspend fun removeSaveOnRecipe(recipeId: Int)
    suspend fun addSaveOnRecipe(recipeId: Int)
    suspend fun getUserLikedRecipes()
    suspend fun getCommentsOnRecipePaginated(recipeId: Int,page: Int)
    suspend fun createCommentOnRecipe(recipeId: Int,comment: CreateCommentRequest)
}