package com.example.projectfoodmanager.data.model.modelRequest.recipe

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.modelResponse.ingredients.IngredientQuantity
import com.example.projectfoodmanager.data.model.modelResponse.recipe.NutritionInformation
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Preparation
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class RecipeRequest(
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("img_source")
    val imgSource: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("source_link")
    val sourceLink: String? = null,
    @SerializedName("time")
    val time: String? = null,
    @SerializedName("difficulty")
    val difficulty: String? = null,
    @SerializedName("portion")
    val portion: String? = null,
    @SerializedName("tags")
    var tags: List<String> = arrayListOf(),
    @SerializedName("ingredients")
    var ingredients: List<IngredientQuantity> = arrayListOf(),
    @SerializedName("preparation")
    val preparation: List<Preparation> = arrayListOf(),
    @SerializedName("nutrition_information")
    val nutritionInformation: NutritionInformation? = null,
    ): Serializable, Parcelable