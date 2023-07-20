package com.example.projectfoodmanager.data.repository

import androidx.lifecycle.LiveData
import com.example.projectfoodmanager.data.model.modelRequest.CalenderEntryRequest

import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult

interface CalenderRepository {

    val createEntryOnCalender: LiveData<Event<NetworkResult<Boolean>>>

    suspend fun createEntryOnCalender(recipeId: Int,comment: CalenderEntryRequest)
}