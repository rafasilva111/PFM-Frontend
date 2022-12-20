package com.example.projectfoodmanager.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Recipe(
    var id: String? = null,
    val title: String = "",
    val desc: String = "",
    val portion: String = "",
    val source: String = "",
    val company: String = "",
    val img: String = "",
    val time: String = "",
    val difficulty: String= "",
    val remote_rating: String="",
    val app_rating: String="",
    val date: String="",
    val tags: String="",
    val ingredients: HashMap<String,String> = HashMap(),
    val nutrition_table: HashMap<String,String> = HashMap<String,String>(),
    val preparation: HashMap<String,String> = HashMap<String,String>(),
) : Parcelable

