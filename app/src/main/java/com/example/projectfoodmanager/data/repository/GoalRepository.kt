package com.example.projectfoodmanager.data.repository

import androidx.lifecycle.LiveData
import com.example.projectfoodmanager.data.model.user.goal.FitnessReport
import com.example.projectfoodmanager.data.model.user.goal.IdealWeight
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult


interface GoalRepository {

    /** Goal */


    /** Steps */
    val getFitnessModelLiveData: LiveData<Event<NetworkResult<FitnessReport>>>


    suspend fun getFitnessModel()

}