package com.example.projectfoodmanager.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectfoodmanager.data.model.dtos.calender.CalenderEntryDTO
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryListUpdate
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryPatchRequest
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderDatedEntryList
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntry
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntryList
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ShoppingListSimplified
import com.example.projectfoodmanager.data.repository.CalenderRepository
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject


@HiltViewModel
class CalendarViewModel @Inject constructor(
    val repository: CalenderRepository
): ViewModel() {


    private val TAG:String ="CalendarViewModel"

    val createEntryOnCalendarLiveData: LiveData<Event<NetworkResult<CalenderEntry>>>
        get() = repository.createEntryOnCalender

    fun createEntryOnCalendar(recipeId: Int, calenderEntryRequest: CalenderEntryDTO){
        viewModelScope.launch {
            repository.createEntryOnCalender(recipeId,calenderEntryRequest)
        }
    }

    val getEntryOnCalendarLiveData: LiveData<Event<NetworkResult<CalenderEntryList>>>
        get() = repository.getEntryOnCalendarLiveData
    // cancel job if multiple times clicked
    var getEntryOnCalenderJob: Job? = null

    fun getEntryOnCalendar(date:LocalDateTime){
        getEntryOnCalenderJob?.cancel()
        getEntryOnCalenderJob = viewModelScope.launch {
            repository.getEntryOnCalender(date)
        }
    }

    val getCalendarDatedEntryListLiveData: LiveData<Event<NetworkResult<CalenderDatedEntryList>>>
        get() = repository.getCalenderDatedEntryList

    fun getCalendarDatedEntryList(fromDate:LocalDateTime, toDate: LocalDateTime, cleanseOldRegistry:Boolean = false){
        viewModelScope.launch {
            repository.getCalenderDatedEntryList(fromDate,toDate,cleanseOldRegistry)
        }
    }


    val getCalendarIngredientsLiveData: LiveData<Event<NetworkResult<ShoppingListSimplified>>>
        get() = repository.getCalendarIngredients

    fun getCalendarIngredients(fromDate: LocalDateTime, toDate: LocalDateTime) {
        viewModelScope.launch {
            repository.getCalendarIngredients(fromDate,toDate)
        }
    }


    val deleteCalendarEntryLiveData: LiveData<Event<NetworkResult<Int>>>
        get() = repository.deleteCalendarEntry


    fun deleteCalendarEntry(calenderEntry: CalenderEntry) {
        viewModelScope.launch {
            repository.deleteCalenderEntry(calenderEntry)
        }
    }


    val patchCalendarEntryLiveData: LiveData<Event<NetworkResult<CalenderEntry>>>
        get() = repository.patchCalendarEntry

    fun patchCalendarEntry(calenderEntryId: Int, calenderPatchRequest : CalenderEntryPatchRequest) {
        viewModelScope.launch {
            repository.patchCalenderEntry(calenderEntryId,calenderPatchRequest)
        }
    }

    val checkCalenderEntriesLiveData: LiveData<Event<NetworkResult<Boolean>>>
        get() = repository.checkCalenderEntries

    fun checkCalenderEntries(calenderEntryListUpdate: CalenderEntryListUpdate) {

        viewModelScope.launch {
            repository.checkCalenderEntries(calenderEntryListUpdate)
        }
    }


}