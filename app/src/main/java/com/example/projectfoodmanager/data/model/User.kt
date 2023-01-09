package com.example.projectfoodmanager.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var id: String = "",
    var idade: String = "",
    val first_name: String = "",
    val last_name: String = "",
    val email: String = "",
    var favorite_recipes: ArrayList<Recipe> = arrayListOf(),
    var liked_recipes: ArrayList<Recipe> = arrayListOf(),
    //bio data
    val altura: String = "",
    val peso: String = "",
    val nivel_de_atividade: String = "",
    val genero: String = "",

) : Parcelable {
    fun addFavoriteRecipe(recipe: Recipe){
        this.favorite_recipes.add(recipe)
    }

    fun removeFavoriteRecipe(recipeId: String) {
        for (a in this.liked_recipes){
            if (a.title == recipeId){
                this.favorite_recipes.remove(a)
            }
        }
    }

    fun getFavoriteRecipe (recipeId: String): Recipe?{
        for (a in this.favorite_recipes){
            if (a.title == recipeId){
                return a
            }
        }
        return null
    }

    fun addLikeRecipe(recipe: Recipe) {
        this.liked_recipes.add(recipe)
    }

    fun removeLikeRecipe(recipeId: String) {
        for (a in this.liked_recipes){
            if (a.title == recipeId){
                this.liked_recipes.remove(a)
            }
        }
    }

    fun getLikedRecipe (recipeId: String): Recipe?{
        for (a in this.liked_recipes){
            if (a.title == recipeId){
                return a
            }
        }
        return null
    }
}