package com.example.projectfoodmanager.data.model.modelResponse.user

data class UserResponse(
    val activity_level: Double,
    val age: String,
    val birth_date: String,
    val created_date: String,
    val email: String,
    val first_name: String,
    val height: Double,
    val id: Int,
    val img_source: String,
    val last_name: String,
    val password: String,
    val profile_type: String,
    val sex: String,
    val updated_date: String,
    val user_type: String,
    val verified: Boolean,
    val weight: Double
)