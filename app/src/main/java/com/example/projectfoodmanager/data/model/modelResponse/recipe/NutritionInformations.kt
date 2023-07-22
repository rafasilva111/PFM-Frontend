package com.example.projectfoodmanager.data.model.modelResponse.recipe

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NutritionInformations(
    val id: Int,
    val energia: String,
    val energia_perc: String,
    val gordura: String,
    val gordura_perc: String,
    val gordura_saturada: String,
    val gordura_saturada_perc: String,
    val hidratos_carbonos: String,
    val hidratos_carbonos_perc: String,
    val hidratos_carbonos_acucares: String,
    val hidratos_carbonos_acucares_perc: String,
    val fibra: String,
    val fibra_perc: String,
    val proteina: String,
    val proteina_perc: String
) : Parcelable