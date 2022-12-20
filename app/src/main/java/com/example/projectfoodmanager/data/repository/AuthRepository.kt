package com.example.projectfoodmanager.data.repository


import android.os.Parcelable
import com.example.projectfoodmanager.data.model.Recipe
import com.example.projectfoodmanager.data.model.User
import com.example.projectfoodmanager.util.UiState

interface AuthRepository {

    //Ui States
    //auth
    fun registerUser(email: String, password: String, user: User, result: (UiState<String>) -> Unit)
    fun updateUserInfo(user: User, result: (UiState<String>) -> Unit)
    fun loginUser(email: String, password: String, result: (UiState<String>) -> Unit)
    fun forgotPassword(email: String, result: (UiState<String>) -> Unit)

    // session
    fun getUserSession(result: (UiState<User?>) -> Unit)

    // user favorites
    fun removeFavoriteRecipe(recipe: Recipe, result: (UiState<Pair<User,String>>?) -> Unit)
    fun addFavoriteRecipe(recipe: Recipe, result: (UiState<Pair<User,String>>?) -> Unit)
    fun getFavoritesRecipeClass(result: (UiState<ArrayList<Recipe>?>) -> Unit)
    fun getFavoritesRecipeString(result: (UiState<ArrayList<String>?>) -> Unit)


    //simple functions
    //auth
    fun logout(result: () -> Unit)

    //session
    fun storeSession(result: (User?) -> Unit)
    fun getSession(result: (User?) -> Unit)

    //metadata
    fun getMetadata(result: (HashMap<String,String>?) -> Unit)
    fun updateMetadata(key:String,value:String,result: (HashMap<String,String>?) -> Unit)
    fun removeMetadata(key:String,value:String,result: (HashMap<String,String>?) -> Unit)
    fun removeMetadata(result: () -> Unit)
}