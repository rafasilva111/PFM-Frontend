package com.example.projectfoodmanager.data.model.modelResponse.shoppingList

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.modelRequest.calender.shoppingList.ShoppingIngredientRequest
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShoppingListSimplified (
    val result: MutableList<ShoppingIngredient>
): Parcelable