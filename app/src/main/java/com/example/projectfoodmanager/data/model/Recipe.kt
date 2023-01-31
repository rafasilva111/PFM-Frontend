package com.example.projectfoodmanager.data.model

import android.os.Parcelable
import android.util.Log
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize


@Parcelize
data class Recipe(
    var id: String = "",
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
    var likes: ArrayList<String> = arrayListOf(),
    val ingredients: HashMap<String,String> = HashMap(),
    val nutrition_table: HashMap<String,String> = HashMap<String,String>(),
    val preparation: HashMap<String,String> = HashMap<String,String>(),

) : Parcelable {


    fun addLike(userId:String) {
        if (this.likes.contains(userId)){
            Log.d("RecipeModel", "addLike: This recipe is already liked by this user: $userId")
        }
        else
            this.likes.add(userId)
    }
    fun removeLike(userId:String) {
        if (!this.likes.contains(userId))
            Log.d("RecipeModel", "addLike: This recipe has already been removed by this user: $userId")
        else
            this.likes.remove(userId)
    }
}

