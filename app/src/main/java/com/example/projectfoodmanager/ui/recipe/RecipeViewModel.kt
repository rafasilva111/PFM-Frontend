package com.example.projectfoodmanager.ui.recipe


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projectfoodmanager.data.model.Recipe
import com.example.projectfoodmanager.data.repository.RecipeRepository
import com.example.projectfoodmanager.data.model.Recipe_info
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

    fun getRecipes(){
        _recipes.value = UiState.Loading
        repository.getRecipes {
            _recipes.value = UiState.Loading
            repository.getRecipes { _recipes.value = it }
        }

    }

    fun getRecipesPaginated(page: Long){
        _recipes.value = UiState.Loading
        repository.getRecipesPaginated(page) {

            _recipes.value = UiState.Loading
            repository.getRecipesPaginated(page){ _recipes.value = it }
        }

    }

    private val _addRecipe = MutableLiveData<UiState<String>>()
    val addRecipe: LiveData<UiState<String>>
        get() = _addRecipe


    fun addRecipe(recipe: Recipe){
        _addRecipe.value = UiState.Loading
        repository.addRecipe(recipe) { _addRecipe.value = it}
    }
}