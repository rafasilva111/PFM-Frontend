package com.example.projectfoodmanager.data.model.modelResponse.recipe.list

data class RecipeResult(
    val company: String,
    val created_date: String,
    val description: String,
    val difficulty: String,
    val id: Int,
    val img_source: String,
    val ingredients: List<List<String>>,
    val likes: Int,
    val nutrition_information: NutritionInformation,
    val portion: String,
    val preparation: List<String>,
    val source_rating: Double,
    val tags: List<String>,
    val time: String,
    val title: String,
    val updated_date: String,
    val views: Int
)