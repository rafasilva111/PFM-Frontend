package com.example.projectfoodmanager.data.model.modelResponse.user.profile

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.modelResponse.user.ProfileType
import com.example.projectfoodmanager.data.model.modelResponse.user.UserType
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class UserProfile(
    val id: Int,
    val name: String,
    val username: String,
    val description: String,
    @SerializedName("profile_type")
    val profileType: ProfileType,
    val verified: Boolean,
    @SerializedName("user_type")
    var userType: UserType,
    @SerializedName("img_source")
    val imgSource: String="",
    @SerializedName("follows_c")
    val followsCount: Int=0,
    @SerializedName("followers_c")
    val followersCount: Int=0,
    @SerializedName("recipes_created")
    val recipesCreated: Int=0
) : Parcelable
