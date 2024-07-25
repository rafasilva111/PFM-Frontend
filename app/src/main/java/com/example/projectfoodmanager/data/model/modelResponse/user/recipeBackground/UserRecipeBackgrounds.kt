package com.example.projectfoodmanager.data.model.modelResponse.user.recipeBackground

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserRecipeBackgrounds(

    @SerializedName("created_recipes")
    val createdRecipes: MutableList<Recipe> = mutableListOf(),

    @SerializedName("saved_recipes")
    val savedRecipes: MutableList<Recipe> = mutableListOf(),

    ): Parcelable
