package com.example.projectfoodmanager.data.model.modelRequest.geral

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class IdListRequest(
    @SerializedName("id_list")
    val idList: MutableList<Int>
):  Parcelable
