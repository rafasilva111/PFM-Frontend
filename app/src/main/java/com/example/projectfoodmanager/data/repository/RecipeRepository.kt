package com.example.projectfoodmanager.data.repository

import com.example.projectfoodmanager.data.model.Recipe
import com.example.projectfoodmanager.data.model.User
import com.example.projectfoodmanager.util.UiState

interface RecipeRepository {
    fun getRecipes(result: (UiState<List<Recipe>>) -> Unit)
    fun getRecipesPaginated(firstTime:Boolean,result: (UiState<List<Recipe>>) -> Unit)
    fun addRecipe(recipeInfo: Recipe, result: (UiState<String>)-> Unit)
    fun removeLikeOnRecipe(userId:String,recipe: Recipe, result: (UiState<Pair<Recipe,String>>?)-> Unit)
    fun addLikeOnRecipe(userId:String,recipe: Recipe, result: (UiState<Pair<Recipe,String>>?) -> Unit)
    fun getRecipesByTitle(title: String,firstTime: Boolean,result: (UiState<List<Recipe>>) -> Unit)
    fun getRecipesByTitleAndTags(title: String,firstTime: Boolean, result: (UiState<List<Recipe>>) -> Unit)
}