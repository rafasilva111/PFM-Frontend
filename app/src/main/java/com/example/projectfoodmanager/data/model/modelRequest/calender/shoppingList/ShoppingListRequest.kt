package com.example.projectfoodmanager.data.model.modelRequest.calender.shoppingList

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShoppingListRequest (

    val name: String? = null,
    val archived: Boolean? =false,
    val shopping_ingredients: MutableList<ShoppingIngredientRequest>? = null
): Parcelable