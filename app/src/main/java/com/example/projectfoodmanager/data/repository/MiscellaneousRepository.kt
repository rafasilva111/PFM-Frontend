package com.example.projectfoodmanager.data.repository

import androidx.lifecycle.LiveData
import com.example.projectfoodmanager.data.model.modelResponse.miscellaneous.ApplicationReport
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult

interface MiscellaneousRepository {


    /** App reports */

    val postAppReportLiveData: LiveData<Event<NetworkResult<String>>>

    suspend fun postAppReport(applicationReport: ApplicationReport)
}