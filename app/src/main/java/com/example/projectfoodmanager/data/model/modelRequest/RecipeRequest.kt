package com.example.projectfoodmanager.data.model.modelRequest

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.modelResponse.ingredients.IngredientQuantity
import com.example.projectfoodmanager.data.model.modelResponse.recipe.NutritionInformations
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Preparation
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RecipeRequest(
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("img_source")
    val img_source: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("source_link")
    val source_link: String? = null,
    @SerializedName("source_rating")
    val source_rating: String? = null,
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
    val nutrition_information: NutritionInformations? = null,
    ): java.io.Serializable, Parcelable