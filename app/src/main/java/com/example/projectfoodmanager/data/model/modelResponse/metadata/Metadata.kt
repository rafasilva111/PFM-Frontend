package com.example.projectfoodmanager.data.model.modelResponse.metadata

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Metadata(
    val current_page: Int,
    val items_per_page: Int,
    val total_items: Int,
    val total_pages: Int,
    val next: String?,
    val previous: String?
) : Parcelable