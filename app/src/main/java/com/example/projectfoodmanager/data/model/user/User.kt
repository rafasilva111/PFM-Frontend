package com.example.projectfoodmanager.data.model.user

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.recipe.Recipe
import com.example.projectfoodmanager.data.model.recipe.RecipeSimplified
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
    @SerializedName("liked_recipes")
    @JvmField
    var likedRecipes: MutableList<Recipe> = mutableListOf(),
    @SerializedName("saved_recipes")
    @JvmField
    var savedRecipes: MutableList<Recipe> = mutableListOf(),
    @SerializedName("created_recipes")
    @JvmField
    var createdRecipes: MutableList<Recipe> = mutableListOf(),
    @SerializedName("created_date")
    val createdDate: String?,
    @SerializedName("updated_date")
    val updatedDate: String?,
    @SerializedName("followed_state")
    val followedState: String? = null,
    @SerializedName("followers_c")
    val followers: Int = 0,
    @SerializedName("followeds_c")
    val followeds: Int = 0,
    @SerializedName("followers_request_c")
    val followersRequest: Int = 0,
    @SerializedName("followeds_request_c")
    val followedsRequest: Int = 0,
    @SerializedName("fitness_goal")
    var fitnessGoal: Goal? = null



) : Parcelable {

    // this is needed
    fun initLists() {
        if (likedRecipes == null){
            likedRecipes = mutableListOf()
        }
        if (savedRecipes == null){
            savedRecipes = mutableListOf()
        }
        if (createdRecipes == null){
            createdRecipes = mutableListOf()
        }
    }


    fun checkIfLiked(recipe: Recipe): Int {
        return if (likedRecipes.isNotEmpty())
            likedRecipes.indexOfFirst { it.id == recipe.id }
        else
            -1
    }

    fun checkIfLiked(recipe: RecipeSimplified): Int {
        return if (likedRecipes.isNotEmpty())
            likedRecipes.indexOfFirst { it.id == recipe.id }
        else
            -1
    }

    fun  addLike(recipe: Recipe){
        if (this.checkIfLiked(recipe) == -1){
            likedRecipes.add(recipe)
        }
    }

    fun removeLike(recipe: Recipe){
        val index = checkIfLiked(recipe)
        if (index != -1)
            likedRecipes.removeAt(index)
    }

    fun checkIfSaved(recipe: Recipe): Int {
        return if (savedRecipes.isNotEmpty())
            savedRecipes.indexOfFirst { it.id == recipe.id }
        else
            -1
    }

    fun addSave(recipe: Recipe){
        if (checkIfSaved(recipe) == -1){
            savedRecipes.add(recipe)
        }
    }

    fun removeSave(recipe: Recipe){
        val index = checkIfSaved(recipe)
        if (index != -1)
            savedRecipes.removeAt(index)
    }


}