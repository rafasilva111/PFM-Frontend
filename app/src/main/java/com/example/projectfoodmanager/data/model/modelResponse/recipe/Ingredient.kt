package com.example.projectfoodmanager.data.model.modelResponse.recipe

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ingredient(
    val id: Int,
    val ingredient: IngredientBase,
    var quantity_original: String,
    val quantity_normalized: Float? = 0.0f,
    val units_normalized: String? = null
) : Parcelable