package com.example.projectfoodmanager.viewmodels


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

    //OLD

    private val _recipes_search = MutableLiveData<UiState<List<Recipe>>>()
    val recipe_search: LiveData<UiState<List<Recipe>>>
        get() = _recipes_search

    private val _updateRecipe = MutableLiveData<UiState<Pair<Recipe,String>>>()
    val updateRecipe: LiveData<UiState<Pair<Recipe,String>>>
        get() = _updateRecipe


    private val _addRecipe = MutableLiveData<UiState<String>>()
    val addRecipe: LiveData<UiState<String>>
        get() = _addRecipe

    fun getRecipes(){
        _recipes.value = UiState.Loading
        repositoryOld.getRecipes {
            _recipes.value = UiState.Loading
            repositoryOld.getRecipes { _recipes.value = it }
        }

    }

    fun getRecipesPaginatedOld(firstTime:Boolean){
        _recipes.value = UiState.Loading
        repositoryOld.getRecipesPaginated(firstTime) {
            _recipes.value = it
        }

    }



    fun addRecipe(recipe: Recipe){
        _addRecipe.value = UiState.Loading
        repositoryOld.addRecipe(recipe) { _addRecipe.value = it}
    }

    fun removeLikeOnRecipe(userId: String, recipe: Recipe) {
        _updateRecipe.value = UiState.Loading
        repositoryOld.removeLikeOnRecipe(userId,recipe) { _updateRecipe.value = it}
    }

    fun addLikeOnRecipe(userId: String, recipe: Recipe) {
        _updateRecipe.value = UiState.Loading
        repositoryOld.addLikeOnRecipe(userId,recipe) { _updateRecipe.value = it}
    }




}