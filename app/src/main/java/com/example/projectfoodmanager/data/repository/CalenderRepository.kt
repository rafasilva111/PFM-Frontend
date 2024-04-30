package com.example.projectfoodmanager.data.repository

import androidx.lifecycle.LiveData
import com.example.projectfoodmanager.data.model.dtos.calender.CalenderEntryDTO
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryListUpdate
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryPatchRequest
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderDatedEntryList
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntry
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntryList
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ShoppingListSimplefied

import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult
import java.time.LocalDateTime

interface CalenderRepository {


    val createEntryOnCalender: LiveData<Event<NetworkResult<CalenderEntry>>>
    val getEntryOnCalendarLiveData: LiveData<Event<NetworkResult<CalenderEntryList>>>
    val getCalenderDatedEntryList: LiveData<Event<NetworkResult<CalenderDatedEntryList>>>
    val getCalendarIngredients: LiveData<Event<NetworkResult<ShoppingListSimplefied>>>
    val deleteCalendarEntry: LiveData<Event<NetworkResult<Int>>>
    val patchCalendarEntry: LiveData<Event<NetworkResult<CalenderEntry>>>
    val checkCalenderEntries: LiveData<Event<NetworkResult<Boolean>>>


    suspend fun createEntryOnCalender(recipeId: Int,comment: CalenderEntryDTO)
    suspend fun getEntryOnCalender(date: LocalDateTime)
    suspend fun getCalenderDatedEntryList(fromDate: LocalDateTime,toDate:LocalDateTime,cleanseOldRegistry: Boolean)
    suspend fun getCalendarIngredients(fromDate: LocalDateTime, toDate: LocalDateTime)
    suspend fun deleteCalenderEntry(calenderEntry: CalenderEntry)
    suspend fun patchCalenderEntry(calenderEntryId: Int, calenderPatchRequest : CalenderEntryPatchRequest)
    suspend fun checkCalenderEntries(calenderEntryListUpdate: CalenderEntryListUpdate)

}