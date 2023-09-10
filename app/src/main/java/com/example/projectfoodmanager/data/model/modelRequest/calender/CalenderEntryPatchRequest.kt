package com.example.projectfoodmanager.data.model.modelRequest.calender

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CalenderEntryPatchRequest(
    var tag: String?=null,
    var realization_date: String?=null,
    val checked_done:Boolean?=null
) : Parcelable
