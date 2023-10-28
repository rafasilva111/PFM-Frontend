package com.example.projectfoodmanager.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryPatchRequest
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryRequest
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderDatedEntryList
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntry
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntryList
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ShoppingListSimplefied

import com.example.projectfoodmanager.data.repository.datasource.RemoteDataSource
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.Helper.Companion.formatLocalTimeToServerTime
import com.example.projectfoodmanager.util.NetworkResult
import com.example.projectfoodmanager.util.SharedPreference
import java.time.LocalDateTime
import javax.inject.Inject

class CalenderRepositoryImp @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val sharedPreference: SharedPreference,
) : CalenderRepository {

    private val TAG:String = "AuthRepositoryImp"

    private val _functionCreateEntryOnCalender = MutableLiveData<Event<NetworkResult<CalenderEntry>>>()
    override val createEntryOnCalender: LiveData<Event<NetworkResult<CalenderEntry>>>
        get() = _functionCreateEntryOnCalender

    override suspend fun createEntryOnCalender(recipeId: Int,comment: CalenderEntryRequest) {
        _functionCreateEntryOnCalender.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making addLikeOnRecipe request.")
        val response =remoteDataSource.createCalenderEntry(recipeId,comment)
        if (response.isSuccessful) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            sharedPreference.saveCalendarEntry(response.body()!!)
            _functionCreateEntryOnCalender.postValue(Event(NetworkResult.Success(response.body()!!)))
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

    private val _functionGetEntryOnCalender = MutableLiveData<Event<NetworkResult<CalenderEntryList>>>()
    override val getEntryOnCalendarLiveData: LiveData<Event<NetworkResult<CalenderEntryList>>>
        get() = _functionGetEntryOnCalender

    override suspend fun getEntryOnCalender(date: LocalDateTime) {
        _functionGetEntryOnCalender.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making addLikeOnRecipe request.")


        val response =remoteDataSource.getEntryOnCalender(formatLocalTimeToServerTime(date))
        if (response.isSuccessful) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            _functionGetEntryOnCalender.postValue(Event(NetworkResult.Success(response.body()!!
            )))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "handleResponse: request made was sucessfull. \n$errorObj")
            _functionGetEntryOnCalender.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _functionGetEntryOnCalender.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }

    private val _functionGetCalenderDatedEntryList = MutableLiveData<Event<NetworkResult<CalenderDatedEntryList>>>()
    override val getCalenderDatedEntryList: LiveData<Event<NetworkResult<CalenderDatedEntryList>>>
        get() = _functionGetCalenderDatedEntryList

    override suspend fun getCalenderDatedEntryList(fromDate: LocalDateTime, toDate: LocalDateTime,cleanseOldRegistry:Boolean) {
        _functionGetCalenderDatedEntryList.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making addLikeOnRecipe request.")
        val response =remoteDataSource.getEntryOnCalender(formatLocalTimeToServerTime(fromDate),formatLocalTimeToServerTime(toDate))
        if (response.isSuccessful) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            sharedPreference.saveMultipleCalendarEntrys(response.body()!!,cleanseOldRegistry)
            _functionGetCalenderDatedEntryList.postValue(Event(NetworkResult.Success(response.body()!!
            )))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "handleResponse: request made was sucessfull. \n$errorObj")
            _functionGetCalenderDatedEntryList.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _functionGetCalenderDatedEntryList.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }


    private val _functionGetCalenderIngredients = MutableLiveData<Event<NetworkResult<ShoppingListSimplefied>>>()
    override val getCalendarIngredients: LiveData<Event<NetworkResult<ShoppingListSimplefied>>>
        get() = _functionGetCalenderIngredients

    override suspend fun getCalendarIngredients(fromDate: LocalDateTime, toDate: LocalDateTime) {
        _functionGetCalenderIngredients.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making addLikeOnRecipe request.")
        val response =remoteDataSource.getCalenderIngredients(formatLocalTimeToServerTime(fromDate),formatLocalTimeToServerTime(toDate))
        if (response.isSuccessful) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            _functionGetCalenderIngredients.postValue(Event(NetworkResult.Success(response.body()!!
            )))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "handleResponse: request made was sucessfull. \n$errorObj")
            _functionGetCalenderIngredients.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _functionGetCalenderIngredients.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }

    private val _functionDeleteCalenderEntry = MutableLiveData<Event<NetworkResult<Int>>>()
    override val deleteCalendarEntry: LiveData<Event<NetworkResult<Int>>>
        get() = _functionDeleteCalenderEntry

    override suspend fun deleteCalenderEntry(calenderEntry: CalenderEntry) {
        _functionDeleteCalenderEntry.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making addLikeOnRecipe request.")
        val response =remoteDataSource.deleteCalenderEntry(calenderEntry.id)
        if (response.isSuccessful) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            sharedPreference.deleteCalendarEntry(calenderEntry)
            _functionDeleteCalenderEntry.postValue(Event(NetworkResult.Success(response.code())))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "handleResponse: request made was sucessfull. \n$errorObj")
            _functionDeleteCalenderEntry.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _functionDeleteCalenderEntry.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }

    private val _functionPatchCalenderEntry = MutableLiveData<Event<NetworkResult<CalenderEntry>>>()
    override val patchCalendarEntry: LiveData<Event<NetworkResult<CalenderEntry>>>
        get() = _functionPatchCalenderEntry


    override suspend fun patchCalenderEntry(calenderEntryId: Int, calenderPatchRequest : CalenderEntryPatchRequest) {
        _functionPatchCalenderEntry.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making addLikeOnRecipe request.")

        val response =remoteDataSource.patchCalenderEntry(calenderEntryId,calenderPatchRequest)

        if (response.isSuccessful) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            sharedPreference.updateCalendarEntry(response.body()!!)

            _functionPatchCalenderEntry.postValue(Event(NetworkResult.Success(response.body()!!
            )))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "handleResponse: request made was sucessfull. \n$errorObj")
            _functionPatchCalenderEntry.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _functionPatchCalenderEntry.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }


}