package com.example.projectfoodmanager.data.model.recipe

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.user.UserSimplified
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecipeSimplified(
    val id: Int,
    val title: String,
    val description: String,
    @SerializedName("img_source")
    val imgSource: String,
    val difficulty: String,
    val portion: String,
    val time: String,
    val likes: Int,
    val views: Int,
    val rating: Double = 0.0,
    @SerializedName("source_rating")
    val sourceRating: String,
    @SerializedName("source_link")
    val sourceLink: String,
    @SerializedName("created_by")
    var createdBy: UserSimplified,
    @SerializedName("created_date")
    val createdDate: String,
    @SerializedName("updated_date")
    val updatedDate: String
): Parcelable