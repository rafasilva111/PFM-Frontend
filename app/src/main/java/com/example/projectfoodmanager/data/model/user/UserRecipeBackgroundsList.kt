package com.example.projectfoodmanager.data.model.user

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserRecipeBackgroundsList(

    val recipes_created: MutableList<Recipe>,
    val recipes_liked: MutableList<Recipe>,
    val recipes_saved: MutableList<Recipe>

): Parcelable
