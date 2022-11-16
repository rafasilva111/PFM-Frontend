package com.example.projectfoodmanager.data.repository.models

import android.media.Image
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Recipe(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageRef: String = "",
    @ServerTimestamp
    val date: Date = Date(),
)
