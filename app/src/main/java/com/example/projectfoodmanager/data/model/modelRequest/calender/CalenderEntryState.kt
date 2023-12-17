package com.example.projectfoodmanager.data.model.modelRequest.calender

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CalenderEntryState(
    @SerializedName("id")
    val id: Int,
    @SerializedName("state")
    val state: Boolean

):  Parcelable
