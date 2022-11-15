package com.example.projectfoodmanager.data.repository.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Recipe(
    val id: String,
    val description: String,
    @ServerTimestamp
    val date: Date,
)
