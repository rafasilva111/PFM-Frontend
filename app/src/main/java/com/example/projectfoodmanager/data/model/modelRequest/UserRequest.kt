package com.example.projectfoodmanager.data.model.modelRequest

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class UserRequest(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("birth_date")
    val birth_date: String? = null,
    @SerializedName("fmc_token")
    val fmc_token: String? = null,
    @SerializedName("email")
    val email: String?= null,
    @SerializedName("password")
    val password: String?= null,
    @SerializedName("img_source")
    var img_source: String? = null,
    @SerializedName("activity_level")
    val activity_level: Float? = 0.00F,
    @SerializedName("height")
    val height: Float?=-1F,
    @SerializedName("sex")
    val sex: String?= null,
    @SerializedName("weight")
    val weight: Float? =-1F,
) : Serializable, Parcelable
