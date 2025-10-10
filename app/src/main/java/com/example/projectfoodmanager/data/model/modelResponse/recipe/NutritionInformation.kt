package com.example.projectfoodmanager.data.model.modelResponse.recipe

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class NutritionInformation(

    @SerializedName("energy_kcal")
    val energyKcal: Double,
    @SerializedName("energy_perc")
    val energyPerc: Double,
    @SerializedName("fat_g")
    val fatG: Double,
    @SerializedName("fat_perc")
    val fatPerc: Double,
    @SerializedName("saturates_g")
    val saturatesG: Double,
    @SerializedName("saturates_perc")
    val saturatesPerc: Double,
    @SerializedName("carbohydrates_g")
    val carbohydratesG: Double,
    @SerializedName("carbohydrates_perc")
    val carbohydratesPerc: Double,
    @SerializedName("sugars_g")
    val sugarsG: Double,
    @SerializedName("sugars_perc")
    val sugarsPerc: Double,
    @SerializedName("fiber_g")
    val fiberG: Double,
    @SerializedName("protein_g")
    val proteinG: Double,
    @SerializedName("protein_perc")
    val proteinPerc: Double,
    @SerializedName("salt_g")
    val saltG: Double,
    @SerializedName("salt_perc")
    val saltPerc: Double
) : Parcelable

