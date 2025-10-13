package com.example.projectfoodmanager.data.model.modelResponse.recipe.rating

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecipeRating(
    val id: Int,
    val rating: Float,
    @SerializedName("recipe")
    val recipeId: Int,
    @SerializedName("user")
    val userId: Int,
    val createdAt: String?,
    val updatedAt: String?
) : Parcelable