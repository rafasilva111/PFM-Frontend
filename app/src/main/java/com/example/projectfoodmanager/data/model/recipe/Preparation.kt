package com.example.projectfoodmanager.data.model.recipe

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Preparation(
    val description: String,
    val step: Int
): Parcelable