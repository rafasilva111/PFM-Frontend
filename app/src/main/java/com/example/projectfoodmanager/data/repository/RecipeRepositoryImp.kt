package com.example.projectfoodmanager.data.repository

import com.example.projectfoodmanager.data.repository.models.Recipe
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class RecipeRepositoryImp(
    val database: FirebaseFirestore
): RecipeRepository {

    override fun getRecipes(): List<Recipe> {
        return arrayListOf(Recipe(
            id= "1",
            description = "È uma merda",
            date = Date()
        ),Recipe(
            id= "2",
            description = "È uma merda",
            date = Date()
        ),Recipe(
            id= "3",
            description = "È uma merda",
            date = Date()
        ))
    }

}
