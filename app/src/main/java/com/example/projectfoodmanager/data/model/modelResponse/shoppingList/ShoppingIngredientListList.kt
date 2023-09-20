package com.example.projectfoodmanager.data.model.modelResponse.shoppingList

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.modelResponse.metadata.Metadata
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class ShoppingIngredientListList (
    val _metadata: Metadata,
    val result: MutableList<ShoppingIngredientList>
    ): Parcelable