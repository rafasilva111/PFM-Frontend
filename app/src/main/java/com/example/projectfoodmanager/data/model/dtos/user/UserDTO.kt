package com.example.projectfoodmanager.data.model.dtos.user

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class UserDTO(
    @SerializedName("username")
    var userName: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("birth_date")
    var birth_date: String? = null,
    @SerializedName("fmc_token")
    var fmc_token: String? = null,
    @SerializedName("email")
    var email: String?= null,
    @SerializedName("password")
    var password: String?= null,
    @SerializedName("img_source")
    var img_source: String? = null,
    @SerializedName("activity_level")
    var activity_level: Float? = null,
    @SerializedName("height")
    var height: Float?=null,
    @SerializedName("sex")
    var sex: String?= null,
    @SerializedName("weight")
    var weight: Float? =null,
    @SerializedName("user_portion")
    var user_portion: Int? = null,
    @SerializedName("profile_type")
    var profile_type: String? = null,
) : Serializable, Parcelable
