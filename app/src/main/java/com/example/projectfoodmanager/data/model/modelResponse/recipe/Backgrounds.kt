package com.example.projectfoodmanager.data.model.modelResponse.recipe

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Backgrounds(
    val id: Int,
    val type: String?,
    val user: String?
): Parcelable