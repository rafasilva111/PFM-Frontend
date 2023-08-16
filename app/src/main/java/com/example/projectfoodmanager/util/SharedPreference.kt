package com.example.projectfoodmanager.util

import android.content.SharedPreferences
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderDatedEntryList
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntry
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.data.model.modelResponse.user.UserRecipeBackgrounds
import com.example.projectfoodmanager.util.Helper.Companion.formatServerTimeToDateString
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
    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
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

    private fun getFullCalenderEntrys(): TreeMap<String,MutableList<CalenderEntry>> {

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

    fun saveSingleCalenderEntry(calenderEntry: CalenderEntry) {
        val fullCalenderEntryList = getFullCalenderEntrys()
        val key = formatServerTimeToDateString(calenderEntry.realization_date)
        val calenderDayEntrys = fullCalenderEntryList[key]

        if (calenderDayEntrys != null) {
            if (calenderEntry !in calenderDayEntrys){
                calenderDayEntrys.add(calenderEntry)
                fullCalenderEntryList[key] = calenderDayEntrys
            }
        }

        sharedPreferences.edit().putString(SharedPreferencesConstants.USER_CALENDER_SESSION,gson.toJson(fullCalenderEntryList)).apply()
    }

    // overrides whats written
    fun saveSingleCalenderDayEntry(date: LocalDateTime, calenderEntryList: MutableList<CalenderEntry>) {
        val fullCalenderEntryList = getFullCalenderEntrys()
        val dateString = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        if (dateString in fullCalenderEntryList)
            fullCalenderEntryList[dateString] = calenderEntryList
        else{
            fullCalenderEntryList[dateString] = calenderEntryList
            fullCalenderEntryList.comparator()
        }

        sharedPreferences.edit().putString(SharedPreferencesConstants.USER_CALENDER_SESSION,gson.toJson(fullCalenderEntryList)).apply()
    }

    // date must be dd/mm/yyyy
    private fun saveSingleCalenderDayEntry(dateString: String, calenderEntryList: MutableList<CalenderEntry>) {
        val fullCalenderEntryList = getFullCalenderEntrys()
        if (dateString in fullCalenderEntryList)
            fullCalenderEntryList[dateString] = calenderEntryList
        else{
            fullCalenderEntryList[dateString] = calenderEntryList
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
        fullCalenderEntryList.entries.removeIf { entry ->
            val entryDate = LocalDate.parse(entry.key, formatter)
            entryDate.isBefore(threeMonthsAgo)
        }
    }

    fun saveUserRecipesSession(userRecipeBackgrounds: UserRecipeBackgrounds) {
        val user:User = gson.fromJson(sharedPreferences.getString(SharedPreferencesConstants.USER_SESSION,""), User::class.java)
        user.liked_recipes = userRecipeBackgrounds.result.recipes_liked
        user.saved_recipes = userRecipeBackgrounds.result.recipes_saved
        user.created_recipes = userRecipeBackgrounds.result.recipes_created
        saveUserSession(user)
    }

    fun deleteCalenderEntry(calenderEntry: CalenderEntry) {
        val fullCalenderEntry = getFullCalenderEntrys()

        val key = formatServerTimeToDateString(calenderEntry.realization_date)

        val calenderDayEntrys = fullCalenderEntry[key]
        if (calenderDayEntrys != null){
            calenderDayEntrys.remove(calenderEntry)
            saveSingleCalenderDayEntry(key,calenderDayEntrys)
        }
    }




}