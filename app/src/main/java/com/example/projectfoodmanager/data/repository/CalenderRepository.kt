package com.example.projectfoodmanager.data.repository

import androidx.lifecycle.LiveData
import com.example.projectfoodmanager.data.model.modelRequest.CalenderEntryRequest
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderList

import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult
import java.time.LocalDateTime

interface CalenderRepository {

    val createEntryOnCalender: LiveData<Event<NetworkResult<Boolean>>>
    val getEntryOnCalenderLiveData: LiveData<Event<NetworkResult<CalenderList>>>

    suspend fun createEntryOnCalender(recipeId: Int,comment: CalenderEntryRequest)
    suspend fun getEntryOnCalender(date: LocalDateTime)
    suspend fun getEntryOnCalender(fromDate: LocalDateTime,toDate:LocalDateTime)
}