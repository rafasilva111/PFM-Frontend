package com.example.projectfoodmanager.data.model.dtos.recipe.comment

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.user.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommentDTO(

    val text: String

): Parcelable