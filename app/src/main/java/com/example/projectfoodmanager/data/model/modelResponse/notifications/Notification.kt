package com.example.projectfoodmanager.data.model.modelResponse.notifications

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notification(
    val type: Int,
    val id: Int,
    val user: String?,
    val title: String,
    val message: String,
    val created_date: String,
    val seen: Boolean
): Parcelable