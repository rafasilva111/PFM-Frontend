package com.example.projectfoodmanager.data.model.modelResponse.recipe

data class RecipeResult(
    val backgrounds: Backgrounds,
    val company: String,
    val created_date: String,
    val description: String,
    val difficulty: String,
    val id: Int,
    val img_source: String,
    val ingredients: List<Ingredient>,
    val likes: Int,
    val nutrition_informations: NutritionInformations,
    val portion: String,
    val preparation: List<Preparation>,
    val source_link: String,
    val source_rating: String,
    val tags: List<String>,
    val time: String,
    val title: String,
    val updated_date: String,
    val views: Int
)