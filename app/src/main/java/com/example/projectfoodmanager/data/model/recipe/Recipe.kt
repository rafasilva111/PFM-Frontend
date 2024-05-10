package com.example.projectfoodmanager.data.model.recipe

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.modelResponse.ingredients.IngredientQuantity
import com.example.projectfoodmanager.data.model.user.UserSimplified
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    val id: Int,
    val title: String,
    @SerializedName("img_source")
    val imgSource: String,
    val verified: Boolean,
    val rating: Double = 0.0,
    @SerializedName("source_rating")
    val sourceRating: String,
    @SerializedName("source_link")
    val sourceLink: String,
    val time: String,
    val portion: String,
    val difficulty: String,
    @SerializedName("created_date")
    val createdDate: String,
    @SerializedName("updated_date")
    val updatedDate: String,
    var likes: Int,
    var comments: Int,
    val views: Int, //Ainda não esta a ser usado
    val description: String,
    var tags: MutableList<Tag> = mutableListOf(),
    val ingredients: MutableList<IngredientQuantity> = mutableListOf(),
    var preparation: MutableList<Preparation> = mutableListOf(),
    @SerializedName("nutrition_information")
    val nutritionInformation: NutritionInformations?,
    @SerializedName("created_by")
    var createdBy: UserSimplified
) : Parcelable