package com.example.projectfoodmanager.data.repository

import android.util.Log
import com.example.projectfoodmanager.data.model.Recipe
import com.example.projectfoodmanager.util.FireStoreCollection
import com.example.projectfoodmanager.util.FireStorePaginations
import com.example.projectfoodmanager.util.UiState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


class RecipeRepositoryImp(
    val database: FirebaseFirestore
): RecipeRepository {
    val TAG: String = "RecipeRepositoryImp"
    var lastRecipeSnapshot: DocumentSnapshot? = null
    var lastRecipe: Recipe? = null

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

    override fun getRecipesPaginated(firstTime: Boolean, result: (UiState<List<Recipe>>) -> Unit) {
        var first: Query?
        val notes = arrayListOf<Recipe>()

        if (firstTime){
             first = database.collection(FireStoreCollection.RECIPE_PROD)
                 .orderBy("id")
                .limit(FireStorePaginations.RECIPE_LIMIT)
        }
        else{
            first = database.collection(FireStoreCollection.RECIPE_PROD)
                .orderBy("id")
                .limit(FireStorePaginations.RECIPE_LIMIT)
                .startAfter(lastRecipe?.id)
        }
        first.get()
            .addOnSuccessListener { documentSnapshots ->
                lastRecipeSnapshot = documentSnapshots.documents[documentSnapshots.size() - 1]

                for (document in documentSnapshots.documents) {
                    val recipe = document.toObject(Recipe::class.java)

                    if (recipe != null) {
                        notes.add(recipe)
                    } else {
                        Log.d(TAG, "Problem on recipe -> " + document.toString())
                    }
                }
                lastRecipe = documentSnapshots.documents[documentSnapshots.size() - 1].toObject(Recipe::class.java)
                result.invoke(
                    UiState.Success(
                        notes
                    )
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

    override fun getRecipesByTitle(title: String,firstTime: Boolean, result: (UiState<List<Recipe>>) -> Unit) {
        var first: Query?
        val notes = arrayListOf<Recipe>()


        first = database.collection(FireStoreCollection.RECIPE_PROD)

        first.get()
            .addOnSuccessListener { documentSnapshots ->
                documentSnapshots.query
                if( documentSnapshots.size() !=0) {
                    lastRecipeSnapshot = documentSnapshots.documents[documentSnapshots.size() - 1]

                    for (document in documentSnapshots.documents) {
                        val recipe = document.toObject(Recipe::class.java)
                        val found = recipe!!.title.lowercase().contains(title.lowercase())
                        Log.d("TAG", "${recipe.title} / $found")
                        if (found) {
                            notes.add(recipe)
                        } else {
                            Log.d(TAG, "Problem on recipe -> " + document.toString())
                        }
                        if (notes.size ==FireStorePaginations.RECIPE_LIMIT.toInt()){
                            break
                        }
                    }
                    lastRecipe =
                        documentSnapshots.documents[documentSnapshots.size() - 1].toObject(Recipe::class.java)
                    result.invoke(
                        UiState.Success(
                            notes
                        )
                    )
                }
                else

                    UiState.Success(
                        notes
                    )
            }
            .addOnFailureListener {
                Log.d(TAG, "getRecipesByTitle: "+it)
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun addLikeOnRecipe(
        recipe: Recipe,
        result: (UiState<Pair<Recipe, String>>?) -> Unit
    ) {

        recipe.addLike()
        database.collection(FireStoreCollection.RECIPE_PROD).document(recipe.id).set(recipe).addOnFailureListener {
            Log.d(TAG, "addFavoriteRecipe: "+it.toString())
        }

        Log.d(TAG, "Recipe has been liked successfully.")

        result.invoke(
            UiState.Success(Pair(recipe,"Receita gostada com sucesso!"))
        )
    }

    override fun removeLikeOnRecipe(
        recipe: Recipe,
        result: (UiState<Pair<Recipe, String>>?) -> Unit
    ) {
        recipe.removeLike()
        database.collection(FireStoreCollection.RECIPE_PROD).document(recipe.id).set(recipe).addOnFailureListener {
            Log.d(TAG, "addFavoriteRecipe: "+it.toString())
        }

        Log.d(TAG, "Recipe has been unliked successfully.")

        result.invoke(
            UiState.Success(Pair(recipe,"Receita gosto retirado com sucesso!"))
        )
    }
}
