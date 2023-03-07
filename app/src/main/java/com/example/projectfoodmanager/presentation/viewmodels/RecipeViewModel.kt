package com.example.projectfoodmanager.presentation.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectfoodmanager.data.model.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.recipe.list.RecipeListResponse
import com.example.projectfoodmanager.data.old.RecipeRepository_old
import com.example.projectfoodmanager.data.repository.RecipeRepository
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult
import com.example.projectfoodmanager.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor (
    val repositoryOld: RecipeRepository_old,
    val repository: RecipeRepository



): ViewModel() {
    private val _recipes = MutableLiveData<UiState<List<Recipe>>>()
    val recipe: LiveData<UiState<List<Recipe>>>
            get() = _recipes

    private val _recipes_search = MutableLiveData<UiState<List<Recipe>>>()
    val recipe_search: LiveData<UiState<List<Recipe>>>
        get() = _recipes_search

    private val _updateRecipe = MutableLiveData<UiState<Pair<Recipe,String>>>()
    val updateRecipe: LiveData<UiState<Pair<Recipe,String>>>
        get() = _updateRecipe


    private val _addRecipe = MutableLiveData<UiState<String>>()
    val addRecipe: LiveData<UiState<String>>
        get() = _addRecipe

    val recipeResponseLiveData: LiveData<Event<NetworkResult<RecipeListResponse>>>
        get() = repository.recipeResponseLiveData



    fun getRecipes(){
        _recipes.value = UiState.Loading
        repositoryOld.getRecipes {
            _recipes.value = UiState.Loading
            repositoryOld.getRecipes { _recipes.value = it }
        }

    }

    fun getRecipesPaginated(page: Int){
        viewModelScope.launch {
            repository.getRecipesPaginated(page)
        }
    }

    fun getRecipesPaginatedOld(firstTime:Boolean){
        _recipes.value = UiState.Loading
        repositoryOld.getRecipesPaginated(firstTime) {
            _recipes.value = it
        }

    }

    fun getRecipesByTitle(title: String,firstTime:Boolean) {
        _recipes_search.value = UiState.Loading
        repositoryOld.getRecipesByTitle(title,firstTime) {
            _recipes_search.value = it
        }
    }
    fun getRecipesByTitleAndTags(title: String,firstTime:Boolean) {
        _recipes_search.value = UiState.Loading
        repositoryOld.getRecipesByTitleAndTags(title,firstTime) {
            _recipes_search.value = it
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