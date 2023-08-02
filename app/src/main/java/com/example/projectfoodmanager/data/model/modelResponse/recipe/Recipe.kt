package com.example.projectfoodmanager.data.model.modelResponse.recipe

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    val backgrounds: List<Backgrounds>,
    val created_date: String,
    val description: String,
    val difficulty: String,
    val verified: Boolean,
    val id: Int,
    val img_source: String,
    val ingredients: List<Ingredient>,
    var likes: Int,
    var comments: Int,
    val nutrition_information: NutritionInformations,
    val portion: String,
    val preparation: List<Preparation>,
    val rating: Double = 0.0,
    val source_link: String,
    val source_rating: String,
    var tags: List<String> = arrayListOf(),
    val time: String,
    val title: String,
    val updated_date: String,
    val views: Int,
    val created_by: User
) : Parcelable