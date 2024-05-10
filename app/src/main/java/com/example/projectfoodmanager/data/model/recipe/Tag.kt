package com.example.projectfoodmanager.data.model.recipe

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tag(
    val id: Int,
    val title: String
): Parcelable