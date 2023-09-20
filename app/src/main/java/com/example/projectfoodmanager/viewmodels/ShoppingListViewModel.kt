package com.example.projectfoodmanager.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectfoodmanager.data.model.modelRequest.calender.shoppingList.ShoppingIngredientListRequest
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ShoppingIngredientList
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ShoppingIngredientListList
import com.example.projectfoodmanager.data.repository.CalenderRepository
import com.example.projectfoodmanager.data.repository.ShoppingListRepository
import com.example.projectfoodmanager.util.Event
import com.example.projectfoodmanager.util.NetworkResult
import com.example.projectfoodmanager.util.SharedPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    val repository: ShoppingListRepository
): ViewModel() {

    // get

    val getShoppingLists: LiveData<Event<NetworkResult<ShoppingIngredientListList>>>
        get() = repository.getShoppingListsLiveData

    fun getShoppingLists() {
        viewModelScope.launch {
            repository.getShoppingLists()
        }
    }

    val getShoppingList: LiveData<Event<NetworkResult<ShoppingIngredientList>>>
        get() = repository.getShoppingListLiveData

    fun getShoppingList(shoppingListId : Int) {
        viewModelScope.launch {
            repository.getShoppingList(shoppingListId)
        }
    }
    // post

    val postShoppingListLiveData: LiveData<Event<NetworkResult<Unit>>>
        get() = repository.postShoppingListLiveData

    fun postShoppingList(shoppingIngredientListRequest: ShoppingIngredientListRequest) {
        viewModelScope.launch {
            repository.postShoppingList(shoppingIngredientListRequest)
        }
    }


}