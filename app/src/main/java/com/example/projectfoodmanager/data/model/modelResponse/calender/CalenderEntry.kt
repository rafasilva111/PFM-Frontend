package com.example.projectfoodmanager.data.model.modelResponse.calender

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CalenderEntry(
    val id: Int,
    val user: User,
    val recipe: Recipe,
    val tag: String,
    val created_date: String,
    val realization_date: String,
    val checked_done: Boolean
) : Parcelable