package com.example.projectfoodmanager.data.model.modelResponse.shoppingList

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.modelResponse.metadata.Metadata
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListOfShoppingLists (
    val _metadata: Metadata,
    val result: MutableList<ShoppingList>
    ): Parcelable