package com.example.projectfoodmanager.data.model

import android.os.Parcelable
import com.google.firebase.storage.StorageReference
import kotlinx.parcelize.Parcelize

data class Recipe(
    val info: Recipe_info,
    var storageReference: StorageReference,
)


@Parcelize
data class Recipe_info(
    val id: Long? = null,
    val title: String = "",
    val desc: String = "",
    val portion: String = "",
    val source: String = "",
    val company: String = "",
    val description: String = "",
    val img: String = "",
    val time: String = "",
    val difficulty: String= "",
    val nrPersons: String="",
    val remote_rating: String="",
    val app_rating: String="",
    val date: String="",
    val tags: String="",
    val ingredients: HashMap<String,String> = HashMap<String,String>(),
    val nutrition_table: HashMap<String,String> = HashMap<String,String>(),
    val preparation: HashMap<String,String> = HashMap<String,String>(),
) : Parcelable

