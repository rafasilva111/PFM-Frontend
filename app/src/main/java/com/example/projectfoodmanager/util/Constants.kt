package com.example.projectfoodmanager.util




object Constants {
    const val BASE_URL = "http://192.168.1.110:5000/"
    const val USER_IS_LOGGED_IN = "USER_IS_LOGGED_IN"
    const val IS_FIRST_APP_LAUNCH = "IS_FIRST_APP_LAUNCH"
}

object FireStoreCollection{
    val USER = "user"
    val RECIPE ="recipe"
    val RECIPE_PROD="recipes_test"
}

object FireStorePaginations{
    val RECIPE_LIMIT: Long = 5
}

object SharedPrefConstants {
    val LOCAL_SHARED_PREF = "local_shared_pref"
    val USER_SESSION = "user_session"
    val FAVORITE_RECIPES_SESSION = "favorite_recipes_session"

    var METADATA = "metadata"
}

object MetadataConstants{
    val FIRST_TIME_LOGIN = "first_time_login"

}


object RecipeListingFragmentFilters {
    val CARNE ="carne"
    val PEIXE ="peixe"
    val SOPA ="sopa"
    val VEGETARIANO ="vegetariano"
    val FRUTA ="fruta"
    val BEBIDAS ="bebida"
}
