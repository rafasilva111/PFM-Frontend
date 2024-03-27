package com.example.projectfoodmanager.data.repository

import androidx.lifecycle.LiveData
import com.example.projectfoodmanager.data.model.dtos.user.goal.GoalDTO
import com.example.projectfoodmanager.data.model.user.goal.FitnessReport
import com.example.projectfoodmanager.data.model.user.goal.Goal
import com.example.projectfoodmanager.data.model.user.goal.IdealWeight
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult


interface GoalRepository {


    val getFitnessModelLiveData: LiveData<Event<NetworkResult<FitnessReport>>>
    val createFitnessGoalLiveData: LiveData<Event<NetworkResult<Goal>>>


    suspend fun getFitnessModel()
    suspend fun createFitnessGoal(goalDTO: GoalDTO)

}