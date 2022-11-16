package com.example.projectfoodmanager.data.repository

import com.example.projectfoodmanager.data.repository.models.Recipe
import com.example.projectfoodmanager.util.FireStoreTables
import com.example.projectfoodmanager.util.UiState
import com.google.firebase.firestore.FirebaseFirestore


class RecipeRepositoryImp(
    val database: FirebaseFirestore
): RecipeRepository {

    override fun getRecipes(result: (UiState<List<Recipe>>) -> Unit) {
        database.collection(FireStoreTables.RECIPE)
            .get()
            .addOnSuccessListener {
                val notes = arrayListOf<Recipe>()
                    for(document in it){
                        val note = document.toObject(Recipe::class.java)
                        notes.add(note)
                    }
                result.invoke(
                    UiState.Success(notes)
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun addRecipe(recipe: Recipe, result: (UiState<String>) -> Unit) {
        database.collection(FireStoreTables.RECIPE)
            .add(recipe)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success(it.id)
                )
            }
            .addOnFailureListener{
                result.invoke(
                    UiState.Failure(it.localizedMessage)
                )
            }
    }


}
