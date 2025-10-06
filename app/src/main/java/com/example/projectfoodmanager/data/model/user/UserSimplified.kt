package com.example.projectfoodmanager.data.model.user

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserSimplified(
    val id: Int,
    val name: String,
    val username: String,
    val description: String,
    val profile_type: String,
    val verified: Boolean,
    @SerializedName("user_type")
    var userType: String,
    @SerializedName("image")
    val imgSource: String="",
    @SerializedName("follows_c")
    val followsCount: Int=0,
    @SerializedName("followers_c")
    val followersCount: Int=0
) : Parcelable