package com.example.projectfoodmanager.data.model.modelResponse.recipe
import com.example.projectfoodmanager.data.model.modelResponse.metadata.Metadata
data class RecipeList(
    val _metadata: Metadata,
    val result: MutableList<RecipeSimplified>
)