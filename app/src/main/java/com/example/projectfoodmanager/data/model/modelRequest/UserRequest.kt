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
    val activity_level: Float? = null,
    @SerializedName("height")
    val height: Float?=null,
    @SerializedName("sex")
    val sex: String?= null,
    @SerializedName("weight")
    val weight: Float? =null,
    @SerializedName("user_portion")
    val user_portion: Int? = null,
    @SerializedName("profile_type")
    val profile_type: String? = null,
) : Serializable, Parcelable
