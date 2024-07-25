package com.example.projectfoodmanager.data.model.modelResponse.metadata

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Metadata(
    val page: Int,
    @SerializedName("page_size")
    val pageSize: Int,
    @SerializedName("total_items")
    val totalItems: Int,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("next_page")
    val nextPage: String?,
    @SerializedName("previous_page")
    val previousPage: String?
) : Parcelable