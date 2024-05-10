package com.example.projectfoodmanager.data.model.user

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserSimplified(
    val id: Int,
    val name: String,
    val username: String?,
    val email: String,
    val description: String,
    val profile_type: String,
    val verified: Boolean,
    @SerializedName("user_type")
    var userType: String,
    @SerializedName("img_source")
    val imgSource: String=""
) : Parcelable