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

    fun saveUserRecipesSession(userRecipeBackgrounds: UserRecipeBackgrounds) {
        val user:User = gson.fromJson(sharedPreferences.getString(SharedPreferencesConstants.USER_SESSION,""), User::class.java)
        user.liked_recipes = userRecipeBackgrounds.result.recipes_liked
        user.saved_recipes = userRecipeBackgrounds.result.recipes_saved
        user.created_recipes = userRecipeBackgrounds.result.recipes_created
        saveUserSession(user)
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

            // calender entrys

    // Get

    private fun getFullCalendarEntrys(): TreeMap<String,MutableList<CalenderEntry>> {

        val type = object : TypeToken<TreeMap<String, MutableList<CalenderEntry?>>>() {}.type

        try {
            return gson.fromJson(sharedPreferences.getString(SharedPreferencesConstants.USER_CALENDER_SESSION,""),type)
        }catch (e: Exception) {
            // we order the hashmap by the date
            val dateComparator = compareByDescending<String> { LocalDate.parse(it, formatter) }
            return TreeMap<String,MutableList<CalenderEntry>>(dateComparator)
        }
    }

    fun getEntryOnCalendar(atStartOfDay: LocalDateTime):MutableList<CalenderEntry>? {
        val type = object : TypeToken<TreeMap<String, MutableList<CalenderEntry?>>>() {}.type
        val formattedDate = atStartOfDay.format(formatter)

        val calender : TreeMap<String,MutableList<CalenderEntry>> = gson.fromJson(sharedPreferences.getString(SharedPreferencesConstants.USER_CALENDER_SESSION,""),type)

        return calender[formattedDate]
    }

    // Save


    fun saveSingleCalendarEntry(calenderEntry: CalenderEntry) {
        val fullCalenderEntryList = getFullCalendarEntrys()
        val dateString = formatServerTimeToDateString(calenderEntry.created_date)
        if (dateString !in fullCalenderEntryList){
            fullCalenderEntryList[dateString] = mutableListOf()
            fullCalenderEntryList.comparator()
        }
        fullCalenderEntryList[dateString]!!.add(calenderEntry)

        val pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss")
        fullCalenderEntryList[dateString]!!.sortBy { unit ->
            LocalDateTime.parse(unit.created_date, pattern)
        }
        sharedPreferences.edit().putString(SharedPreferencesConstants.USER_CALENDER_SESSION,gson.toJson(fullCalenderEntryList)).apply()
    }


    // overrides whats written
    fun saveSingleCalendarDayEntry(date: LocalDateTime, calenderEntryList: MutableList<CalenderEntry>) {
        val fullCalenderEntryList = getFullCalendarEntrys()
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
    private fun saveSingleCalendarDayEntry(dateString: String, calenderEntryList: MutableList<CalenderEntry>) {
        val fullCalenderEntryList = getFullCalendarEntrys()
        if (dateString in fullCalenderEntryList)
            fullCalenderEntryList[dateString] = calenderEntryList
        else{
            fullCalenderEntryList[dateString] = calenderEntryList
            fullCalenderEntryList.comparator()
        }

        sharedPreferences.edit().putString(SharedPreferencesConstants.USER_CALENDER_SESSION,gson.toJson(fullCalenderEntryList)).apply()
    }


    fun saveMultipleCalendarEntrys(body: CalenderDatedEntryList, cleanseOldRegistry:Boolean = false) {
        val fullCalenderEntryList = getFullCalendarEntrys()

        if (cleanseOldRegistry){
            // cleanse older registry
            cleanseOldCalendarRegistry(fullCalenderEntryList)
        }

        for (key in body.result.keys){
            fullCalenderEntryList[key] = body.result[key]!!
        }
        fullCalenderEntryList.comparator()
        sharedPreferences.edit().putString(SharedPreferencesConstants.USER_CALENDER_SESSION,gson.toJson(fullCalenderEntryList)).apply()
    }

    // Delete

    private fun cleanseOldCalendarRegistry(fullCalenderEntryList: TreeMap<String,MutableList<CalenderEntry>>){
        val threeMonthsAgo  = LocalDate.now().minusMonths(3)
        fullCalenderEntryList.entries.removeIf { entry ->
            val entryDate = LocalDate.parse(entry.key, formatter)
            entryDate.isBefore(threeMonthsAgo)
        }
    }

    fun deleteCalendarEntry(calenderEntry: CalenderEntry) {
        val fullCalenderEntry = getFullCalendarEntrys()

        val key = formatServerTimeToDateString(calenderEntry.realization_date)

        val calenderDayEntrys = fullCalenderEntry[key]
        if (calenderDayEntrys != null){
            calenderDayEntrys.remove(calenderEntry)
            saveSingleCalendarDayEntry(key,calenderDayEntrys)
        }
    }




}