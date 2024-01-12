package com.example.projectfoodmanager.data.model.modelResponse.recipe

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.modelResponse.ingredients.IngredientQuantity
import com.example.projectfoodmanager.data.model.user.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    val id: Int,
    val title: String,
    val img_source: String,
    val verified: Boolean,
    val rating: Double = 0.0,
    val source_rating: String,
    val source_link: String,
    val time: String,
    val portion: String,
    val difficulty: String,
    val created_date: String,
    val updated_date: String,
    var likes: Int,
    var comments: Int,
    val views: Int, //Ainda não esta a ser usado
    val description: String,
    var tags: MutableList<Tag> = mutableListOf(),
    val ingredients: MutableList<IngredientQuantity>,
    val preparation: MutableList<Preparation>,
    val nutrition_information: NutritionInformations?,
    var created_by: User
) : Parcelable