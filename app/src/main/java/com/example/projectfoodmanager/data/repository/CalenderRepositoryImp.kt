package com.example.projectfoodmanager.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.projectfoodmanager.data.model.modelRequest.CalenderEntryRequest
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderList
import com.example.projectfoodmanager.data.model.modelResponse.comment.Comment

import com.example.projectfoodmanager.data.repository.datasource.RemoteDataSource
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.Helper.Companion.formatLocalTimeToServerTime
import com.example.projectfoodmanager.util.NetworkResult
import com.example.projectfoodmanager.util.SharedPreference
import com.google.gson.Gson
import java.time.LocalDateTime
import javax.inject.Inject

class CalenderRepositoryImp @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    sharedPreference: SharedPreference
) : CalenderRepository {

    private val TAG:String = "AuthRepositoryImp"

    private val _functionCreateEntryOnCalender = MutableLiveData<Event<NetworkResult<Boolean>>>()
    override val createEntryOnCalender: LiveData<Event<NetworkResult<Boolean>>>
        get() = _functionCreateEntryOnCalender

    override suspend fun createEntryOnCalender(recipeId: Int,comment: CalenderEntryRequest) {
        _functionCreateEntryOnCalender.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making addLikeOnRecipe request.")
        val response =remoteDataSource.createCalenderEntry(recipeId,comment)
        if (response.isSuccessful) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            _functionCreateEntryOnCalender.postValue(Event(NetworkResult.Success(response.isSuccessful
            )))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "handleResponse: request made was sucessfull. \n$errorObj")
            _functionCreateEntryOnCalender.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _functionCreateEntryOnCalender.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }

    private val _functionGetEntryOnCalender = MutableLiveData<Event<NetworkResult<CalenderList>>>()
    override val getEntryOnCalenderLiveData: LiveData<Event<NetworkResult<CalenderList>>>
        get() = _functionGetEntryOnCalender

    override suspend fun getEntryOnCalender(date: LocalDateTime) {
        _functionCreateEntryOnCalender.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making addLikeOnRecipe request.")


        val response =remoteDataSource.getEntryOnCalender(formatLocalTimeToServerTime(date))
        if (response.isSuccessful) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            // todo rafael add on shared preferences

            _functionCreateEntryOnCalender.postValue(Event(NetworkResult.Success(response.isSuccessful
            )))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "handleResponse: request made was sucessfull. \n$errorObj")
            _functionCreateEntryOnCalender.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _functionCreateEntryOnCalender.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }

    override suspend fun getEntryOnCalender(fromDate: LocalDateTime, toDate: LocalDateTime) {
        _functionCreateEntryOnCalender.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making addLikeOnRecipe request.")
        val response =remoteDataSource.getEntryOnCalender(formatLocalTimeToServerTime(fromDate),formatLocalTimeToServerTime(toDate))
        if (response.isSuccessful) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            // todo rafael add on shared preferences
            _functionCreateEntryOnCalender.postValue(Event(NetworkResult.Success(response.isSuccessful
            )))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "handleResponse: request made was sucessfull. \n$errorObj")
            _functionCreateEntryOnCalender.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _functionCreateEntryOnCalender.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }




}