package com.example.projectfoodmanager.data.model.modelRequest.comment

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateCommentRequest(
    val text: String,
):Parcelable