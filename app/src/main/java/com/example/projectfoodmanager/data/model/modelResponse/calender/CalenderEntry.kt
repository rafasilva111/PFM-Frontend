package com.example.projectfoodmanager.data.model.modelResponse.calender

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.recipe.Recipe
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CalenderEntry(
    val id: Int,
    val recipe: Recipe,
    val tag: String,
    @SerializedName("created_at")
    val createdDate: String,
    @SerializedName("realization_date")
    val realizationDate: String,
    @SerializedName("checked_done")
    var checkedDone: Boolean
) : Parcelable