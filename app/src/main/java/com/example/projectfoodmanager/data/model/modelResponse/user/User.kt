package com.example.projectfoodmanager.data.model.modelResponse.user

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeResponse
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val birth_date: String,
    val email: String,
    val profile_type: String,
    val verified: Boolean,
    val user_type: String,
    val img_source: String,
    val activity_level: Double,
    val height: Double,
    val sex: String,
    val weight: Double,
    val age: Int,
    val liked_recipes: MutableList<RecipeResponse>,
    val saved_recipes: MutableList<RecipeResponse>,
    val created_recipes: MutableList<RecipeResponse>,
    val created_date: String,
    val updated_date: String
) : Parcelable {


    fun checkIfLiked(recipe: RecipeResponse): Int {
        return  liked_recipes.indexOfFirst { it.id == recipe.id }
    }

    fun addLike(recipe: RecipeResponse){
        if (checkIfLiked(recipe) == -1)
            liked_recipes.add(recipe)
    }
    fun removeLike(recipe: RecipeResponse){
        val index = checkIfLiked(recipe)
        if (index != -1)
            liked_recipes.removeAt(index)
    }


    /*fun addLike(userId:String) {
        if (this.likes.contains(userId)){
            Log.d("RecipeModel", "addLike: This recipe is already liked by this user: $userId")
        }
        else
            this.likes.add(userId)
    }
    fun removeLike(userId:String) {
        if (!this.likes.contains(userId))
            Log.d("RecipeModel", "addLike: This recipe has already been removed by this user: $userId")
        else
            this.likes.remove(userId)
    }*/
}