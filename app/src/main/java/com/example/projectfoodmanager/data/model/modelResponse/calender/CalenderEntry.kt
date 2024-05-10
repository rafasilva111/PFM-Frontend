package com.example.projectfoodmanager.data.model.modelResponse.calender

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.recipe.Recipe
import com.example.projectfoodmanager.data.model.user.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class CalenderEntry(
    val id: Int,
    var user: User,
    val recipe: Recipe,
    val tag: String,
    val created_date: String,
    val realization_date: String,
    var checked_done: Boolean
) : Parcelable