package com.example.projectfoodmanager.data.model

data class Recipe(
    val id: Long? = null,
    val title: String = "",
    val description: String = "",
    val imageRef: String = "",
    val preparacao: String = "",
    val tempPreparacao: String = "",
    val difficulty: String= "",
    val nrPersons: String="",
    val remote_rating: String="",
    val app_rating: String="",
    val date: String="",
    val ingredients: HashMap<String,String> = HashMap<String,String>(),
)
