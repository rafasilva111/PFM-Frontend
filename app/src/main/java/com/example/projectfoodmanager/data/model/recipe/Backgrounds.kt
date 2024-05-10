package com.example.projectfoodmanager.data.model.recipe

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.user.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class Backgrounds(
    val id: Int,
    val type: String,
    val user: User
): Parcelable