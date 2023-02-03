package com.example.projectfoodmanager.data.model.modelRequest

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class UserRequest(
    @SerializedName("uuid")
    var uuid: String="",
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
    val activity_level: String="",
    @SerializedName("height")
    val height: String="",
    @SerializedName("sex")
    val sex: String="",
    @SerializedName("weight")
    val weight: String="",
    @SerializedName("profile_type")
    val profile_type: String="",
) : Serializable
