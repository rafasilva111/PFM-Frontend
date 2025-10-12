package com.example.projectfoodmanager.data.model.user.goal

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.user.goal.fitnessReport.GenericReport
import com.example.projectfoodmanager.data.model.user.goal.fitnessReport.Limits
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FitnessReport(
    @SerializedName("ideal_weight")
    val idealWeight: Limits,
    @SerializedName("protein")
    val protein: Limits,
    @SerializedName("plus")
    val plus: GenericReport,
    @SerializedName("plus_half")
    val plusHalf: GenericReport,
    @SerializedName("maintain")
    val maintain: GenericReport,
    @SerializedName("minus_half")
    val minusHalf: GenericReport,
    @SerializedName("minus")
    val minus: GenericReport,
) : Parcelable