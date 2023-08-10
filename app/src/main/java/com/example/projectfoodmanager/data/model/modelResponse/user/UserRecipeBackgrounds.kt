package com.example.projectfoodmanager.data.model.modelResponse.user

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserRecipeBackgrounds(

    val result: UserRecipeBackgroundsList

): Parcelable
