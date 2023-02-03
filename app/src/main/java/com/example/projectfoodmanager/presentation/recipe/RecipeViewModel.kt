package com.example.projectfoodmanager.presentation.recipe


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projectfoodmanager.data.model.Recipe
import com.example.projectfoodmanager.data.repository.RecipeRepository
import com.example.projectfoodmanager.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor (
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



    fun getRecipes(){
        _recipes.value = UiState.Loading
        repository.getRecipes {
            _recipes.value = UiState.Loading
            repository.getRecipes { _recipes.value = it }
        }

    }

    fun getRecipesPaginated(firstTime:Boolean){
        _recipes.value = UiState.Loading
        repository.getRecipesPaginated(firstTime) {
            _recipes.value = it
        }

    }

    fun getRecipesByTitle(title: String,firstTime:Boolean) {
        _recipes_search.value = UiState.Loading
        repository.getRecipesByTitle(title,firstTime) {
            _recipes_search.value = it
        }
    }
    fun getRecipesByTitleAndTags(title: String,firstTime:Boolean) {
        _recipes_search.value = UiState.Loading
        repository.getRecipesByTitleAndTags(title,firstTime) {
            _recipes_search.value = it
        }
    }

    fun addRecipe(recipe: Recipe){
        _addRecipe.value = UiState.Loading
        repository.addRecipe(recipe) { _addRecipe.value = it}
    }

    fun removeLikeOnRecipe(userId: String, recipe: Recipe) {
        _updateRecipe.value = UiState.Loading
        repository.removeLikeOnRecipe(userId,recipe) { _updateRecipe.value = it}
    }

    fun addLikeOnRecipe(userId: String, recipe: Recipe) {
        _updateRecipe.value = UiState.Loading
        repository.addLikeOnRecipe(userId,recipe) { _updateRecipe.value = it}
    }




}