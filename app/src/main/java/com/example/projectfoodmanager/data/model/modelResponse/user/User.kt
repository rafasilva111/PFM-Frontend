package com.example.projectfoodmanager.data.model.modelResponse.user

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeSimplified
import com.example.projectfoodmanager.data.model.user.goal.Goal
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Int,
    val name: String,
    val username: String?,
    val birth_date: String?,
    val email: String,
    val description: String,
    @SerializedName("fmc_token")
    var fmcToken: String?,
    val profile_type: String,
    val verified: Boolean,
    @SerializedName("user_type")
    var userType: String,
    @SerializedName("user_portion")
    val userPortion: Int,
    @SerializedName("img_source")
    val imgSource: String="",
    @SerializedName("activity_level")
    val activityLevel: Double = 0.0,
    val height: Double = 0.0,
    val sex: String?,
    val weight: Double = 0.0,
    val age: Int? = 0,
    val rating: Double = 0.0,
    @SerializedName("saved_recipes")
    @JvmField
    var savedRecipes: MutableList<Recipe> = mutableListOf(),
    @SerializedName("created_recipes")
    @JvmField
    var createdRecipes: MutableList<Recipe> = mutableListOf(),
    @SerializedName("created_at")
    val createdDate: String?,
    @SerializedName("updated_at")
    val updatedDate: String?,
    @SerializedName("followers_c")
    val followers: Int = 0,
    @SerializedName("followeds_c")
    val followeds: Int = 0,
    @SerializedName("fitness_goal")
    var fitnessGoal: Goal? = null



) : Parcelable {

    // this is needed
    fun initLists() {

        savedRecipes = mutableListOf()
        createdRecipes = mutableListOf()

    }


    fun checkIfSaved(recipeId: Int): Int {

        return if (savedRecipes.isNotEmpty())
            savedRecipes.indexOfFirst { it.id == recipeId }
        else
            -1
    }

    fun addSave(recipe: Recipe){
        if (checkIfSaved(recipe.id) == -1){
            savedRecipes.add(recipe)
        }
    }


    fun removeSave(recipe: Recipe){
        val index = checkIfSaved(recipe.id)
        if (index != -1)
            savedRecipes.removeAt(index)
    }

    fun removeSave(recipe: RecipeSimplified){
        val index = checkIfSaved(recipe.id)
        if (index != -1)
            savedRecipes.removeAt(index)
    }


}