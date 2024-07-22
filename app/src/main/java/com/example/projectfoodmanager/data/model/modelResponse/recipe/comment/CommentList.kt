package com.example.projectfoodmanager.data.model.modelResponse.recipe.comment
import com.example.projectfoodmanager.data.model.modelResponse.metadata.Metadata

data class CommentList(
    val _metadata: Metadata,
    val result: MutableList<Comment>
)