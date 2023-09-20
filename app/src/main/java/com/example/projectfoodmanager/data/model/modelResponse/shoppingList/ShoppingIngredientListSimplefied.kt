package com.example.projectfoodmanager.data.model.modelResponse.shoppingList

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShoppingIngredientListSimplefied (
    val result: MutableList<ShoppingIngredient>
): Parcelable