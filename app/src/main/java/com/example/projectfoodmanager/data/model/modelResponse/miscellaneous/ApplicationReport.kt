package com.example.projectfoodmanager.data.model.modelResponse.miscellaneous

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ApplicationReport (
    val title: String,
    val message: String
): Parcelable
