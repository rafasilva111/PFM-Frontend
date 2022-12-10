package com.example.projectfoodmanager.data.repository

import android.util.Log
import com.example.projectfoodmanager.data.model.Recipe
import com.example.projectfoodmanager.util.FireStoreCollection
import com.example.projectfoodmanager.util.FireStorePaginations
import com.example.projectfoodmanager.util.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class RecipeRepositoryImp(
    val database: FirebaseFirestore
): RecipeRepository {
    val TAG: String = "RecipeRepositoryImp"

    override fun getRecipes(result: (UiState<List<Recipe>>) -> Unit) {
        database.collection(FireStoreCollection.RECIPE_PROD)
            .get()
            .addOnSuccessListener {
                val notes = arrayListOf<Recipe>()
                for (document in it) {
                    val recipe = document.toObject(Recipe::class.java)

                    if (recipe != null) {
                        notes.add(recipe)
                    } else {
                        Log.d(TAG, "Problem on recipe -> " + document.toString())
                    }
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
        database.collection(FireStoreCollection.RECIPE_PROD)
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

    override fun getRecipesPaginated(page: Long, result: (UiState<List<Recipe>>) -> Unit) {
        if (page.compareTo(0) == 0) {
            database.collection(FireStoreCollection.RECIPE_PROD).limit(FireStorePaginations.RECIPE_LIMIT).orderBy("id")
                .get()
                .addOnSuccessListener { documentSnapshots ->

                    val notes = arrayListOf<Recipe>()
                    for (document in documentSnapshots.documents) {
                        val recipe = document.toObject(Recipe::class.java)

                        if (recipe != null) {

                            notes.add(recipe)
                        } else {
                            Log.d(TAG, "Problem on recipe -> " + document.toString())
                        }
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
        else{
            database.collection(FireStoreCollection.RECIPE_PROD).orderBy("id").startAt(page+1)
                .limit(FireStorePaginations.RECIPE_LIMIT)
                .get()
                .addOnSuccessListener { documentSnapshots ->

                    val notes = arrayListOf<Recipe>()
                    for (document in documentSnapshots.documents) {
                        val recipe = document.toObject(Recipe::class.java)

                        if (recipe != null) {

                            notes.add(recipe)
                        } else {
                            Log.d(TAG, "Problem on recipe -> " + document.toString())
                        }
                    }
                    result.invoke(
                        UiState.Success(notes)
                    )
                }
                .addOnFailureListener {
                    Log.d(TAG, "problem" +it.toString())
                    result.invoke(
                        UiState.Failure(
                            it.localizedMessage

                        )
                    )
                }
        }
    }


}
