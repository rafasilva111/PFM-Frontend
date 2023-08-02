package com.example.projectfoodmanager.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectfoodmanager.data.model.modelRequest.CalenderEntryRequest
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderDatedEntryList
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntryList
import com.example.projectfoodmanager.data.repository.CalenderRepository
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult
import com.example.projectfoodmanager.util.SharedPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject


@HiltViewModel
class CalenderViewModel @Inject constructor(
    val repository: CalenderRepository,
    val sharedPreference: SharedPreference
): ViewModel() {

    private val TAG:String ="CalendarViewModel"

    val createEntryOnCalenderLiveData: LiveData<Event<NetworkResult<Boolean>>>
        get() = repository.createEntryOnCalender

    fun createEntryOnCalender(recipeId: Int,calenderEntryRequest: CalenderEntryRequest){
        viewModelScope.launch {
            repository.createEntryOnCalender(recipeId,calenderEntryRequest)
        }
    }

    val getEntryOnCalenderLiveData: LiveData<Event<NetworkResult<CalenderEntryList>>>
        get() = repository.getEntryOnCalenderLiveData

    fun getEntryOnCalender(date:LocalDateTime){
        viewModelScope.launch {
            repository.getEntryOnCalender(date)
        }
    }

    val getCalenderDatedEntryList: LiveData<Event<NetworkResult<CalenderDatedEntryList>>>
        get() = repository.getCalenderDatedEntryList

    fun getCalenderDatedEntryList(fromDate:LocalDateTime,toDate: LocalDateTime,cleanseOldRegistry:Boolean = false){
        viewModelScope.launch {
            repository.getCalenderDatedEntryList(fromDate,toDate,cleanseOldRegistry)
        }
    }

    /*val getCalenderDatedEntryList: LiveData<Event<NetworkResult<CalenderDatedEntryList>>>
        get() = repository.getCalenderDatedEntryList

    fun getCalenderIngredients(fromDate: LocalDateTime, toDate: LocalDateTime) {
        viewModelScope.launch {
            repository.getCalenderIngredients(fromDate,toDate,cleanseOldRegistry)
        }
    }*/


}