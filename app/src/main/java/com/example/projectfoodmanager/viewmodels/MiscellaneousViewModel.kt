package com.example.projectfoodmanager.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectfoodmanager.data.model.modelResponse.miscellaneous.ApplicationReport
import com.example.projectfoodmanager.data.repository.MiscellaneousRepository
import com.example.projectfoodmanager.util.network.Event
import com.example.projectfoodmanager.util.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MiscellaneousViewModel @Inject constructor(
    private val repository: MiscellaneousRepository
) : ViewModel() {

    /** App reports */

    val postAppReportLiveData: LiveData<Event<NetworkResult<String>>>
        get() = repository.postAppReportLiveData

    fun postAppReport(applicationReport: ApplicationReport){
        viewModelScope.launch {
            repository.postAppReport(applicationReport)
        }
    }



}