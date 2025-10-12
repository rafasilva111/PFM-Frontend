package com.example.projectfoodmanager.data.model.user.goal.fitnessReport

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CarbohydrateReport(
    @SerializedName("forty_perc")
    val fortyPerc: Float,
    @SerializedName("fifty_perc")
    val fiftyPerc: Float,
    @SerializedName("sixty_five_perc")
    val sixtyFivePerc: Float,
    @SerializedName("seventy_five_perc")
    val seventyFivePerc: Float,
    @SerializedName("only_option")
    val onlyOption: Float?,
) : Parcelable