package com.example.projectfoodmanager.data.model.modelRequest

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class UserRequest(
    @SerializedName("first_name")
    val first_name: String="",
    @SerializedName("last_name")
    val last_name: String="",
    @SerializedName("birth_date")
    val birth_date: String="",
    @SerializedName("email")
    val email: String="",
    @SerializedName("password")
    val password: String="",
    @SerializedName("img_source")
    val img_source: String="",
    @SerializedName("activity_level")
    val activity_level: Float= 0.00F,
    @SerializedName("height")
    val height: Float=-1F,
    @SerializedName("sex")
    val sex: String="",
    @SerializedName("weight")
    val weight: Float=-1F,
) : Serializable, Parcelable
