package com.example.projectfoodmanager.util



val SPLASH_TIME:Long = 3000
val LOGIN_TIME:Long = 2000

object Constants {
    // TODO: Primeira coisa a fazer quando testar
    //rui const val BASE_URL = "http://192.168.1.104:5000/"
    const val BASE_URL = "http://192.168.1.110:5000"
    //const val BASE_URL = "http://35.180.118.91/"
    const val USER_TOKEN = "user_token"
    const val USER_SESSION = "user_session"
    const val IS_FIRST_APP_LAUNCH = "IS_FIRST_APP_LAUNCH"
    const val PREFS_TOKEN_FILE = "prefs_token_file"
}

object ERROR_CODES {
    const val SESSION_INVALID = 404
    const val UNAUTORIZED = 403
    const val BAD_INPUTS = "401"
}

object actionResultCodes{
    const val GALLERY_REQUEST_CODE = 1
}

object FireStorage{
    const val user_profile_images = "images/users/profile/"
}

object FireStoreCollection{
    const val USER = "user"
    const val RECIPE ="recipe"
    const val RECIPE_PROD="recipes_test"
}

object FireStorePaginations{
    const val RECIPE_LIMIT: Long = 5
}

object PaginationNumber{
    const val DEFAULT: Int = 5
    const val COMMENTS: Int = 20
}

object SharedPrefConstants {
    const val LOCAL_SHARED_PREF = "local_shared_pref"
    const val USER_SESSION = "user_session"
    const val FAVORITE_RECIPES_SESSION = "favorite_recipes_session"
    var METADATA = "metadata"
}

object SexConstants{
    const val M = "M"
    const val F = "F"
    const val NA = "NA"
}

object ImageTagsConstants{
    const val FOTO ="foto"
    const val DEFAULT ="default_img"
    const val SELECTED_AVATAR ="select_avatar"
    const val RANDOM_AVATAR ="random_avatar"
}

object ToastConstants{
    const val SUCCESS = 0
    const val ALERT = 1
    const val INFO = 2
    const val ERROR = -1
    const val VIP = 3
}
object RecipeDifficultyConstants{
    const val LOW ="Fácil"
    const val MEDIUM ="Moderada"
    const val HIGH ="Dificil"
}

object RecipeListingFragmentFilters {
    val CARNE ="carne"
    val PEIXE ="peixe"
    val SOPA ="sopa"
    val VEGETARIANA ="vegetariana"
    val FRUTA ="fruta"
    val BEBIDAS ="bebida"
    val SALADA ="salada"
    val PIZZA ="pizza"
    val SOBREMESA = "sobremesa"
    val SANDES = "sandes"
    val LANCHE = "lanche"
    val PEQUENO_ALMOCO = "pequeno-almoco"
    val JANTAR = "jantar"
    val ALMOCO = "almoco"
    val PETISCO = "petisco"

}

object NutritionTable{
    val ENERGIA = "energia"
    val ENERGIA_PERC = "energia_perc"
    val GORDURA = "gordura"
    val GORDURA_PERC = "gordura_perc"
    val GORDURA_SAT = "gordura_saturada"
    val GORDURA_SAT_PERC = "gordura_saturada_perc"
    //Erro "s"
    val HIDRATOS_CARBONO = "hidratos_carbonos"
    //Falta adicionar este campo na BD
    val HIDRATOS_CARBONO_PERC = "hidratos_carbono_perc"
    //Erro "s"
    val HIDRATOS_CARBONO_ACUCARES="hidratos_carbonos_acucares"
    val HIDRATOS_CARBONO_ACUCARES_PERC="hidratos_carbonos_acucares_perc"
    val FIBRA = "fibra"
    val FIBRA_PERC = "fibra_perc"
    val PROTEINA = "proteina"
    //Falta adicionar este campo na BD
    val PROTEINA_PERC = "proteina_perc"
}