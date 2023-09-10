package com.example.projectfoodmanager.data.model.modelResponse.recipe

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class IngredientBase(
    val id: Int,
    var name: String
) : Parcelable
