package com.example.projectfoodmanager.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projectfoodmanager.data.repository.RecipeRepository
import com.example.projectfoodmanager.data.repository.models.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor (
    val repository: RecipeRepository
): ViewModel() {
    private val _recipes = MutableLiveData<List<Recipe>>()

    val recipe: LiveData<List<Recipe>>
            get() = _recipes


    fun getRecipes(){
        _recipes.value = repository.getRecipes()
    }
}