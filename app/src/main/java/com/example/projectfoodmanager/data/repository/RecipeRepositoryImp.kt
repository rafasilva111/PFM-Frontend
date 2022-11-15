package com.example.projectfoodmanager.data.repository

import com.example.projectfoodmanager.data.repository.models.Recipe

class RecipeRepositoryImp : RecipeRepository {

    override fun getRecipes(): List<Recipe> {
        return arrayListOf()
    }

}
