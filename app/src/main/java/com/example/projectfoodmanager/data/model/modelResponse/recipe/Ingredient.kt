package com.example.projectfoodmanager.data.model.modelResponse.recipe

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ingredient(
    val id: Int,
    val ingredient: IngredientBase,
    val quantity_original: String,
    val quantity_normalized: Float,
    val units_normalized: String
) : Parcelable