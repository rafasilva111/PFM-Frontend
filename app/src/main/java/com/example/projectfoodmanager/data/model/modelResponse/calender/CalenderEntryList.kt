package com.example.projectfoodmanager.data.model.modelResponse.calender
import android.os.Parcelable
import com.example.projectfoodmanager.data.model.modelResponse.metadata.Metadata
import kotlinx.parcelize.Parcelize

@Parcelize
data class CalenderEntryList(
    val _metadata: Metadata,
    val result: MutableList<CalenderEntry>
): Parcelable