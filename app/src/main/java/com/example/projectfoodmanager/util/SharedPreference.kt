package com.example.projectfoodmanager.util

import android.content.SharedPreferences
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderDatedEntryList
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntry
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntryList
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
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
        return gson.fromJson(
            sharedPreferences.getString(SharedPreferencesConstants.USER_SESSION, ""),
            User::class.java
        )
    }

    fun saveUserSession(user: User) {
        sharedPreferences.edit().putString(SharedPreferencesConstants.USER_SESSION,gson.toJson(user)).apply()
    }

    fun deleteUserSession() {
        sharedPreferences.edit().remove(SharedPreferencesConstants.USER_SESSION).apply()
        sharedPreferences.edit().remove(SharedPreferencesConstants.USER_CALENDER_SESSION).apply()
    }

    fun addLikeToUserSession(recipe : Recipe): User{
        val user:User = gson.fromJson(sharedPreferences.getString(SharedPreferencesConstants.USER_SESSION,""), User::class.java)
        user.addLike(recipe)
        saveUserSession(user)
        return user
    }

    fun removeLikeFromUserSession(recipe : Recipe): User{
        val user:User = gson.fromJson(sharedPreferences.getString(SharedPreferencesConstants.USER_SESSION,""), User::class.java)
        user.removeLike(recipe)
        saveUserSession(user)
        return user
    }

    fun addSaveToUserSession(recipe: Recipe): User {
        val user:User = gson.fromJson(sharedPreferences.getString(SharedPreferencesConstants.USER_SESSION,""), User::class.java)
        user.addSave(recipe)
        saveUserSession(user)
        return user
    }

    fun removeSaveFromUserSession(recipe: Recipe): User {
        val user:User = gson.fromJson(sharedPreferences.getString(SharedPreferencesConstants.USER_SESSION,""), User::class.java)
        user.removeSave(recipe)
        saveUserSession(user)
        return user
    }

    fun updateUserSession(user: User) {
        saveUserSession(user)
    }

    fun getFullCalenderEntrys(): TreeMap<String,MutableList<CalenderEntry>> {

        val type = object : TypeToken<TreeMap<String, MutableList<CalenderEntry?>>>() {}.type

        try {
            return gson.fromJson(sharedPreferences.getString(SharedPreferencesConstants.USER_CALENDER_SESSION,""),type)
        }catch (e: Exception) {
            // we order the hashmap by the date
            val dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val dateComparator = compareByDescending<String> { LocalDate.parse(it, dateFormat) }
            return TreeMap<String,MutableList<CalenderEntry>>(dateComparator)
        }
    }

    // overrides whats written
    fun saveSingleCalenderEntry(date: LocalDateTime,calenderEntry: CalenderEntryList) {
        val fullCalenderEntryList = getFullCalenderEntrys()
        val dateString = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        if (dateString in fullCalenderEntryList)
            fullCalenderEntryList[dateString] = calenderEntry.result
        else{
            fullCalenderEntryList[dateString] = calenderEntry.result
            fullCalenderEntryList.comparator()
        }

        sharedPreferences.edit().putString(SharedPreferencesConstants.USER_CALENDER_SESSION,gson.toJson(fullCalenderEntryList)).apply()
    }

    fun saveMultipleCalenderEntrys(body: CalenderDatedEntryList,cleanseOldRegistry:Boolean = false) {
        val fullCalenderEntryList = getFullCalenderEntrys()

        if (cleanseOldRegistry){
            // cleanse older registry
            cleanseOldRegistry(fullCalenderEntryList)
        }

        for (key in body.result.keys){
            fullCalenderEntryList[key] = body.result[key]!!
        }
        fullCalenderEntryList.comparator()
        sharedPreferences.edit().putString(SharedPreferencesConstants.USER_CALENDER_SESSION,gson.toJson(fullCalenderEntryList)).apply()
    }

    private fun cleanseOldRegistry(fullCalenderEntryList: TreeMap<String,MutableList<CalenderEntry>>){
        val threeMonthsAgo  = LocalDate.now().minusMonths(3)
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        fullCalenderEntryList.entries.removeIf { entry ->
            val entryDate = LocalDate.parse(entry.key, formatter)
            entryDate.isBefore(threeMonthsAgo)
        }
    }



}