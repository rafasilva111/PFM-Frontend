package com.example.projectfoodmanager.data.model.modelResponse

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserResponse(
    @SerializedName("id")
    val id: String="",
    @SerializedName("uuid")
    val uuid: String="",
    @SerializedName("first_name")
    val first_name: String="",
    @SerializedName("last_name")
    val last_name: String="",
    @SerializedName("birth_date")
    val birth_date: String="",
    @SerializedName("age")
    val age: Int=0,
    @SerializedName("email")
    val email: String="",
    @SerializedName("profile_type")
    val profile_type: String="",
    @SerializedName("verified")
    val verified: Boolean=false,
    @SerializedName("user_type")
    val user_type: String="",
    @SerializedName("img_source")
    val img_source: String="",
    @SerializedName("created_date")
    val created_date: String="",
    @SerializedName("updated_date")
    val updated_date: String="",
    @SerializedName("activity_level")
    val activity_level: String="",
    @SerializedName("altura")
    val height: String="",
    @SerializedName("sexo")
    val sex: String="",
    @SerializedName("peso")
    val weight: String="",
) : Serializable
