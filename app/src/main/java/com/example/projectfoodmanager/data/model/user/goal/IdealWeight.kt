package com.example.projectfoodmanager.data.model.user.goal

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeSimplefied
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class IdealWeight(
    @SerializedName("upper_limit")
    val upperLimit: Float,
    @SerializedName("lower_limit")
    val lowerLimit: Float,
) : Parcelable