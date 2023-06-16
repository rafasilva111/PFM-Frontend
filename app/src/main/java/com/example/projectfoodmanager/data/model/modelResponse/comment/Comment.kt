package com.example.projectfoodmanager.data.model.modelResponse.comment

import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.user.User

data class Comment(
    val id: Int,
    val text: String,
    val user: User,
    val recipe: Recipe,
    val created_date: String,
    val updated_date: String

)