package com.example.projectfoodmanager.data.model.modelResponse

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class ServerResponse(

    val type: String,
    val title: String,
    val message: String,

): Parcelable