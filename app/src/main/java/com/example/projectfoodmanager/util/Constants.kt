package com.example.projectfoodmanager.util




object Constants {
    const val BASE_URL = "http://192.168.1.110:5000/"
    const val USER_IS_LOGGED_IN = "USER_IS_LOGGED_IN"
    const val IS_FIRST_APP_LAUNCH = "IS_FIRST_APP_LAUNCH"
}

object ERROR_CODES {
    const val SESSION_INVALID = 404
    const val UNAUTORIZED = 403
    const val BAD_INPUTS = "401"
}

object FireStoreCollection{
    const val USER = "user"
    const val RECIPE ="recipe"
    const val RECIPE_PROD="recipes_test"
}

object FireStorePaginations{
    const val RECIPE_LIMIT: Long = 5
}

object SharedPrefConstants {
    const val LOCAL_SHARED_PREF = "local_shared_pref"
    const val USER_SESSION = "user_session"
    const val FAVORITE_RECIPES_SESSION = "favorite_recipes_session"
    var METADATA = "metadata"
}

object MetadataConstants{
    const val FIRST_TIME_LOGIN = "first_time_login"

}


object RecipeListingFragmentFilters {
    val CARNE ="carne"
    val PEIXE ="peixe"
    val SOPA ="sopa"
    val VEGETARIANO ="vegetariano"
    val FRUTA ="fruta"
    val BEBIDAS ="bebida"
}
