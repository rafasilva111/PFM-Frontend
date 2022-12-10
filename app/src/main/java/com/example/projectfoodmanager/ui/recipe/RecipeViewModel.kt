package com.example.projectfoodmanager.ui.recipe


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
    val recipeRepository: RecipeRepository

): ViewModel() {
    private val _recipes = MutableLiveData<UiState<List<Recipe>>>()

    val recipe: LiveData<UiState<List<Recipe>>>
            get() = _recipes

    fun getRecipes(){
        _recipes.value = UiState.Loading
        recipeRepository.getRecipes {
            _recipes.value = UiState.Loading
            recipeRepository.getRecipes { _recipes.value = it }
        }

    }

    fun getRecipesPaginated(page: Long){
        _recipes.value = UiState.Loading
        recipeRepository.getRecipesPaginated(page) {

            _recipes.value = UiState.Loading
            recipeRepository.getRecipesPaginated(page){ _recipes.value = it }
        }

    }

    private val _addRecipe = MutableLiveData<UiState<String>>()
    val addRecipe: LiveData<UiState<String>>
        get() = _addRecipe


    fun addRecipe(recipe: Recipe){
        _addRecipe.value = UiState.Loading
        recipeRepository.addRecipe(recipe) { _addRecipe.value = it}
    }


}