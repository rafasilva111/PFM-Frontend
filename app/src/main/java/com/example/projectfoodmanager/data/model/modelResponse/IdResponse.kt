package com.example.projectfoodmanager.data.model.modelResponse

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.modelResponse.ingredients.Ingredient
import kotlinx.parcelize.Parcelize

@Parcelize
data class IdResponse(

    val id: Int,
): Parcelable