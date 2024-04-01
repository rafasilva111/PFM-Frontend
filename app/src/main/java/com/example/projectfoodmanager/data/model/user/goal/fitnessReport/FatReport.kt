package com.example.projectfoodmanager.data.model.user.goal.fitnessReport

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class FatReport(
    @SerializedName("fat_twenty_thirty")
    val fatTwentyThirty: Limits,
    @SerializedName("saturated_fat_ten")
    val saturatedFatTen: Float,
    @SerializedName("saturated_fat_seven")
    val saturatedFatSeven: Float,
) : Parcelable