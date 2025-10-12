package com.example.projectfoodmanager.data.model.notification

import android.os.Parcelable
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeSimplified
import com.example.projectfoodmanager.data.model.modelResponse.recipe.comment.Comment
import com.example.projectfoodmanager.data.model.user.UserSimplified
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss")

@Parcelize
data class Notification(
    val type: Int,
    val id: Int,
    val from_user: UserSimplified,
    var recipe: RecipeSimplified? = null,
    val comment: Comment? = null,
    val title: String,
    val message: String,
    val created_date: String,
    val seen: Boolean = false
): Parcelable{

        fun getDate(): LocalDateTime {
            return LocalDateTime.parse(this.created_date, formatter)
        }
    companion object {

    }


}