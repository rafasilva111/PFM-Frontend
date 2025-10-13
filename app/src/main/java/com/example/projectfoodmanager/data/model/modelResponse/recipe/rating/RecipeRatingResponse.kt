package com.example.projectfoodmanager.data.model.modelResponse.recipe.rating

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecipeRatingResponse(
    val rating: Float
) : Parcelable