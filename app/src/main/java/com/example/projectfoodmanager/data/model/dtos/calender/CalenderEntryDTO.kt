package com.example.projectfoodmanager.data.model.dtos.calender

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CalenderEntryDTO(
    val id: Int? = null,
    val tag: String? = null,
    val portion: Int? = null,
    @SerializedName("realization_date")
    val realizationDate: String? = null,
    @SerializedName("checked_done")
    var checkedDone: Boolean? = null,
): Parcelable