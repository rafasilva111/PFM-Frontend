package com.example.projectfoodmanager.data.repository

import androidx.lifecycle.LiveData
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeListResponse

import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult

interface RecipeRepository {



    val recipeResponseLiveData: LiveData<Event<NetworkResult<RecipeListResponse>>>
    val recipeSearchByTitleAndTagsResponseLiveData: LiveData<Event<NetworkResult<RecipeListResponse>>>

    // like
    val functionLikeOnRecipe: LiveData<Event<NetworkResult<Int>>>
    val functionRemoveLikeOnRecipe: LiveData<Event<NetworkResult<Int>>>

    // save
    val functionAddSaveOnRecipe: LiveData<Event<NetworkResult<Int>>>
    val functionRemoveSaveOnRecipe: LiveData<Event<NetworkResult<Int>>>

    suspend fun getRecipesPaginated(page: Int)
    suspend fun getRecipesByTitleAndTags(title: String, searchPage: Int)
    suspend fun addLikeOnRecipe(recipeId: Int)
    suspend fun removeLikeOnRecipe(recipeId: Int)
    suspend fun removeSaveOnRecipe(recipeId: Int)
    suspend fun addSaveOnRecipe(recipeId: Int)

}