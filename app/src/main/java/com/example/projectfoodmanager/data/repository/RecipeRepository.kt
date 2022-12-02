package com.example.projectfoodmanager.data.repository

import com.example.projectfoodmanager.data.model.Recipe
import com.example.projectfoodmanager.util.UiState

interface RecipeRepository {
    fun getRecipes(result: (UiState<List<Recipe>>) -> Unit)
    fun addRecipe(recipe: Recipe, result: (UiState<String> )-> Unit)
}