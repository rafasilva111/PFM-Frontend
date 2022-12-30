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

    fun removeFavoriteRecipe(recipe: Recipe) {
        this.favorite_recipes.remove(recipe)
    }

    fun addLikeRecipe(recipe: Recipe) {
        this.liked_recipes.add(recipe)
    }

    fun removeLikeRecipe(recipe: Recipe) {
        this.liked_recipes.remove(recipe)
    }
}