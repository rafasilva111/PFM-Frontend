package com.example.projectfoodmanager.data.model.user.goal.fitnessReport

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Limits(
    @SerializedName("upper_limit")
    val upperLimit: Float,
    @SerializedName("lower_limit")
    val lowerLimit: Float,
) : Parcelable