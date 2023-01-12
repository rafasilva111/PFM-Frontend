package com.example.projectfoodmanager.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.projectfoodmanager.data.model.Recipe
import com.example.projectfoodmanager.data.model.User
import com.example.projectfoodmanager.data.repository.AuthRepository
import com.example.projectfoodmanager.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val repository: AuthRepository
): ViewModel() {

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

    private val _getUserSession = MutableLiveData<UiState<User?>>()
    val getUserSession: LiveData<UiState<User?>>
        get() = _getUserSession

    private val _updateUserSession = MutableLiveData<UiState<User?>>()
    val updateUserSession: LiveData<UiState<User?>>
        get() = _updateUserSession

    private val _getMetadata = MutableLiveData<UiState<HashMap<String,String>?>>()
    val getMetadata: LiveData<UiState<HashMap<String,String>?>>
        get() = _getMetadata

    fun register(
        email: String,
        password: String,
        user: User
    ) {
        _register.value = UiState.Loading
        repository.registerUser(
            email = email,
            password = password,
            user = user
        ) { _register.value = it }
    }

    fun login(
        email: String,
        password: String
    ) {
        _login.value = UiState.Loading
        repository.loginUser(
            email,
            password
        ){
            _login.value = it
        }
    }
    fun getUserSession(result: (User?) -> Unit){
        _getUserSession.value  = UiState.Loading
        repository.getUserSession(result)
    }
    fun logout(result: () -> Unit){
        repository.logout(result)
    }

    fun updateUserSession(user: User,result: (UiState<String?>) -> Unit){
        _getUserSession.value  = UiState.Loading
        repository.updateUserInfo(user,result)
    }
    fun getFavoriteRecipeList(){
        _getFavoriteRecipeList.value = UiState.Loading
        repository.getFavoritesRecipe { _getFavoriteRecipeList.value = it}
    }

    fun addFavoriteRecipe(recipe: Recipe) {
        _updateFavoriteList.value = UiState.Loading
        repository.addFavoriteRecipe(recipe) { _updateFavoriteList.value = it}

    }

    fun removeFavoriteRecipe(recipe: Recipe) {
        _updateFavoriteList.value = UiState.Loading
        repository.removeFavoriteRecipe(recipe) { _updateFavoriteList.value = it}
    }

    fun storeMetadata(key:String , value:String,result: (HashMap<String,String>?) -> Unit) {
        _getMetadata.value  = UiState.Loading
        repository.updateMetadata(key,value,result)
    }

    fun getMetadata(result: (HashMap<String,String>?) -> Unit) {
        _getMetadata.value  = UiState.Loading
        repository.getMetadata(result)
    }

    fun removeLikeOnRecipe(recipe: Recipe) {
        _updateLikeList.value = UiState.Loading
        repository.removeLikeRecipe(recipe) { _updateLikeList.value = it}
    }

    fun addLikeOnRecipe(recipe: Recipe) {
        _updateLikeList.value = UiState.Loading
        repository.addLikeRecipe(recipe) { _updateLikeList.value = it}
    }

    fun getUserInSharedPreferences(result: (User?) -> Unit) {
        _getUserSession.value  = UiState.Loading
        repository.getUserInSharedPreferences(result)
    }

}