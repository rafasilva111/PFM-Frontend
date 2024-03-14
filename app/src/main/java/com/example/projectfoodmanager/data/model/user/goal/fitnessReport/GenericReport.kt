package com.example.projectfoodmanager.data.model.user.goal.fitnessReport

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GenericReport(
    @SerializedName("calories")
    val calories: String?,
    @SerializedName("fat")
    val fat: FatReport,
    @SerializedName("carbohydrates")
    val carbohydrates: CarbohydrateReport,
) : Parcelable