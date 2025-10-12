package com.example.projectfoodmanager.data.model.user.goal

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Goal(
    @SerializedName("goal")
    val goal: Float,
    @SerializedName("calories")
    val energy: Float,
    @SerializedName("proteins_upper_limit")
    val proteinUpperLimit: Float,
    @SerializedName("proteins_lower_limit")
    val proteinLowerLimit: Float,
    @SerializedName("fat_lower_limit")
    val fatLowerLimit: Float,
    @SerializedName("fat_upper_limit")
    var fatUpperLimit: Float,
    @SerializedName("saturated_fat")
    var saturatedFat: Float,
    @SerializedName("carbohydrates")
    var carbohydrates: Float,

) : Parcelable