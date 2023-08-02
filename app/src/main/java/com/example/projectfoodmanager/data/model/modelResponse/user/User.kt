package com.example.projectfoodmanager.data.model.modelResponse.user

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Int,
    val name: String,
    val birth_date: String?,
    val email: String,
    val description: String,
    val profile_type: String,
    val verified: Boolean,
    val user_type: String,
    val img_source: String="",
    val activity_level: Double = 0.0,
    val height: Double = 0.0,
    val sex: String?,
    val weight: Double = 0.0,
    val age: Int?,
    val rating: Double = 0.0,
    var liked_recipes: MutableList<Recipe>? = mutableListOf(),
    var saved_recipes: MutableList<Recipe>? = mutableListOf(),
    var created_recipes: MutableList<Recipe>? = mutableListOf(),
    val created_date: String?,
    val updated_date: String?
) : Parcelable {


    fun checkIfLiked(recipe: Recipe): Int {
        return if (liked_recipes!=null && liked_recipes!!.isNotEmpty())
            liked_recipes!!.indexOfFirst { it.id == recipe.id }
        else
            -1
    }

    fun  addLike(recipe: Recipe){
        if (liked_recipes!=null && liked_recipes!!.isNotEmpty()){
            this.liked_recipes = mutableListOf()
            liked_recipes!!.add(recipe)
        }
        else if (this.checkIfLiked(recipe) == -1){
            liked_recipes!!.add(recipe)
        }
    }

    fun removeLike(recipe: Recipe){
        val index = checkIfLiked(recipe)
        if (index != -1)
            liked_recipes!!.removeAt(index)
    }

    fun checkIfSaved(recipe: Recipe): Int {
        return if (saved_recipes!=null && saved_recipes!!.isNotEmpty())
            saved_recipes!!.indexOfFirst { it.id == recipe.id }
        else
            -1
    }

    fun addSave(recipe: Recipe){
        if (saved_recipes!=null && saved_recipes!!.isNotEmpty()){
            this.saved_recipes = mutableListOf()
            saved_recipes!!.add(recipe)
        }
        else if (checkIfSaved(recipe) == -1){
            saved_recipes!!.add(recipe)
        }
    }

    fun removeSave(recipe: Recipe){
        val index = checkIfSaved(recipe)
        if (index != -1)
            saved_recipes!!.removeAt(index)
    }

    fun getLikedRecipes(): MutableList<Recipe>{
        return if (liked_recipes!=null && liked_recipes!!.isNotEmpty())
            liked_recipes!!
        else
            mutableListOf()
    }

    fun getSavedRecipes(): MutableList<Recipe>{
        return if (saved_recipes!=null && saved_recipes!!.isNotEmpty())
            saved_recipes!!
        else
            mutableListOf()
    }

    fun getCreateRecipes(): MutableList<Recipe>{
        return if (created_recipes!=null && created_recipes!!.isNotEmpty())
            created_recipes!!
        else
            mutableListOf()
    }
}