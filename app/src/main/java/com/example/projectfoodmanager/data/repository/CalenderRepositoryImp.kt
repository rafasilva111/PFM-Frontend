package com.example.projectfoodmanager.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryListUpdate
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryPatchRequest
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryRequest
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderDatedEntryList
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntry
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntryList
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ListOfShoppingLists
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ShoppingList
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ShoppingListSimplefied

import com.example.projectfoodmanager.data.repository.datasource.RemoteDataSource
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.Helper.Companion.formatLocalTimeToServerTime
import com.example.projectfoodmanager.util.NetworkResult
import com.example.projectfoodmanager.util.SharedPreference
import retrofit2.Response
import java.time.LocalDateTime
import javax.inject.Inject

class CalenderRepositoryImp @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val sharedPreference: SharedPreference,
) : CalenderRepository {

    private val TAG:String = "AuthRepositoryImp"

    /**
     * Generic function to handle API requests and responses.
     *
     * @param liveData The LiveData where the result will be posted.
     * @param saveSharedPreferences Flag to determine if the result should be saved in SharedPreferences.
     * @param apiCall The API call to be executed.
     */
    private suspend fun <T> handleApiResponse(
        liveData: MutableLiveData<Event<NetworkResult<T>>>,
        saveSharedPreferences: Boolean = false,
        deleteSharedPreferences: Boolean = false,
        cleanseOldRegistry:Boolean =false,
        apiCall: suspend () -> Response<T>
    ) {
        try {
            // Post a loading state to indicate the request is in progress
            liveData.postValue(Event(NetworkResult.Loading()))
            Log.i(TAG, "Making API request.")

            // Invoke the API call
            val response = apiCall.invoke()

            if (response.isSuccessful) {
                // API request was successful
                val responseBody = response.body()
                if (responseBody != null) {

                    // Post a success result with the response body
                    liveData.postValue(Event(NetworkResult.Success(responseBody)))
                    // Optionally save data to SharedPreferences

                    if (saveSharedPreferences) {
                        when (responseBody) {
                            is CalenderEntry -> sharedPreference.saveCalendarEntry(responseBody)
                            is CalenderDatedEntryList -> sharedPreference.saveMultipleCalendarEntrys(responseBody,cleanseOldRegistry)
                            else -> Log.e(TAG, "Unable to save this type into shared preferences...")
                        }
                    }

                    if (deleteSharedPreferences) {
                        when (responseBody) {
                            is CalenderEntry -> sharedPreference.saveCalendarEntry(responseBody)
                            else -> Log.e(TAG, "Unable to save this type into shared preferences...")
                        }
                    }

                } else {
                    // Handle the case where the response body is null
                    liveData.postValue(Event(NetworkResult.Error("Response body is null")))
                }
            } else if (response.errorBody() != null) {
                // Handle the case where the API request was not successful and has an error body
                val errorObj = response.errorBody()!!.charStream().readText()
                Log.e(TAG, "API request was not successful. Error: \n$errorObj")
                liveData.postValue(Event(NetworkResult.Error(errorObj)))
            } else {
                // Handle the case where something went wrong without an error body
                liveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
            }
        } catch (e: Exception) {
            // Handle exceptions here if needed
            Log.e(TAG, "API request failed with exception: ${e.message}")
            // Post an error result for exceptions
            liveData.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }

    private val _functionCreateEntryOnCalender = MutableLiveData<Event<NetworkResult<CalenderEntry>>>()
    override val createEntryOnCalender: LiveData<Event<NetworkResult<CalenderEntry>>>
        get() = _functionCreateEntryOnCalender

    override suspend fun createEntryOnCalender(recipeId: Int,comment: CalenderEntryRequest) {
        handleApiResponse(
            _functionCreateEntryOnCalender,
            saveSharedPreferences = true
        ) {
            remoteDataSource.createCalenderEntry(recipeId,comment)
        }
    }

    private val _functionGetEntryOnCalender = MutableLiveData<Event<NetworkResult<CalenderEntryList>>>()
    override val getEntryOnCalendarLiveData: LiveData<Event<NetworkResult<CalenderEntryList>>>
        get() = _functionGetEntryOnCalender

    override suspend fun getEntryOnCalender(date: LocalDateTime) {

        handleApiResponse(
            _functionGetEntryOnCalender
        ) {
            remoteDataSource.getEntryOnCalender(formatLocalTimeToServerTime(date))
        }
    }

    private val _functionGetCalenderDatedEntryList = MutableLiveData<Event<NetworkResult<CalenderDatedEntryList>>>()
    override val getCalenderDatedEntryList: LiveData<Event<NetworkResult<CalenderDatedEntryList>>>
        get() = _functionGetCalenderDatedEntryList

    override suspend fun getCalenderDatedEntryList(fromDate: LocalDateTime, toDate: LocalDateTime,cleanseOldRegistry:Boolean) {

        handleApiResponse(
            _functionGetCalenderDatedEntryList,
            saveSharedPreferences = true,
            cleanseOldRegistry = cleanseOldRegistry
        ) {
            remoteDataSource.getEntryOnCalender(formatLocalTimeToServerTime(fromDate),formatLocalTimeToServerTime(toDate))
        }

    }


    private val _functionGetCalenderIngredients = MutableLiveData<Event<NetworkResult<ShoppingListSimplefied>>>()
    override val getCalendarIngredients: LiveData<Event<NetworkResult<ShoppingListSimplefied>>>
        get() = _functionGetCalenderIngredients

    override suspend fun getCalendarIngredients(fromDate: LocalDateTime, toDate: LocalDateTime) {

        handleApiResponse(
            _functionGetCalenderIngredients
        ) {
            remoteDataSource.getCalenderIngredients(formatLocalTimeToServerTime(fromDate),formatLocalTimeToServerTime(toDate))
        }
    }

    private val _functionDeleteCalenderEntry = MutableLiveData<Event<NetworkResult<Int>>>()
    override val deleteCalendarEntry: LiveData<Event<NetworkResult<Int>>>
        get() = _functionDeleteCalenderEntry

    override suspend fun deleteCalenderEntry(calenderEntry: CalenderEntry) {

        handleApiResponse(
            _functionDeleteCalenderEntry,
            deleteSharedPreferences = true
        ) {
            remoteDataSource.deleteCalenderEntry(calenderEntry.id)
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
    private val _checkCalenderEntries = MutableLiveData<Event<NetworkResult<Boolean>>>()
    override val checkCalenderEntries: LiveData<Event<NetworkResult<Boolean>>>
        get() = _checkCalenderEntries



    override suspend fun checkCalenderEntries(calenderEntryListUpdate: CalenderEntryListUpdate) {
        _checkCalenderEntries.postValue(Event(NetworkResult.Loading()))
        Log.i(TAG, "loginUser: making addLikeOnRecipe request.")

        val response =remoteDataSource.checkCalenderEntries(calenderEntryListUpdate)

        if (response.isSuccessful) {
            Log.i(TAG, "handleResponse: request made was sucessfull.")
            // todo update shared preferences
            sharedPreference.updateCalenderEntriesState(calenderEntryListUpdate)
            _checkCalenderEntries.postValue(Event(NetworkResult.Success(response.isSuccessful
            )))
        }
        else if(response.errorBody()!=null){
            val errorObj = response.errorBody()!!.charStream().readText()
            Log.i(TAG, "handleResponse: request made was sucessfull. \n$errorObj")
            _checkCalenderEntries.postValue(Event(NetworkResult.Error(errorObj)))
        }
        else{
            _checkCalenderEntries.postValue(Event(NetworkResult.Error("Something Went Wrong")))
        }
    }
}