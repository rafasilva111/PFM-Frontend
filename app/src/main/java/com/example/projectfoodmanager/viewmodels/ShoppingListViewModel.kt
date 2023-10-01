package com.example.projectfoodmanager.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectfoodmanager.data.model.modelRequest.calender.shoppingList.ShoppingListRequest
import com.example.projectfoodmanager.data.model.modelResponse.IdResponse
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ShoppingList
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ListOfShoppingLists
import com.example.projectfoodmanager.data.repository.ShoppingListRepository
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    val repository: ShoppingListRepository
): ViewModel() {

    // get

    val getUserShoppingLists: LiveData<Event<NetworkResult<ListOfShoppingLists>>>
        get() = repository.getShoppingListsLiveData

    fun getUserShoppingLists() {
        viewModelScope.launch {
            repository.getUserShoppingLists()
        }
    }

    val getShoppingList: LiveData<Event<NetworkResult<ShoppingList>>>
        get() = repository.getShoppingListLiveData

    fun getShoppingList(shoppingListId : Int) {
        viewModelScope.launch {
            repository.getShoppingList(shoppingListId)
        }
    }
    // post

    val postShoppingListLiveData: LiveData<Event<NetworkResult<ShoppingList>>>
        get() = repository.postShoppingListLiveData

    fun postShoppingList(shoppingListRequest: ShoppingListRequest) {
        viewModelScope.launch {
            repository.postShoppingList(shoppingListRequest)
        }
    }

    // put

    val putShoppingListLiveData: LiveData<Event<NetworkResult<ShoppingList>>>
        get() = repository.putShoppingListLiveData

    fun putShoppingList(shoppingListId: Int, shoppingListRequest: ShoppingListRequest) {
        viewModelScope.launch {
            repository.putShoppingList(shoppingListId,shoppingListRequest)
        }
    }

    fun archiveShoppingList(shoppingListId: Int, shoppingListRequest: ShoppingListRequest) {
        viewModelScope.launch {
            repository.archiveShoppingList(shoppingListId,shoppingListRequest)
        }
    }

    // delete

    val deleteShoppingListLiveData: LiveData<Event<NetworkResult<IdResponse>>>
        get() = repository.deleteShoppingListLiveData

    fun deleteShoppingList(shoppingListId: Int) {
        viewModelScope.launch {
            repository.deleteShoppingList(shoppingListId)
        }
    }


}