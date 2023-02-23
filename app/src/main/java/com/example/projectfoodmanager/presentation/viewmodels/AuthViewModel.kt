package com.example.projectfoodmanager.presentation.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectfoodmanager.data.model.Recipe
import com.example.projectfoodmanager.data.model.User
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.old.AuthRepository_old
import com.example.projectfoodmanager.data.repository.AuthRepository
import com.example.projectfoodmanager.data.util.Network.isNetworkAvailable
import com.example.projectfoodmanager.data.util.Resource
import com.example.projectfoodmanager.data.util.SharedPreference
import com.example.projectfoodmanager.domain.usecase.AuthUseCase
import com.example.projectfoodmanager.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val application : Application,
    val authrepositoryOld: AuthRepository_old,
    val repository: AuthRepository,
    private val authUseCase: AuthUseCase,
    private val sharedPreference: SharedPreference
): ViewModel() {
    private val TAG:String ="AuthViewModel"
    val successful: MutableLiveData<Boolean?> = MutableLiveData()
    val logout: MutableLiveData<Boolean?> = MutableLiveData()
    val error: MutableLiveData<String?> = MutableLiveData()
    var user : MutableLiveData<Resource<User>> = MutableLiveData()


    private val _register = MutableLiveData<UiState<String>>()
    val register: LiveData<UiState<String>>
        get() = _register

    private val _login = MutableLiveData<UiState<String>>()
    val login: LiveData<UiState<String>>
        get() = _login

    private val _updateFavoriteList = MutableLiveData<UiState<Pair<User,String>>>()
    val updateFavoriteList: LiveData<UiState<Pair<User,String>>>
        get() = _updateFavoriteList

    private val _updateLikeList = MutableLiveData<UiState<Pair<User,String>>>()
    val updateLikeList: LiveData<UiState<Pair<User,String>>>
        get() = _updateLikeList

    private val _getFavoriteRecipeList = MutableLiveData<UiState<ArrayList<Recipe>>>()
    val getFavoriteRecipeList: LiveData<UiState<ArrayList<Recipe>>>
        get() = _getFavoriteRecipeList

    private val _getLikedRecipeList = MutableLiveData<UiState<ArrayList<Recipe>>>()
    val getLikedRecipeList: LiveData<UiState<ArrayList<Recipe>>>
        get() = _getLikedRecipeList

    private val _getUserSession = MutableLiveData<UiState<User?>>()
    val getUserSession: LiveData<UiState<User?>>
        get() = _getUserSession

    private val _updateUserSession = MutableLiveData<UiState<User?>>()
    val updateUserSession: LiveData<UiState<User?>>
        get() = _updateUserSession

    private val _getMetadata = MutableLiveData<UiState<HashMap<String,String>?>>()
    val getMetadata: LiveData<UiState<HashMap<String,String>?>>
        get() = _getMetadata

    fun registerUser(userRequest: UserRequest) {
        authUseCase.registerUser(userRequest = userRequest).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    Log.i("LoginViewModel", "I dey here, Loading")
                }
                is Resource.Error -> {
                    error.postValue("${result.message}")
                    successful.postValue(false)
                    Log.i("LoginViewModel", "I dey here, Error ${result.message}")

                }
                is Resource.Success -> {
                    successful.postValue(true)
                    Log.i("LoginViewModel", "I dey here, Success ${result.data}")
                }
            }
        }.launchIn(viewModelScope)
    }

    fun login(
        email: String,
        password: String
    ) {
        authUseCase.loginUser(email,password).onEach { result ->
            when (result) {
                is Resource.Loading -> {
                    Log.i("LoginViewModel", "I dey here, Loading")
                }
                is Resource.Error -> {
                    error.postValue("${result.message}")
                    successful.postValue(false)
                    Log.i("LoginViewModel", "I dey here, Error ${result.message}")

                }
                is Resource.Success -> {
                    successful.postValue(true)
                    Log.i("LoginViewModel", "I dey here, Success ${result.data}")
                }
            }
        }.launchIn(viewModelScope)
    }

    fun logout(result: () -> Unit){
        repository.logout(result)
    }

    fun getUserSession() = viewModelScope.launch(Dispatchers.IO){
        user.postValue(Resource.Loading())
        try {
            if (isNetworkAvailable(application)){
                val apiResult = authUseCase.getUserSession()
                user.postValue(apiResult)
            }else{
                user.postValue(Resource.Error(message = "Internet not available"))
            }
        }catch (e : Exception){
            user.postValue(Resource.Error(message = e.localizedMessage ?: "Unknown Error"))
        }
    }


    //old

    fun getUserSession_old(result: (User?) -> Unit){
        _getUserSession.value  = UiState.Loading
        authrepositoryOld.getUserSession(result)
    }



    fun updateUserSession(user: User,result: (UiState<String?>) -> Unit){
        _getUserSession.value  = UiState.Loading
        authrepositoryOld.updateUserInfo(user,result)
    }
    fun getSavedRecipesList(){
        _getFavoriteRecipeList.value = UiState.Loading
        authrepositoryOld.getFavoritesRecipe { _getFavoriteRecipeList.value = it}
    }

    fun getLikedRecipesList() {
        _getLikedRecipeList.value = UiState.Loading
        authrepositoryOld.getLikedRecipes{ _getLikedRecipeList.value = it}
    }

    fun addFavoriteRecipe(recipe: Recipe) {
        _updateFavoriteList.value = UiState.Loading
        authrepositoryOld.addFavoriteRecipe(recipe) { _updateFavoriteList.value = it}

    }

    fun removeFavoriteRecipe(recipe: Recipe) {
        _updateFavoriteList.value = UiState.Loading
        authrepositoryOld.removeFavoriteRecipe(recipe) { _updateFavoriteList.value = it}
    }

    fun storeMetadata(key:String , value:String,result: (HashMap<String,String>?) -> Unit) {
        _getMetadata.value  = UiState.Loading
        authrepositoryOld.updateMetadata(key,value,result)
    }

    fun getMetadata(result: (HashMap<String,String>?) -> Unit) {
        _getMetadata.value  = UiState.Loading
        authrepositoryOld.getMetadata(result)
    }

    fun removeLikeOnRecipe(recipe: Recipe) {
        _updateLikeList.value = UiState.Loading
        authrepositoryOld.removeLikeRecipe(recipe) { _updateLikeList.value = it}
    }

    fun addLikeOnRecipe(recipe: Recipe) {
        _updateLikeList.value = UiState.Loading
        authrepositoryOld.addLikeRecipe(recipe) { _updateLikeList.value = it}
    }
    fun getUserInSharedPreferences(result: (User?) -> Unit) {
        _getUserSession.value  = UiState.Loading
        authrepositoryOld.getUserInSharedPreferences(result)
    }

    //view variable management


    fun navigateToPage(){
        successful.postValue(null)
        error.postValue(null)
        logout.postValue(null)
    }

    fun refresh() {
        this.user = MutableLiveData()
    }

}