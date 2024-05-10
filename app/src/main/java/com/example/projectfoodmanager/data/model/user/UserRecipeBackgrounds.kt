package com.example.projectfoodmanager.data.model.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserRecipeBackgrounds(

    val result: UserRecipeBackgroundsList

): Parcelable
