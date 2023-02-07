package com.example.projectfoodmanager.data.model.modelRequest

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class FollowerRequest (
    @SerializedName("id")
    val id: String="",
    @SerializedName("id_user_sender")
    val id_user_sender: String="",
    @SerializedName("id_user_receiver")
    val id_user_receiver: String="",
    @SerializedName("description")
    val description: String="",
    @SerializedName("created_date")
    val created_date: String="",
    @SerializedName("updated_date")
    val updated_date: String=""
) : Serializable