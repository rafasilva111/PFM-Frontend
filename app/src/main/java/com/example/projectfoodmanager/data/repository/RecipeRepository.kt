package com.example.projectfoodmanager.data.repository

import com.example.projectfoodmanager.data.repository.models.Recipe

interface RecipeRepository {
    fun getRecipes(): List<Recipe>
}