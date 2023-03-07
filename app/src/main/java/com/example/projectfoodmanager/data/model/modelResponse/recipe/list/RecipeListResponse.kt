package com.example.projectfoodmanager.data.model.modelResponse.recipe.list

data class RecipeListResponse(
    val _metadata: Metadata,
    val recipe_result: List<RecipeResult>
)