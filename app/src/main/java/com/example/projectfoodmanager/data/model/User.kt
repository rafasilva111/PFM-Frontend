package com.example.projectfoodmanager.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var id: String = "",
    val first_name: String = "",
    val last_name: String = "",
    val job_title: String = "",
    val email: String = "",
    var favorite_recipes: ArrayList<String> = arrayListOf(),
) : Parcelable {
    fun addFavoriteRecipe(recipe: Recipe): Recipe? {


            this.favorite_recipes = ArrayList<String>()
            this.favorite_recipes.add(recipe.id.toString())

        return recipe
    }
}