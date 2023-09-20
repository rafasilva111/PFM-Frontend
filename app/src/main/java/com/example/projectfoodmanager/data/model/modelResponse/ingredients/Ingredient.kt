package com.example.projectfoodmanager.data.model.modelResponse.ingredients

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ingredient(
    val id: Int,
    val name: String
) : Parcelable
