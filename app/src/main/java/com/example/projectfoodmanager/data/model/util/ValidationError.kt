package com.example.projectfoodmanager.data.model.util

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ValidationError(
    val errors: Map<String, List<String>>
) : Parcelable