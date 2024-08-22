package com.example.projectfoodmanager.data.repository

import androidx.lifecycle.LiveData
import com.example.projectfoodmanager.data.model.modelRequest.calender.shoppingList.ShoppingListRequest
import com.example.projectfoodmanager.data.model.modelResponse.Id
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ShoppingList
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ListOfShoppingLists

import com.example.projectfoodmanager.util.network.Event
import com.example.projectfoodmanager.util.network.NetworkResult

interface ShoppingListRepository {

    // get
    val getShoppingListLiveData: LiveData<Event<NetworkResult<ShoppingList>>>
    val getShoppingListsLiveData: LiveData<Event<NetworkResult<ListOfShoppingLists>>>

    suspend fun getUserShoppingLists()
    suspend fun getShoppingList(shoppingListId : Int)

    // post
    val postShoppingListLiveData: LiveData<Event<NetworkResult<ShoppingList>>>

    suspend fun postShoppingList(shoppingListRequest: ShoppingListRequest)

    // put
    val putShoppingListLiveData: LiveData<Event<NetworkResult<ShoppingList>>>

    suspend fun putShoppingList(shoppingListId: Int, shoppingListRequest: ShoppingListRequest)

    suspend fun archiveShoppingList(shoppingListId: Int, shoppingListRequest: ShoppingListRequest)

    // delete
    val deleteShoppingListLiveData: LiveData<Event<NetworkResult<Id>>>

    suspend fun deleteShoppingList(shoppingListId: Int)
}