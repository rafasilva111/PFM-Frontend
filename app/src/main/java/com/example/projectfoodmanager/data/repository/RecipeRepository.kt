package com.example.projectfoodmanager.data.repository

import com.example.projectfoodmanager.data.model.Recipe
import com.example.projectfoodmanager.data.model.User
import com.example.projectfoodmanager.util.UiState

interface RecipeRepository {
    fun getRecipes(result: (UiState<List<Recipe>>) -> Unit)
    fun getRecipesPaginated(result: (UiState<List<Recipe>>) -> Unit)
    fun addRecipe(recipeInfo: Recipe, result: (UiState<String>)-> Unit)
    fun removeLikeOnRecipe(recipe: Recipe, result: (UiState<Pair<Recipe,String>>?)-> Unit)
    fun addLikeOnRecipe(recipe: Recipe, result: (UiState<Pair<Recipe,String>>?) -> Unit)
}