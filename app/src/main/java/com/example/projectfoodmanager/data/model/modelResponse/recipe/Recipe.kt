package com.example.projectfoodmanager.data.model.modelResponse.recipe

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
    @SerializedName("created_at")
    val createdDate: String,
    @SerializedName("updated_at")
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
    var createdBy: UserSimplified,

    val saved: Boolean,
    var liked: Boolean
) : Parcelable

fun Recipe.toRecipeSimplified(): RecipeSimplified {
    return RecipeSimplified(
        id = this.id,
        title = this.title,
        description = this.description,
        imgSource = this.imgSource,
        difficulty = this.difficulty,
        portion = this.portion,
        time = this.time,
        likes = this.likes,
        views = this.views,
        verified = this.verified,
        rating = this.rating,
        sourceRating = this.sourceRating,
        sourceLink = this.sourceLink,
        createdBy = this.createdBy,
        createdDate = this.createdDate,
        updatedDate = this.updatedDate,
        tags = this.tags,
        saved = this.saved,
        liked = this.liked
    )
}

fun MutableList<Recipe>.toRecipeSimplifiedList(): MutableList<RecipeSimplified> {
    return this.map { it.toRecipeSimplified() }.toMutableList()
}