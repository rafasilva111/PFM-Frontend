package com.example.projectfoodmanager.data.repository


import com.example.projectfoodmanager.data.model.User
import com.example.projectfoodmanager.util.UiState

interface AuthRepository {
    fun registerUser(email: String, password: String, user: User, result: (UiState<String>) -> Unit)
    fun updateUserInfo(user: User, result: (UiState<String>) -> Unit)
    fun loginUser(email: String, password: String, result: (UiState<String>) -> Unit)
    fun forgotPassword(user: User, result: (UiState<String>) -> Unit)
    fun storeSession(id: String, result: (User?) -> Unit)
    fun getSession(result: (User?) -> Unit)
}