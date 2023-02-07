package com.example.projectfoodmanager.data.model

import android.os.Parcelable
import com.google.type.DateTime
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var id: String = "",
    var age: Int = 0,
    val first_name: String = "",
    val last_name: String = "",
    val birth_date: String = "",
    val email: String = "",
    val password: String = "",
    val img_source: String? = "",
    var favorite_recipes: ArrayList<Recipe> = arrayListOf(),
    var liked_recipes: ArrayList<Recipe> = arrayListOf(),
    //bio data
    val height: String? = "",
    val weight: String? = "",
    val activity_level: String? = "",
    val sex: String = "",

    ) : Parcelable {


    fun addFavoriteRecipe(recipe: Recipe){
        this.favorite_recipes.add(recipe)
    }

    fun removeFavoriteRecipe(recipeId: String) {
        // it need to create a copy otherwise it will mess up the for
        val favorited_recipes_helper: ArrayList<Recipe> = this.liked_recipes.clone() as ArrayList<Recipe>

        for (a in favorited_recipes_helper){
            if (a.id == recipeId){
                this.favorite_recipes.remove(a)
            }
        }
    }

    fun getFavoriteRecipe (recipeId: String): Recipe?{
        for (a in this.favorite_recipes){
            if (a.id == recipeId){
                return a
            }
        }
        return null
    }

    fun addLikeRecipe(recipe: Recipe) {
        this.liked_recipes.add(recipe)
    }

    fun removeLikeRecipe(recipeId: String) {
        val liked_recipes_helper: ArrayList<Recipe> = this.liked_recipes.clone() as ArrayList<Recipe>
        for (a in liked_recipes_helper){
            if (a.id == recipeId){
                liked_recipes.remove(a)
            }
        }
    }

    fun getLikedRecipe (recipeId: String): Recipe?{
        for (a in this.liked_recipes){
            if (a.id == recipeId){
                return a
            }
        }
        return null
    }
}