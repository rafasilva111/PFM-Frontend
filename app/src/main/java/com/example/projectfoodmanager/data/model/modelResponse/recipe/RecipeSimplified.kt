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
    @SerializedName("img_source")
    val imgSource: String,
    val difficulty: String,
    val portion: String,
    val time: String,
    val likes: Int,
    val views: Int,
    val verified: Boolean,
    val rating: Double = 0.0,
    @SerializedName("source_rating")
    val sourceRating: String,
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