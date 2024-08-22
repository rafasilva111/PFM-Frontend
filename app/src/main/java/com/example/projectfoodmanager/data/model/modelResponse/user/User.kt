package com.example.projectfoodmanager.data.model.modelResponse.user

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.user.goal.Goal
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Int,
    val name: String,
    val username: String,
    @SerializedName("birth_date")
    var birthDate: String?,
    val email: String,
    val description: String,
    @SerializedName("fmc_token")
    var fmcToken: String?,
    @SerializedName("profile_type")
    val profileType: String,
    val verified: Boolean,
    @SerializedName("user_type")
    var userType: String,
    @SerializedName("user_portion")
    val userPortion: Int,
    @SerializedName("img_source")
    val imgSource: String="",
    @SerializedName("activity_level")
    var activityLevel: Double = 0.0,
    val height: Double = 0.0,
    val sex: String?,
    val weight: Double = 0.0,
    val age: Int? = 0,
    val rating: Double = 0.0,
    @SerializedName("created_at")
    val createdDate: String?,
    @SerializedName("updated_at")
    val updatedDate: String?,
    @SerializedName("followers_c")
    val followers: Int = 0,
    @SerializedName("follows_c")
    val follows: Int = 0,
    @SerializedName("fitness_goal")
    var fitnessGoal: Goal? = null

) : Parcelable