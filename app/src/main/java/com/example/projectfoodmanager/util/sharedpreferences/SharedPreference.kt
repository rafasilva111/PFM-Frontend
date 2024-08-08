package com.example.projectfoodmanager.util.sharedpreferences

import android.content.SharedPreferences
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryCheckListRequest
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderDatedEntryList
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntry
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ShoppingList
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.data.model.modelResponse.user.recipeBackground.UserRecipesBackground
import com.example.projectfoodmanager.data.model.user.goal.Goal
import com.example.projectfoodmanager.util.Constants
import com.example.projectfoodmanager.util.Helper.Companion.formatServerTimeToDateString
import com.example.projectfoodmanager.util.Helper.Companion.formatServerTimeToLocalDateTime
import com.example.projectfoodmanager.util.SharedPreferencesConstants.IS_FIRST_APP_LAUNCH
import com.example.projectfoodmanager.util.SharedPreferencesConstants.IS_FIRST_PORTION_ASK
import com.example.projectfoodmanager.util.SharedPreferencesConstants.METADATA
import com.example.projectfoodmanager.util.SharedPreferencesConstants.USER_SESSION
import com.example.projectfoodmanager.util.SharedPreferencesConstants.USER_SESSION_BACKGROUND_RECIPES
import com.example.projectfoodmanager.util.SharedPreferencesConstants.USER_SESSION_CALENDER
import com.example.projectfoodmanager.util.SharedPreferencesConstants.USER_SESSION_SHOPPING_LISTS
import com.example.projectfoodmanager.util.SharedPreferencesMetadata
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



    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun isFirstAppLaunch(): Boolean {
        return sharedPreferences.getBoolean(IS_FIRST_APP_LAUNCH, true)
    }

    fun saveFirstAppLaunch() {
        sharedPreferences.edit().putBoolean(IS_FIRST_APP_LAUNCH,false).apply()
    }


    fun isFirstPortionAsk(): Boolean {
        return sharedPreferences.getBoolean(IS_FIRST_PORTION_ASK, true)
    }

    fun saveFirstPortionAsk() {
        sharedPreferences.edit().putBoolean(IS_FIRST_PORTION_ASK,false).apply()

    }


    /**
     *  Metadata
     * */

    /**     Get
     * */

    /** Multiple */
    fun getSharedPreferencesMetadata(): TreeMap<String,Boolean> {

        val type = object : TypeToken<TreeMap<String, Boolean>>() {}.type
        var sharedPreferencesMetadata = gson.fromJson(sharedPreferences.getString(METADATA,""),type) as TreeMap<String, Boolean>?
        if (sharedPreferencesMetadata == null)
            sharedPreferencesMetadata = TreeMap<String, Boolean>()
        return sharedPreferencesMetadata

    }
    /** Single */
    fun getSingleSharedPreferencesMetadata(sharedPreferencesMetadata: String): Boolean {

        return getSharedPreferencesMetadata()[sharedPreferencesMetadata]!!
    }

    /**     Save
     * */

    /** Multiple */
    fun saveMultipleMetadata(shoppingList: ShoppingList) {
        val allShoppingLists = getAllShoppingList()
        allShoppingLists.add(shoppingList)
        sharedPreferences.edit().putString(METADATA,gson.toJson(allShoppingLists)).apply()
    }
    /** Single */
    private fun saveSingleMetadata(sharedPreferencesMetadata: String, state: Boolean = true) {

        val allSharedPreferencesMetadata = getSharedPreferencesMetadata()

        allSharedPreferencesMetadata[sharedPreferencesMetadata] = state

        sharedPreferences.edit().putString(METADATA,gson.toJson(allSharedPreferencesMetadata)).apply()
    }

    /**     Delete
     * */

    fun deleteSession() {
        sharedPreferences.edit().remove(METADATA).apply()
        sharedPreferences.edit().remove(USER_SESSION).apply()
        sharedPreferences.edit().remove(USER_SESSION_BACKGROUND_RECIPES).apply()
        sharedPreferences.edit().remove(USER_SESSION_CALENDER).apply()
        sharedPreferences.edit().remove(USER_SESSION_SHOPPING_LISTS).apply()
    }




    /**
     *  User
     * */


    /**     Get
     * */

    fun getUserSession(): User {
        return gson.fromJson(
            sharedPreferences.getString(USER_SESSION, ""),
            User::class.java
        )
    }

    /**     Save
     * */


    fun saveUserSession(user: User) {
         sharedPreferences.edit().putString(USER_SESSION,gson.toJson(user)).apply()
    }





    /**
     *  Recipes Background
     * */


    /**     Get
     * */

    fun getUserRecipesBackground(): UserRecipesBackground {
        return gson.fromJson(
            sharedPreferences.getString(USER_SESSION_BACKGROUND_RECIPES, ""),
            UserRecipesBackground::class.java
        )
    }

    fun getUserRecipesBackgroundSavedRecipes(): MutableList<Recipe> {
        return gson.fromJson(
            sharedPreferences.getString(USER_SESSION_BACKGROUND_RECIPES, ""),
            UserRecipesBackground::class.java
        ).savedRecipes
    }

    fun getUserRecipesBackgroundCreatedRecipes(): MutableList<Recipe> {
        return gson.fromJson(
            sharedPreferences.getString(USER_SESSION_BACKGROUND_RECIPES, ""),
            UserRecipesBackground::class.java
        ).createdRecipes
    }

    /**     Save
     * */

    fun saveUserRecipesBackground(userRecipesBackground: UserRecipesBackground) {
        sharedPreferences.edit().putString(USER_SESSION_BACKGROUND_RECIPES,gson.toJson(userRecipesBackground)).apply()
        saveSingleMetadata(USER_SESSION_BACKGROUND_RECIPES,true)
    }



    fun addSavedRecipe(recipe: Recipe){
        val userRecipesBackground = getUserRecipesBackground()
        userRecipesBackground.savedRecipes.add(recipe)
        saveUserRecipesBackground(userRecipesBackground)
    }

    fun removeSavedRecipe(recipe: Recipe) {
        val userRecipesBackground = getUserRecipesBackground()
        userRecipesBackground.savedRecipes.remove(recipe)
        saveUserRecipesBackground(userRecipesBackground)
    }

    fun addLikeToSavedRecipes(recipe: Recipe) {
        val userRecipesBackground = getUserRecipesBackground()

        for (item in userRecipesBackground.savedRecipes)
            if (item.id == recipe.id){
                item.liked = true
                item.likes += 1
                break
            }

        saveUserRecipesBackground(userRecipesBackground)
    }

    fun removeLikeFromSavedRecipes(recipe: Recipe) {
        val userRecipesBackground = getUserRecipesBackground()

        for (item in userRecipesBackground.savedRecipes)
            if (item.id == recipe.id){
                item.liked = false
                item.likes -= 1
                break
            }

        saveUserRecipesBackground(userRecipesBackground)
    }


    /**
     *  Calender Entrys
     * */

    /**
     * Get
     * */

    private fun getAllCalendarEntries(): TreeMap<String,MutableList<CalenderEntry>> {

        val type = object : TypeToken<TreeMap<String, MutableList<CalenderEntry?>>>() {}.type

        return try {
            gson.fromJson(sharedPreferences.getString(USER_SESSION_CALENDER,""),type)
        }catch (e: Exception) {
            // we order the hashmap by the date
            val dateComparator = compareByDescending<String> { LocalDate.parse(it, formatter) }
            TreeMap<String,MutableList<CalenderEntry>>(dateComparator)
        }
    }

    fun getEntryOnCalendar(atStartOfDay: LocalDateTime):MutableList<CalenderEntry>? {
        val formattedDate = atStartOfDay.format(formatter)

        val calender : TreeMap<String,MutableList<CalenderEntry>> = getAllCalendarEntries()

        return calender[formattedDate]
    }

    /**
     * Save
     * */

    fun saveCalendarEntry(calenderEntry: CalenderEntry) {
        val fullCalenderEntryList = getAllCalendarEntries()
        val dateString = formatServerTimeToDateString(calenderEntry.realizationDate)
        if (dateString !in fullCalenderEntryList){
            fullCalenderEntryList[dateString] = mutableListOf()
            fullCalenderEntryList.comparator()
        }
        fullCalenderEntryList[dateString]!!.add(calenderEntry)

        fullCalenderEntryList[dateString]!!.sortBy { recipe ->
            formatServerTimeToLocalDateTime(recipe.realizationDate)
        }

        saveSingleMetadata(SharedPreferencesMetadata.CALENDER_ENTRIES,true)
        sharedPreferences.edit().putString(USER_SESSION_CALENDER,gson.toJson(fullCalenderEntryList)).apply()
    }

    // overrides whats written
    fun saveSingleCalendarDayEntry(date: LocalDateTime, calenderEntryList: MutableList<CalenderEntry>) {
        val fullCalenderEntryList = getAllCalendarEntries()
        val dateString = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        if (dateString in fullCalenderEntryList)
            fullCalenderEntryList[dateString] = calenderEntryList
        else{
            fullCalenderEntryList[dateString] = calenderEntryList
            fullCalenderEntryList.comparator()
        }

        sharedPreferences.edit().putString(USER_SESSION_CALENDER,gson.toJson(fullCalenderEntryList)).apply()
    }

    // date must be dd/mm/yyyy
    private fun saveSingleCalendarDayEntry(dateString: String, calenderEntryList: MutableList<CalenderEntry>) {
        val fullCalenderEntryList = getAllCalendarEntries()
        if (dateString in fullCalenderEntryList)
            fullCalenderEntryList[dateString] = calenderEntryList
        else{
            fullCalenderEntryList[dateString] = calenderEntryList
            fullCalenderEntryList.comparator()
        }

        sharedPreferences.edit().putString(USER_SESSION_CALENDER,gson.toJson(fullCalenderEntryList)).apply()
    }

    fun saveMultipleCalendarEntrys(body: CalenderDatedEntryList, cleanseOldRegistry:Boolean = false) {
        var fullCalenderEntryList = getAllCalendarEntries()

        if (cleanseOldRegistry){
            // cleanse older registry
            fullCalenderEntryList = cleanseOldCalendarRegistry(fullCalenderEntryList)
        }

        for (key in body.result.keys){
            fullCalenderEntryList[key] = body.result[key]!!
        }
        fullCalenderEntryList.comparator()
        sharedPreferences.edit().putString(USER_SESSION_CALENDER,gson.toJson(fullCalenderEntryList)).apply()
    }

    // Update

    fun updateCalendarEntry(calenderEntry: CalenderEntry) {
        val fullCalenderEntryList = getAllCalendarEntries()
        val dateString = calenderEntry.realizationDate.split("T")[0]
        
        for (list in fullCalenderEntryList.values){
            list.removeIf {
                it.id == calenderEntry.id
            }
        }

        if (dateString !in fullCalenderEntryList)
            fullCalenderEntryList[dateString] = mutableListOf()

        fullCalenderEntryList[dateString]!!.add(calenderEntry)
        fullCalenderEntryList.comparator()


        sharedPreferences.edit().putString(USER_SESSION_CALENDER,gson.toJson(fullCalenderEntryList)).apply()
    }

    fun updateCalenderEntriesState(calenderEntryCheckListRequest: CalenderEntryCheckListRequest) {
        val fullCalenderEntryList = getAllCalendarEntries()



        for (list in fullCalenderEntryList.values)
            for (item in list){
                if (item.id in calenderEntryCheckListRequest.checked )
                    item.checkedDone  = true
                if (item.id in calenderEntryCheckListRequest.unchecked )
                    item.checkedDone  = false
            }



        sharedPreferences.edit().putString(USER_SESSION_CALENDER,gson.toJson(fullCalenderEntryList)).apply()
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
        val fullCalenderEntry = getAllCalendarEntries()

        val key = formatServerTimeToDateString(calenderEntry.realizationDate)

        val calenderDayEntrys = fullCalenderEntry[key]
        if (calenderDayEntrys != null){
            calenderDayEntrys.remove(calenderEntry)
            saveSingleCalendarDayEntry(key,calenderDayEntrys)
        }
    }

    /**
     *  Shopping List
     * */

    // Get

    fun getAllShoppingList(): MutableList<ShoppingList> {
        val jsonString = sharedPreferences.getString(USER_SESSION_SHOPPING_LISTS, "")

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
        saveSingleMetadata(SharedPreferencesMetadata.SHOPPING_LIST,true)
        sharedPreferences.edit().putString(USER_SESSION_SHOPPING_LISTS,gson.toJson(allShoppingLists)).apply()
    }

    fun saveMultipleShoppingList(listOfShoppingLists: MutableList<ShoppingList>){
        saveSingleMetadata(SharedPreferencesMetadata.SHOPPING_LIST,true)
        sharedPreferences.edit().putString(USER_SESSION_SHOPPING_LISTS,gson.toJson(listOfShoppingLists)).apply()
    }

    // Delete

    fun deleteShoppingList(shoppingListId : Int){
        val allShoppingLists = getAllShoppingList()
        allShoppingLists.removeIf { it.id == shoppingListId }
        sharedPreferences.edit().putString(USER_SESSION_SHOPPING_LISTS,gson.toJson(allShoppingLists)).apply()
    }


    /**
     *  Fitness Goal
     * */

    fun saveFitnessGoal(goal: Goal) {
        val user = getUserSession()
        user.fitnessGoal = goal
        saveUserSession(user)
    }

}