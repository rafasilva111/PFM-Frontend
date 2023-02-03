package com.example.projectfoodmanager.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectfoodmanager.data.model.Recipe
import com.example.projectfoodmanager.data.model.User
import com.example.projectfoodmanager.data.model.modelRequest.UserRequest
import com.example.projectfoodmanager.data.old.AuthRepository_old
import com.example.projectfoodmanager.data.util.Resource
import com.example.projectfoodmanager.data.util.SharedPreference
import com.example.projectfoodmanager.domain.usecase.AuthUseCase
import com.example.projectfoodmanager.util.FireStoreCollection
import com.example.projectfoodmanager.util.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val repository: AuthRepository_old,
    private val authUseCase: AuthUseCase,
    val auth: FirebaseAuth,
    private val database: FirebaseFirestore,
    private val sharedPreference: SharedPreference
): ViewModel() {
    private val TAG:String ="AuthViewModel"
    val successful: MutableLiveData<Boolean?> = MutableLiveData()
    val error: MutableLiveData<String?> = MutableLiveData()

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

    fun getUserSession_v2(){
        authUseCase.getUser().onEach { result ->
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


    //old

    override fun getUserSession(result: (User?) -> Unit) {
        validateSessionUUID().let {
            if (it == null){
                result.invoke(null)
            }
            else{
                database.collection(FireStoreCollection.USER).document(it).get().addOnSuccessListener {
                    val user:User? = it.toObject(User::class.java)
                    val userInPreferences: User? = getUserInSharedPreferences()
                    if (user != null) {
                        if (user != userInPreferences){
                            storeUserInSharedPreferences(user)
                            val userTest = getUserInSharedPreferences()
                            result.invoke(user)
                        } else {
                            result.invoke(user)
                        }
                    } else{
                        result.invoke(null)
                    }

                }.addOnFailureListener {
                    result.invoke(null)
                    Log.d(TAG, "addFavoriteRecipe: "+it.toString())
                }
            }
        }

    }
    fun logout(result: () -> Unit){
        repository.logout(result)
    }

    fun updateUserSession(user: User,result: (UiState<String?>) -> Unit){
        _getUserSession.value  = UiState.Loading
        repository.updateUserInfo(user,result)
    }
    fun getSavedRecipesList(){
        _getFavoriteRecipeList.value = UiState.Loading
        repository.getFavoritesRecipe { _getFavoriteRecipeList.value = it}
    }

    fun getLikedRecipesList() {
        _getLikedRecipeList.value = UiState.Loading
        repository.getLikedRecipes{ _getLikedRecipeList.value = it}
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

    fun navigateToPage(){
        successful.postValue(null)
        error.postValue(null)
    }
    private fun validateSessionUUID(): String? {
        val userUUID = auth.currentUser?.uid
        return userUUID
    }

}