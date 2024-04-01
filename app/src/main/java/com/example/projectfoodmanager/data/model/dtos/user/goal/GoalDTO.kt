package com.example.projectfoodmanager.data.model.dtos.user.goal

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class GoalDTO(
    @SerializedName("goal")
    var goal: Float? = null,
    @SerializedName("calories")
    var calories: Float? = null,
    @SerializedName("fat_upper_limit")
    var fatUpperLimit: Float? = null,
    @SerializedName("fat_lower_limit")
    var fatLowerLimit: Float? = null,
    @SerializedName("saturated_fat")
    var saturatedFat: Float? = null,
    @SerializedName("carbohydrates")
    var carbohydrates: Float? = null,
    @SerializedName("proteins_upper_limit")
    var proteinsUpperLimit: Float? = null,
    @SerializedName("proteins_lower_limit")
    var proteinsLowerLimit: Float? = null
) : Serializable, Parcelable
