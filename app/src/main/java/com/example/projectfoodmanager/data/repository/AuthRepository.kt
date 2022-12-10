package com.example.projectfoodmanager.data.repository


import android.os.Parcelable
import com.example.projectfoodmanager.data.model.Recipe
import com.example.projectfoodmanager.data.model.User
import com.example.projectfoodmanager.util.UiState

interface AuthRepository {
    fun registerUser(email: String, password: String, user: User, result: (UiState<String>) -> Unit)
    fun updateUserInfo(user: User, result: (UiState<String>) -> Unit)
    fun loginUser(email: String, password: String, result: (UiState<String>) -> Unit)
    fun forgotPassword(user: User, result: (UiState<String>) -> Unit)
    fun storeSession(id: String, result: (User?) -> Unit)
    fun getSession(result: (User?) -> Unit)
    fun removeFavoriteRecipe(recipe: Recipe, result: (UiState<Pair<User,String>>?) -> Unit)
    fun addFavoriteRecipe(recipe: Recipe, result: (UiState<Pair<User,String>>?) -> Unit)
    fun getFavoritesRecipeClass(result: (UiState<ArrayList<Recipe>?>) -> Unit)
    fun getFavoritesRecipeString(result: (UiState<ArrayList<String>?>) -> Unit)
    fun getUserSession(result: (UiState<User?>) -> Unit)
}