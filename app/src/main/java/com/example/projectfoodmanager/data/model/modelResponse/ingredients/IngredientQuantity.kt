package com.example.projectfoodmanager.data.model.modelResponse.ingredients

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class IngredientQuantity(
    val id: Int,
    val ingredient: Ingredient,
    val quantity_original: String,
    val quantity_normalized: Float,
    val units_normalized: String
) : Parcelable