package com.example.projectfoodmanager.data.model.modelResponse.recipe

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class IngredientBase(
    val id: Int,
    val name: String
) : Parcelable
