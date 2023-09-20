package com.example.projectfoodmanager.data.repository

import androidx.lifecycle.LiveData
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryPatchRequest
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryRequest
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderDatedEntryList
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntry
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntryList
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ShoppingIngredientListSimplefied

import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult
import java.time.LocalDateTime

interface CalenderRepository {


    val createEntryOnCalender: LiveData<Event<NetworkResult<CalenderEntry>>>
    val getEntryOnCalendarLiveData: LiveData<Event<NetworkResult<CalenderEntryList>>>
    val getCalenderDatedEntryList: LiveData<Event<NetworkResult<CalenderDatedEntryList>>>
    val getCalendarIngredients: LiveData<Event<NetworkResult<ShoppingIngredientListSimplefied>>>
    val deleteCalendarEntry: LiveData<Event<NetworkResult<Int>>>
    val patchCalendarEntry: LiveData<Event<NetworkResult<CalenderEntry>>>


    suspend fun createEntryOnCalender(recipeId: Int,comment: CalenderEntryRequest)
    suspend fun getEntryOnCalender(date: LocalDateTime)
    suspend fun getCalenderDatedEntryList(fromDate: LocalDateTime,toDate:LocalDateTime,cleanseOldRegistry: Boolean)
    suspend fun getCalendarIngredients(fromDate: LocalDateTime, toDate: LocalDateTime)
    suspend fun deleteCalenderEntry(calenderEntryId: CalenderEntry)
    suspend fun patchCalenderEntry(calenderEntryId: Int, calenderPatchRequest : CalenderEntryPatchRequest)

}