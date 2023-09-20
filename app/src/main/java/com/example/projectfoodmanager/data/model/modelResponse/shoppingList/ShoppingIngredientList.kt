package com.example.projectfoodmanager.data.model.modelResponse.shoppingList

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class ShoppingIngredientList (
    val id: Int,
    val name: String,
    val updated_date: String,
    val created_date: String,
    @SerializedName("shopping_ingredients")
    val shoppingIngredients: MutableList<ShoppingIngredient>,
    ): Parcelable