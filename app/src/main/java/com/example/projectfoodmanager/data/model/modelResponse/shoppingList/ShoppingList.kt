package com.example.projectfoodmanager.data.model.modelResponse.shoppingList

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShoppingList (
    val id: Int,
    val name: String,
    @SerializedName("updated_at")
    val updatedDate: String,
    @SerializedName("created_at")
    val createdDate: String,
    @SerializedName("shopping_ingredients")
    val shoppingIngredients: MutableList<ShoppingIngredient>,
    val archived: Boolean
    ): Parcelable