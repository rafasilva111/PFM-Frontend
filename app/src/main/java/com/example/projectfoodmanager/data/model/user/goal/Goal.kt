package com.example.projectfoodmanager.data.model.user.goal

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeSimplefied
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Goal(
    @SerializedName("energia")
    val energy: Float,
    @SerializedName("proteina_upper_limit")
    val proteinUpperLimit: Float,
    @SerializedName("proteina_lower_limit")
    val proteinLowerLimit: Float,
    val goal: Float,
    @SerializedName("gordura_lower_limit")
    val fatLowerLimit: Float,
    @SerializedName("gordura_upper_limit")
    var fatUpperLimit: Float,
    @SerializedName("gordura_saturada")
    var saturatedFat: Float,
    @SerializedName("hidratos_carbonos")
    var carbohydrates: Float,

) : Parcelable