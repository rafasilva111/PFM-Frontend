package com.example.projectfoodmanager.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projectfoodmanager.data.repository.RecipeRepository
import com.example.projectfoodmanager.data.repository.models.Recipe

class RecipeViewModel (
    val repository: RecipeRepository
): ViewModel() {
    private val _recipes = MutableLiveData<List<Recipe>>()

    val recipe: LiveData<List<Recipe>>
            get() = _recipes


    fun getRecipes(){
        _recipes.value = repository.getRecipes()
    }
}