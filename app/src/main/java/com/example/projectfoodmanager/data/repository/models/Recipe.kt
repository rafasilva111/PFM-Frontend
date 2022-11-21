package com.example.projectfoodmanager.data.repository.models

import android.media.Image
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Recipe(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageRef: String = "",
    val preparacao: String = "",
    val ingredients: String = "",
    val tempPreparacao: String = "",
    val difficulty: String= "",
    val nrPersons: String="",
    val remote_rating: String="",
    val app_rating: String="",
    @ServerTimestamp
    val date: Date = Date(),
)
