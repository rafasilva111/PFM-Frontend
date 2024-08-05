package com.example.projectfoodmanager.data.model.modelRequest.calender

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CalenderEntryCheckListRequest(
    @SerializedName("checked_done")
    val checked: MutableList<Int>,

    @SerializedName("checked_removed")
    val unchecked: MutableList<Int>
):  Parcelable
