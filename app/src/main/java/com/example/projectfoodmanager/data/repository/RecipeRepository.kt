package com.example.projectfoodmanager.data.repository

import androidx.lifecycle.LiveData
import com.example.projectfoodmanager.data.model.modelResponse.recipe.list.RecipeListResponse
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult

interface RecipeRepository {
    val recipeResponseLiveData: LiveData<Event<NetworkResult<RecipeListResponse>>>

    suspend fun getRecipesPaginated(page: Int)
}