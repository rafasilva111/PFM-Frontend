package com.example.projectfoodmanager.data.model.modelResponse.recipe

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Preparation(
    var description: String,
    val step: Int
): Parcelable