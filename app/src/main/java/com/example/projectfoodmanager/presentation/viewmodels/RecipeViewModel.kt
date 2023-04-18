package com.example.projectfoodmanager.presentation.viewmodels


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectfoodmanager.data.model.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeListResponse

import com.example.projectfoodmanager.data.old.RecipeRepository_old
import com.example.projectfoodmanager.data.repository.RecipeRepository
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult
import com.example.projectfoodmanager.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor (
    val repositoryOld: RecipeRepository_old,
    val repository: RecipeRepository



): ViewModel() {

    val TAG: String = "RecipeViewModel"

    private val _recipes = MutableLiveData<UiState<List<Recipe>>>()
    val recipe: LiveData<UiState<List<Recipe>>>
            get() = _recipes


    val recipeResponseLiveData: LiveData<Event<NetworkResult<RecipeListResponse>>>
        get() = repository.recipeResponseLiveData


    fun getRecipesPaginated(page: Int){
        viewModelScope.launch {
            repository.getRecipesPaginated(page)
        }
    }

    val recipeSearchByTitleAndTagsResponseLiveData: LiveData<Event<NetworkResult<RecipeListResponse>>>
        get() = repository.recipeSearchByTitleAndTagsResponseLiveData

    var getRecipesByTitleAndTagsJob: Job? = null

    //falta implementar o debouncer
    fun getRecipesByTitleAndTags(title: String,searchPage:Int) {
        getRecipesByTitleAndTagsJob = viewModelScope.launch {
            repository.getRecipesByTitleAndTags(title,searchPage)
        }
    }

    fun getRecipesByTitleAndTags(title: String) {
        getRecipesByTitleAndTags(title,1)
    }

    val functionLikeOnRecipe: LiveData<Event<NetworkResult<Int>>>
        get() = repository.functionLikeOnRecipe


    // LIKE FUNCTION

    fun addLikeOnRecipe(recipeId: Int) {
        viewModelScope.launch {
            repository.addLikeOnRecipe(recipeId)
        }
    }

    val functionRemoveLikeOnRecipe: LiveData<Event<NetworkResult<Int>>>
        get() = repository.functionRemoveLikeOnRecipe

    fun removeLikeOnRecipe(recipeId: Int) {
        viewModelScope.launch {
            repository.removeLikeOnRecipe(recipeId)
        }
    }

    // SAVE FUNCTION

    val functionAddSaveOnRecipe: LiveData<Event<NetworkResult<Int>>>
        get() = repository.functionAddSaveOnRecipe

    fun addSaveOnRecipe(recipeId: Int) {
        viewModelScope.launch {
            repository.addSaveOnRecipe(recipeId)
        }
    }

    val functionRemoveSaveOnRecipe: LiveData<Event<NetworkResult<Int>>>
        get() = repository.functionRemoveSaveOnRecipe

    fun removeSaveOnRecipe(recipeId: Int) {
        viewModelScope.launch {
            repository.removeSaveOnRecipe(recipeId)
        }
    }

    //OLD

    fun getRecipesPaginatedOld(firstTime:Boolean){
        _recipes.value = UiState.Loading
        repositoryOld.getRecipesPaginated(firstTime) {
            _recipes.value = it
        }

    }

}