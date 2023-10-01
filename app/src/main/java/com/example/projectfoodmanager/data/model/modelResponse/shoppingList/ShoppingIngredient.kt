package com.example.projectfoodmanager.data.model.modelResponse.shoppingList

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.modelResponse.ingredients.Ingredient
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class ShoppingIngredient(

    val id: Int?,
    val ingredient: Ingredient,
    val quantity: Float,
    val checked: Boolean,
    val extra_quantity: Float?,
    val units: String,
    val extra_units: String?
): Parcelable