package com.example.projectfoodmanager.data.repository

import androidx.lifecycle.LiveData
import com.example.projectfoodmanager.data.model.modelRequest.CalenderEntryRequest
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderDatedEntryList
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntryList
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderIngredientList

import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult
import java.time.LocalDateTime

interface CalenderRepository {

    val createEntryOnCalender: LiveData<Event<NetworkResult<Boolean>>>
    val getEntryOnCalenderLiveData: LiveData<Event<NetworkResult<CalenderEntryList>>>
    val getCalenderDatedEntryList: LiveData<Event<NetworkResult<CalenderDatedEntryList>>>
    val getCalenderIngredients: LiveData<Event<NetworkResult<CalenderIngredientList>>>

    suspend fun createEntryOnCalender(recipeId: Int,comment: CalenderEntryRequest)
    suspend fun getEntryOnCalender(date: LocalDateTime)
    suspend fun getCalenderDatedEntryList(fromDate: LocalDateTime,toDate:LocalDateTime,cleanseOldRegistry: Boolean)
    suspend fun getCalenderIngredients(fromDate: LocalDateTime, toDate: LocalDateTime)
}