package com.example.projectfoodmanager.data.model.modelResponse.recipe

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.user.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecipeSimplefied(
    val id: Int,
    val title: String,
    val description: String,
    val img_source: String,
    val difficulty: String,
    val portion: String,
    val time: String,
    val likes: Int,
    val views: Int,
    val rating: String,
    val source_rating: String,
    val source_link: String,
    val created_by: User,
    val created_date: String,
    val updated_date: String
): Parcelable