package com.example.projectfoodmanager.data.model.modelRequest.calender

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CalenderEntryListUpdate(
    @SerializedName("calender_entry_state_list")
    val calenderEntryStateList: MutableList<CalenderEntryState>
):  Parcelable
