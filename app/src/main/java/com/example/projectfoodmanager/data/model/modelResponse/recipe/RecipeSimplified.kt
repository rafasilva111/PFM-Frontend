package com.example.projectfoodmanager.data.model.modelResponse.recipe

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.user.UserSimplified
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecipeSimplified(
    val id: Int,
    val title: String,
    val description: String,
    @SerializedName("image")
    val imgSource: String,
    val difficulty: String,
    @SerializedName("portion_lower")
    val portionLower: Int,
    @SerializedName("portion_upper")
    val portionUpper: Int?,
    @SerializedName("portion_units")
    val portionUnits: String,
    val time: Int,
    val likes: Int,
    val views: Int,
    val verified: Boolean,
    val rating: Double = 0.0,
    @SerializedName("source_rating")
    val sourceRating: Double = 0.0,
    @SerializedName("source_link")
    val sourceLink: String,
    @SerializedName("created_by")
    var createdBy: UserSimplified,
    @SerializedName("created_at")
    val createdDate: String,
    @SerializedName("updated_at")
    val updatedDate: String,

    var tags: MutableList<Tag> = mutableListOf(),

    val saved: Boolean,
    val liked: Boolean

): Parcelable