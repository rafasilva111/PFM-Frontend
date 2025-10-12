package com.example.projectfoodmanager.data.repository

import androidx.lifecycle.LiveData
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryRequest
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryCheckListRequest
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderDatedEntryList
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntry
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntryList
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ShoppingListSimplified

import com.example.projectfoodmanager.util.network.Event
import com.example.projectfoodmanager.util.network.NetworkResult
import java.time.LocalDateTime

interface CalenderRepository {


    val createEntryOnCalender: LiveData<Event<NetworkResult<CalenderEntry>>>
    val getEntryOnCalendarLiveData: LiveData<Event<NetworkResult<CalenderEntryList>>>
    val getCalenderDatedEntryList: LiveData<Event<NetworkResult<CalenderDatedEntryList>>>
    val getCalendarIngredients: LiveData<Event<NetworkResult<ShoppingListSimplified>>>
    val deleteCalendarEntry: LiveData<Event<NetworkResult<Int>>>
    val patchCalendarEntry: LiveData<Event<NetworkResult<CalenderEntry>>>
    val checkCalenderEntries: LiveData<Event<NetworkResult<Boolean>>>


    suspend fun createEntryOnCalender(comment: CalenderEntryRequest)
    suspend fun getEntryOnCalender(date: LocalDateTime)
    suspend fun getCalenderDatedEntryList(fromDate: LocalDateTime,toDate:LocalDateTime,cleanseOldRegistry: Boolean)
    suspend fun getCalendarIngredients(fromDate: LocalDateTime, toDate: LocalDateTime)
    suspend fun deleteCalenderEntry(calenderEntry: CalenderEntry)
    suspend fun patchCalenderEntry(calenderEntryId: Int, calenderPatchRequest : CalenderEntryRequest)
    suspend fun checkCalenderEntries(calenderEntryCheckListRequest: CalenderEntryCheckListRequest)

}