package com.example.projectfoodmanager.data.model.modelResponse.recipe
import com.example.projectfoodmanager.data.model.modelResponse.metadata.Metadata
data class RecipeListResponse(
    val _metadata: Metadata,
    val result: List<RecipeResult>
)