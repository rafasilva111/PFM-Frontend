package com.example.projectfoodmanager.data.model.user

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserSimplified(
    val id: Int,
    val name: String,
    val description: String,
    @SerializedName("profile_type")
    val profileType: String,
    val verified: Boolean,
    var type: String,
    @SerializedName("image")
    val imgSource: String="",
    @SerializedName("follows_c")
    val followsCount: Int=0,
    @SerializedName("followers_c")
    val followersCount: Int=0
) : Parcelable