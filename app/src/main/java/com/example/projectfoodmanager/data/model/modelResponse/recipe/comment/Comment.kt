package com.example.projectfoodmanager.data.model.modelResponse.recipe.comment

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeSimplified
import com.example.projectfoodmanager.data.model.user.UserSimplified
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Comment(
    val id: Int = -1,
    val text: String,
    val user: UserSimplified? = null,
    val recipe: RecipeSimplified,
    @SerializedName("created_at")
    val createdDate: String? = "",
    @SerializedName("updated_at")
    val updatedDate: String? = "",
    val likes: Int = 0,
    val liked: Boolean = false

):Parcelable