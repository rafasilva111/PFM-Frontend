package com.example.projectfoodmanager.util

import android.content.SharedPreferences
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryListUpdate
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderDatedEntryList
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntry
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ListOfShoppingLists
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ShoppingList
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
import kotlin.collections.ArrayList

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


    // Updating metadata

    // Get
    // Multiple
    fun getSharedPreferencesMetadata(): TreeMap<String,Boolean> {

        val type = object : TypeToken<TreeMap<String, Boolean>>() {}.type
        var sharedPreferencesMetadata = gson.fromJson(sharedPreferences.getString(SharedPreferencesConstants.SHARED_PREFENCES_METADATA,""),type) as TreeMap<String, Boolean>?
        if (sharedPreferencesMetadata == null)
            sharedPreferencesMetadata = TreeMap<String, Boolean>()
        return sharedPreferencesMetadata

    }
    // Single
    fun getSingleSharedPreferencesMetadata(sharedPreferencesMetadata: String): Boolean {

        return getSharedPreferencesMetadata()[sharedPreferencesMetadata]!!
    }

    //Save
    // Multiple
    fun saveSharedPreferencesMetadata(shoppingList: ShoppingList) {
        val allShoppingLists = getAllShoppingList()
        allShoppingLists.add(shoppingList)
        sharedPreferences.edit().putString(SharedPreferencesConstants.SHARED_PREFENCES_METADATA,gson.toJson(allShoppingLists)).apply()
    }
    // Single
    fun saveSingleSharedPreferencesMetadata(sharedPreferencesMetadata: String,state: Boolean) {

        val allSharedPreferencesMetadata = getSharedPreferencesMetadata()

        allSharedPreferencesMetadata[sharedPreferencesMetadata] = state

        sharedPreferences.edit().putString(SharedPreferencesConstants.SHARED_PREFENCES_METADATA,gson.toJson(allSharedPreferencesMetadata)).apply()
    }

        // Session

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
        saveSingleSharedPreferencesMetadata(SharedPreferencesMetadata.RECIPES_BACKGROUND,true)
        saveUserSession(user)
    }

    fun saveUserSession(user: User) {
        sharedPreferences.edit().putString(SharedPreferencesConstants.USER_SESSION,gson.toJson(user)).apply()
    }

    fun deleteUserSession() {
        sharedPreferences.edit().remove(SharedPreferencesConstants.SHARED_PREFENCES_METADATA).apply()
        sharedPreferences.edit().remove(SharedPreferencesConstants.USER_SESSION).apply()
        sharedPreferences.edit().remove(SharedPreferencesConstants.USER_SESSION_CALENDER).apply()
        sharedPreferences.edit().remove(SharedPreferencesConstants.USER_SESSION_SHOPPING_LISTS).apply()
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

    private fun getAllCalendarEntrys(): TreeMap<String,MutableList<CalenderEntry>> {

        val type = object : TypeToken<TreeMap<String, MutableList<CalenderEntry?>>>() {}.type

        try {
            return gson.fromJson(sharedPreferences.getString(SharedPreferencesConstants.USER_SESSION_CALENDER,""),type)
        }catch (e: Exception) {
            // we order the hashmap by the date
            val dateComparator = compareByDescending<String> { LocalDate.parse(it, formatter) }
            return TreeMap<String,MutableList<CalenderEntry>>(dateComparator)
        }
    }

    fun getEntryOnCalendar(atStartOfDay: LocalDateTime):MutableList<CalenderEntry>? {
        val type = object : TypeToken<TreeMap<String, MutableList<CalenderEntry?>>>() {}.type
        val formattedDate = atStartOfDay.format(formatter)

        val calender : TreeMap<String,MutableList<CalenderEntry>> = gson.fromJson(sharedPreferences.getString(SharedPreferencesConstants.USER_SESSION_CALENDER,""),type)

        return calender[formattedDate]
    }

    // Save

    fun saveCalendarEntry(calenderEntry: CalenderEntry) {
        val fullCalenderEntryList = getAllCalendarEntrys()
        val dateString = formatServerTimeToDateString(calenderEntry.realization_date)
        if (dateString !in fullCalenderEntryList){
            fullCalenderEntryList[dateString] = mutableListOf()
            fullCalenderEntryList.comparator()
        }
        fullCalenderEntryList[dateString]!!.add(calenderEntry)

        val pattern = DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss")
        fullCalenderEntryList[dateString]!!.sortBy { unit ->
            LocalDateTime.parse(unit.realization_date, pattern)
        }

        saveSingleSharedPreferencesMetadata(SharedPreferencesMetadata.CALENDER_ENTRYS,true)
        sharedPreferences.edit().putString(SharedPreferencesConstants.USER_SESSION_CALENDER,gson.toJson(fullCalenderEntryList)).apply()
    }

    // overrides whats written
    fun saveSingleCalendarDayEntry(date: LocalDateTime, calenderEntryList: MutableList<CalenderEntry>) {
        val fullCalenderEntryList = getAllCalendarEntrys()
        val dateString = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        if (dateString in fullCalenderEntryList)
            fullCalenderEntryList[dateString] = calenderEntryList
        else{
            fullCalenderEntryList[dateString] = calenderEntryList
            fullCalenderEntryList.comparator()
        }

        sharedPreferences.edit().putString(SharedPreferencesConstants.USER_SESSION_CALENDER,gson.toJson(fullCalenderEntryList)).apply()
    }

    // date must be dd/mm/yyyy
    private fun saveSingleCalendarDayEntry(dateString: String, calenderEntryList: MutableList<CalenderEntry>) {
        val fullCalenderEntryList = getAllCalendarEntrys()
        if (dateString in fullCalenderEntryList)
            fullCalenderEntryList[dateString] = calenderEntryList
        else{
            fullCalenderEntryList[dateString] = calenderEntryList
            fullCalenderEntryList.comparator()
        }

        sharedPreferences.edit().putString(SharedPreferencesConstants.USER_SESSION_CALENDER,gson.toJson(fullCalenderEntryList)).apply()
    }

    fun saveMultipleCalendarEntrys(body: CalenderDatedEntryList, cleanseOldRegistry:Boolean = false) {
        var fullCalenderEntryList = getAllCalendarEntrys()

        if (cleanseOldRegistry){
            // cleanse older registry
            fullCalenderEntryList = cleanseOldCalendarRegistry(fullCalenderEntryList)
        }

        for (key in body.result.keys){
            fullCalenderEntryList[key] = body.result[key]!!
        }
        fullCalenderEntryList.comparator()
        sharedPreferences.edit().putString(SharedPreferencesConstants.USER_SESSION_CALENDER,gson.toJson(fullCalenderEntryList)).apply()
    }

    // Update

    fun updateCalendarEntry(calenderEntry: CalenderEntry) {
        val fullCalenderEntryList = getAllCalendarEntrys()
        val dateString = calenderEntry.realization_date.split("T")[0]
        
        for (list in fullCalenderEntryList.values){
            list.removeIf {
                it.id == calenderEntry.id
            }
        }

        if (dateString !in fullCalenderEntryList)
            fullCalenderEntryList[dateString] = mutableListOf()

        fullCalenderEntryList[dateString]!!.add(calenderEntry)
        fullCalenderEntryList.comparator()


        sharedPreferences.edit().putString(SharedPreferencesConstants.USER_SESSION_CALENDER,gson.toJson(fullCalenderEntryList)).apply()
    }

    fun updateCalenderEntriesState(calenderEntryListUpdate: CalenderEntryListUpdate) {
        val fullCalenderEntryList = getAllCalendarEntrys()

        val calenderEntryList =calenderEntryListUpdate.calenderEntryStateList.map { it.id }


        for (list in fullCalenderEntryList.values)
            for (item in list)
                if (item.id in calenderEntryList )
                    item.checked_done  =calenderEntryListUpdate.calenderEntryStateList[calenderEntryList.indexOf(item.id)].state


        sharedPreferences.edit().putString(SharedPreferencesConstants.USER_SESSION_CALENDER,gson.toJson(fullCalenderEntryList)).apply()
    }

    // Delete

    private fun cleanseOldCalendarRegistry(fullCalenderEntryList: TreeMap<String,MutableList<CalenderEntry>>): TreeMap<String, MutableList<CalenderEntry>> {
        val threeMonthsAgo  = LocalDate.now().minusDays(Constants.MAX_CALENDER_DAYS)
        fullCalenderEntryList.entries.removeIf { entry ->
            val entryDate = LocalDate.parse(entry.key, formatter)
            entryDate.isBefore(threeMonthsAgo)
        }
        return fullCalenderEntryList
    }

    fun deleteCalendarEntry(calenderEntry: CalenderEntry) {
        val fullCalenderEntry = getAllCalendarEntrys()

        val key = formatServerTimeToDateString(calenderEntry.realization_date)

        val calenderDayEntrys = fullCalenderEntry[key]
        if (calenderDayEntrys != null){
            calenderDayEntrys.remove(calenderEntry)
            saveSingleCalendarDayEntry(key,calenderDayEntrys)
        }
    }

            // Shopping List

    // Get

    fun getAllShoppingList(): MutableList<ShoppingList> {
        val jsonString = sharedPreferences.getString(SharedPreferencesConstants.USER_SESSION_SHOPPING_LISTS, "")

        val typeToken = object : TypeToken<MutableList<ShoppingList>>() {}.type

        return gson.fromJson(jsonString, typeToken) ?: mutableListOf()
    }

    fun getShoppingList(){
        TODO("Not yet implemented")
    }

    // Save

    fun saveShoppingList(shoppingList: ShoppingList) {
        val allShoppingLists = getAllShoppingList()
        allShoppingLists.add(shoppingList)
        saveSingleSharedPreferencesMetadata(SharedPreferencesMetadata.SHOPPING_LIST,true)
        sharedPreferences.edit().putString(SharedPreferencesConstants.USER_SESSION_SHOPPING_LISTS,gson.toJson(allShoppingLists)).apply()
    }

    fun saveMultipleShoppingList(listOfShoppingLists: MutableList<ShoppingList>){
        saveSingleSharedPreferencesMetadata(SharedPreferencesMetadata.SHOPPING_LIST,true)
        sharedPreferences.edit().putString(SharedPreferencesConstants.USER_SESSION_SHOPPING_LISTS,gson.toJson(listOfShoppingLists)).apply()
    }

    // Delete

    fun deleteShoppingList(shoppingListId : Int){
        val allShoppingLists = getAllShoppingList()
        allShoppingLists.removeIf { it.id == shoppingListId }
        sharedPreferences.edit().putString(SharedPreferencesConstants.USER_SESSION_SHOPPING_LISTS,gson.toJson(allShoppingLists)).apply()
    }




}