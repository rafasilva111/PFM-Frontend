package com.example.projectfoodmanager.data.model.recipe.comment

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.user.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class Comment(
    val id: Int = -1,
    val text: String,
    val user: User? = null,
    val recipe: Recipe? = null,
    val created_date: String? = "",
    val updated_date: String? = "",
    val likes: Int = 0,
    val liked: Boolean = false

):Parcelable