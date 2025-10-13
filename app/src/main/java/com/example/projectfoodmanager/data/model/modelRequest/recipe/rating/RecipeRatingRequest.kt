package com.example.projectfoodmanager.data.model.modelRequest.recipe.rating

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecipeRatingRequest(
    val rating: Float
) : Parcelable