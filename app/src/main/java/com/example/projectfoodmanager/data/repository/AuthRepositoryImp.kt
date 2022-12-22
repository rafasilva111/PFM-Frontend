package com.example.projectfoodmanager.data.repository

import android.content.SharedPreferences
import android.util.Log
import com.example.projectfoodmanager.data.model.Recipe
import com.example.projectfoodmanager.data.model.User
import com.example.projectfoodmanager.util.FireStoreCollection
import com.example.projectfoodmanager.util.MetadataConstants
import com.example.projectfoodmanager.util.SharedPrefConstants
import com.example.projectfoodmanager.util.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class AuthRepositoryImp(
    val auth: FirebaseAuth,
    val database: FirebaseFirestore,
    val appPreferences: SharedPreferences,
    val gson: Gson
) : AuthRepository {
    val TAG: String = "AuthRepositoryImp"
    override fun registerUser(
        email: String,
        password: String,
        user: User, result: (UiState<String>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                user.id = it.result.user?.uid ?: ""
                if (it.isSuccessful){
                    updateUserInfo(user) { state ->
                        when(state){
                            is UiState.Success -> {
                                storeSession() {
                                    if (it == null){
                                        result.invoke(UiState.Failure("User register successfully but session failed to store"))
                                    }else{
                                        result.invoke(
                                            UiState.Success("User register successfully!")
                                        )
                                    }
                                }
                            }
                            is UiState.Failure -> {
                                result.invoke(UiState.Failure(state.error))
                            }
                            else -> {}
                        }
                    }
                }else{
                    try {
                        throw it.exception ?: java.lang.Exception("Invalid authentication")
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        result.invoke(UiState.Failure("Authentication failed, Password should be at least 6 characters"))
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        result.invoke(UiState.Failure("Authentication failed, Invalid email entered"))
                    } catch (e: FirebaseAuthUserCollisionException) {
                        result.invoke(UiState.Failure("Authentication failed, Email already registered."))
                    } catch (e: Exception) {
                        result.invoke(UiState.Failure(e.message))
                    }
                }
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }


    override fun updateUserInfo(user: User, result: (UiState<String>) -> Unit) {
        val document = database.collection(FireStoreCollection.USER).document(user.id)
        document
            .set(user)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success("User has been update successfully")
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

    override fun loginUser(
        email: String,
        password: String,
        result: (UiState<String>) -> Unit) {
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storeSession(){
                        if (it == null){
                            result.invoke(UiState.Failure("Failed to store local session"))
                        }else{
                            result.invoke(UiState.Success("Login successfully!"))
                        }
                    }
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failure("Authentication failed, Check email and password"))
            }
    }

    override fun logout(result: () -> Unit) {
        auth.signOut()
        removeUserInSharedPreferences()
        result.invoke()
    }

    override fun forgotPassword(email: String, result: (UiState<String>) -> Unit) {
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        result.invoke(UiState.Success("Foi-lhe enviado um email para restauro."))

                    } else {
                        result.invoke(UiState.Failure(task.exception?.message))
                    }
                }.addOnFailureListener {
                    result.invoke(UiState.Failure("Autentificação falhou, email errado."))
                }
    }

    override fun storeSession( result: (User?) -> Unit) {
        var user:String? = validateSessionUUID()
        if (user!=null) {
            database.collection(FireStoreCollection.USER).document(user)
                    .get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val user = it.result.toObject(User::class.java)
                            appPreferences.edit().putString(SharedPrefConstants.USER_SESSION, gson.toJson(user)).apply()
                            result.invoke(user)
                        } else {
                            result.invoke(null)
                        }
                    }
                    .addOnFailureListener {
                        result.invoke(null)
                    }
        }
        else{
            result.invoke(null)
        }
    }

    override fun getSession(result: (User?) -> Unit) {
        var user:User? = validateSession()

        if (user == null){
            removeUserInSharedPreferences()
            result.invoke(null)
        }else{
            result.invoke(user)
        }
    }

    override fun removeFavoriteRecipe(
        recipe: Recipe,
        result: (UiState<Pair<User, String>>?) -> Unit
    ) {
        //save on profile reference
        var user:User? = validateSession() //check if user is user auth (pensar na segurança)


        if(user == null){
            removeUserInSharedPreferences()
            result.invoke(UiState.Failure("Sessão expirou."))
            return
        }

        user.removeFavoriteRecipe(recipe)
        storeUserInSharedPreferences(user)

        database.collection(FireStoreCollection.USER).document(user.id).set(user).addOnFailureListener {
            Log.d(TAG, "addFavoriteRecipe: "+it.toString())
        }



        //save recipe reference for future loading
        val recipe_str = getRecipesStringInSharedPreferences()
        if (recipe_str.isNullOrEmpty()){
            Log.d(TAG, "addFavoriteRecipe: nothing on shared preferences")
        }
        var recipes_list: MutableList<String> = ArrayList()
        if (recipe_str != null) {
            recipes_list.add(gson.toJson(recipe))
        }
        var stored_recipes = storeRecipesInSharedPreferences(recipes_list)

        Log.d(TAG, "Recipe has been added successfully "+stored_recipes)

        result.invoke(
            UiState.Success(Pair(user,"Recipe has been added successfully"))
        )

    }

    override fun addFavoriteRecipe(recipe: Recipe, result: (UiState<Pair<User, String>>?) -> Unit) {
        //save on profile reference
        var user:User? = validateSession() //check if user is user auth (pensar na segurança)

        if(user == null){
            removeUserInSharedPreferences()
            result.invoke(UiState.Failure("Sessão expirou."))
            return
        }

        user.addFavoriteRecipe(recipe)
        storeUserInSharedPreferences(user)

        database.collection(FireStoreCollection.USER).document(user.id).set(user).addOnFailureListener {
            Log.d(TAG, "addFavoriteRecipe: "+it.toString())
        }



        //save recipe reference for future loading
        val recipe_str = getRecipesStringInSharedPreferences()
        if (recipe_str.isNullOrEmpty()){
            Log.d(TAG, "addFavoriteRecipe: nothing on shared preferences")
        }
        var recipes_list: MutableList<String> = ArrayList()
        if (recipe_str != null) {
            recipes_list.add(gson.toJson(recipe))
        }
        var stored_recipes = storeRecipesInSharedPreferences(recipes_list)

        Log.d(TAG, "Recipe has been added successfully "+stored_recipes)

        result.invoke(
            UiState.Success(Pair(user,"Recipe has been added successfully"))
        )

    }


    override fun getFavoritesRecipeClass(result: (UiState<ArrayList<Recipe>?>) -> Unit) {
        val user: User? = validateSession()
        if (user != null) {
            val recipes = getRecipesClassInSharedPreferences()
            result.invoke(UiState.Success(recipes))
            return
        }
        removeUserInSharedPreferences()
        result.invoke(UiState.Failure("Sessão expirou."))
    }


    override fun getFavoritesRecipeString(result: (UiState<ArrayList<String>?>) -> Unit) {
        val user: User? = validateSession()
        if (user != null) {
            if (user.favorite_recipes != null){
                result.invoke(UiState.Success(user.favorite_recipes))
                return
            }
            result.invoke(UiState.Failure("Utilizador não tem receitas nos favoritos."))
            return
        }
        removeUserInSharedPreferences()
        result.invoke(UiState.Failure("Sessão expirou."))
    }


    override fun getUserSession(result: (UiState<User?>) -> Unit) {
        val user: User? = validateSession()
        if (user!=null){
            database.collection(FireStoreCollection.USER).document(user.id).get().addOnSuccessListener {
                val user:User? = it.toObject(User::class.java)
                if (user != null){
                    storeUserInSharedPreferences(user)
                    result.invoke(UiState.Success(user))
                }
                else{
                    result.invoke(UiState.Failure("Objeto impossivel de parcelar (parcelize)."))
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failure(it.toString()))
                Log.d(TAG, "addFavoriteRecipe: "+it.toString())
            }
        }
        else{
            removeUserInSharedPreferences()
            result.invoke(UiState.Failure("Sessão expirou."))
        }

    }

    override fun updateMetadata(key: String, value: String, result: (HashMap<String,String>?) -> Unit) {
        if (key != MetadataConstants.FIRST_TIME_LOGIN) {
            result.invoke(null)
        }

        var map_old = getMetadataFunction()
        if (map_old!=null){
            map_old.put(key, value)
            appPreferences.edit().putString(SharedPrefConstants.METADATA,gson.toJson(map_old)).apply()
            result.invoke(map_old)
        }
        else{
            var map:HashMap<String,String> = HashMap()
            map.put(key, value)
            appPreferences.edit().putString(SharedPrefConstants.METADATA,gson.toJson(map)).apply()
            result.invoke(map)
        }

    }

    override fun getMetadata(result: (HashMap<String,String>?) -> Unit){
        result.invoke(getMetadataFunction())
    }

    override fun removeMetadata(key: String, value: String, result: (HashMap<String,String>?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun removeMetadata(result: () -> Unit) {
        return appPreferences.edit().remove(SharedPrefConstants.METADATA).apply()
    }

    override fun removeLikeRecipe(recipe: Recipe, result: (UiState<Pair<User,String>>?) -> Unit) {
        //save on profile reference
        var user:User? = validateSession() //check if user is user auth (pensar na segurança)

        if(user == null){
            removeUserInSharedPreferences()
            result.invoke(UiState.Failure("Sessão expirou."))
            return
        }

        user.removeLikeRecipe(recipe)
        storeUserInSharedPreferences(user)


        database.collection(FireStoreCollection.USER).document(user.id).set(user).addOnFailureListener {
            Log.d(TAG, "addFavoriteRecipe: "+it.toString())
        }

        Log.d(TAG, "Recipe has been liked successfully.")

        result.invoke(
            UiState.Success(Pair(user,"Receita adicionada com sucesso!"))
        )
    }

    override fun addLikeRecipe(recipe: Recipe, result: (UiState<Pair<User,String>>?) -> Unit) {
        //save on profile reference
        var user:User? = validateSession() //check if user is user auth (pensar na segurança)

        if(user == null){
            removeUserInSharedPreferences()
            result.invoke(UiState.Failure("Sessão expirou."))
            return
        }

        user.addLikeRecipe(recipe)
        storeUserInSharedPreferences(user)

        database.collection(FireStoreCollection.USER).document(user.id).set(user).addOnFailureListener {
            Log.d(TAG, "addFavoriteRecipe: "+it.toString())
        }

        Log.d(TAG, "Recipe has been liked successfully.")

        result.invoke(
            UiState.Success(Pair(user,"Receita adicionada com sucesso!"))
        )
    }

    private fun getMetadataFunction(): HashMap<String,String>? {
        val serializedHashMap = appPreferences.getString(SharedPrefConstants.METADATA, null)
        return  gson.fromJson(serializedHashMap, HashMap::class.java) as HashMap<String, String>?
    }

    private fun validateSession(): User? {
        val userUUID = auth.currentUser?.uid ?: null
        val user =getUserInSharedPreferences()
        if (user != null) {
            if (user.id == userUUID){
                return user
            }
        }
        return null
    }

    private fun getRecipesClassInSharedPreferences(): ArrayList<Recipe>? {

        val recipes_string = getRecipesStringInSharedPreferences()
        if (recipes_string != null){
            var array = ArrayList<Recipe>()
            for(item in recipes_string){
                array.add(gson.fromJson(item,Recipe::class.java))
            }
            return array
        }
        return null
    }

    private fun getRecipesStringInSharedPreferences(): Array<String>? {

        return  gson.fromJson(appPreferences.getString(SharedPrefConstants.FAVORITE_RECIPES_SESSION,null), Array<String>::class.java)
    }

    private fun storeRecipesInSharedPreferences(recipe: MutableList<String>) {
        return appPreferences.edit().putString(SharedPrefConstants.FAVORITE_RECIPES_SESSION,gson.toJson(recipe)).apply()
    }

    private fun getUserInSharedPreferences(): User? {
        return  gson.fromJson(appPreferences.getString(SharedPrefConstants.USER_SESSION,null),User::class.java)
    }
    private fun getUserStringInSharedPreferences(): String? {
        return  appPreferences.getString(SharedPrefConstants.USER_SESSION,null)
    }

    private fun storeUserInSharedPreferences(user:User) {
        return appPreferences.edit().putString(SharedPrefConstants.USER_SESSION,gson.toJson(user)).apply()
    }

    private fun removeUserInSharedPreferences() {
        return appPreferences.edit().remove(SharedPrefConstants.USER_SESSION).apply()
    }

    private fun validateSessionUUID(): String? {
        val userUUID = auth.currentUser?.uid ?: null
        if (userUUID != null) {
            return userUUID
        }
        return null
    }
}