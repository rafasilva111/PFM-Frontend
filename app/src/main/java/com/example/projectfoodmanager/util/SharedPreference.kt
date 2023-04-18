package com.example.projectfoodmanager.util

import android.content.SharedPreferences
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeResponse
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.google.gson.Gson
import javax.inject.Inject

class SharedPreference @Inject constructor(
    private val sharedPreferences : SharedPreferences,
    private val gson: Gson
) {

    fun isFirstAppLaunch(): Boolean {
        return sharedPreferences.getBoolean(Constants.IS_FIRST_APP_LAUNCH, true)
    }

    fun saveFirstAppLaunch(value: Boolean) {
        sharedPreferences.edit().putBoolean(Constants.IS_FIRST_APP_LAUNCH, value).apply()
    }

    fun getUserSession(): User {
         return gson.fromJson(sharedPreferences.getString(Constants.USER_SESSION,""), User::class.java)
    }

    fun saveUserSession(user: User) {
        sharedPreferences.edit().putString(Constants.USER_SESSION,gson.toJson(user)).apply()
    }

    fun deleteUserSession() {
        sharedPreferences.edit().remove(Constants.USER_SESSION).apply()
    }

    fun addLikeToUserSession(recipe : RecipeResponse){
        val user:User = gson.fromJson(sharedPreferences.getString(Constants.USER_SESSION,""), User::class.java)
        user.addLike(recipe)
        saveUserSession(user)
    }

    fun removeLikeFromUserSession(recipe : RecipeResponse){
        val user:User = gson.fromJson(sharedPreferences.getString(Constants.USER_SESSION,""), User::class.java)
        user.removeLike(recipe)
        saveUserSession(user)
    }

    fun addSaveToUserSession(recipe: RecipeResponse) {
        val user:User = gson.fromJson(sharedPreferences.getString(Constants.USER_SESSION,""), User::class.java)
        user.addSave(recipe)
        saveUserSession(user)
    }

    fun removeSaveFromUserSession(recipe: RecipeResponse) {
        val user:User = gson.fromJson(sharedPreferences.getString(Constants.USER_SESSION,""), User::class.java)
        user.removeSave(recipe)
        saveUserSession(user)
    }
}