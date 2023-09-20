package com.example.projectfoodmanager.data.repository

import androidx.lifecycle.LiveData
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryPatchRequest
import com.example.projectfoodmanager.data.model.modelRequest.calender.CalenderEntryRequest
import com.example.projectfoodmanager.data.model.modelRequest.calender.shoppingList.ShoppingIngredientListRequest
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderDatedEntryList
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntry
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntryList
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ShoppingIngredientList
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ShoppingIngredientListList
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ShoppingIngredientListSimplefied

import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult
import java.time.LocalDateTime

interface ShoppingListRepository {

    val getShoppingListLiveData: LiveData<Event<NetworkResult<ShoppingIngredientList>>>
    val getShoppingListsLiveData: LiveData<Event<NetworkResult<ShoppingIngredientListList>>>

    val postShoppingListLiveData: LiveData<Event<NetworkResult<Unit>>>


    suspend fun getShoppingLists()
    suspend fun getShoppingList(shoppingListId : Int)

    suspend fun postShoppingList(shoppingIngredientListRequest: ShoppingIngredientListRequest)

}