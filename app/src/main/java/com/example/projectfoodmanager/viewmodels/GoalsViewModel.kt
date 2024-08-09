package com.example.projectfoodmanager.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectfoodmanager.data.model.dtos.user.goal.GoalDTO
import com.example.projectfoodmanager.data.model.user.goal.FitnessReport
import com.example.projectfoodmanager.data.model.user.goal.Goal
import com.example.projectfoodmanager.data.repository.GoalRepository
import com.example.projectfoodmanager.util.network.Event
import com.example.projectfoodmanager.util.network.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GoalsViewModel @Inject constructor(
    val repository: GoalRepository
): ViewModel() {


    private val TAG:String ="GoalViewModel"

    val getFitnessModelLiveData: LiveData<Event<NetworkResult<FitnessReport>>>
        get() = repository.getFitnessModelLiveData

    fun getFitnessModel(){
        viewModelScope.launch {
            repository.getFitnessModel()
        }
    }


    val createFitnessGoalLiveData: LiveData<Event<NetworkResult<Goal>>>
        get() = repository.createFitnessGoalLiveData

    fun createFitnessGoal(goalDTO: GoalDTO){
        viewModelScope.launch {
            repository.createFitnessGoal(goalDTO)
        }
    }


}