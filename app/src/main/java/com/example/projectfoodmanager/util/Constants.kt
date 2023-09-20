package com.example.projectfoodmanager.util



const val SPLASH_TIME:Long = 1000
const val LOGIN_TIME:Long = 2000

object Constants {
    // TODO: Primeira coisa a fazer quando testar
    //rui const val BASE_URL = "http://192.168.1.104:5000/"
    const val BASE_URL = "http://192.168.1.110:5000"
    //const val BASE_URL = "http://35.180.110.91/"
    const val USER_TOKEN = "user_token"

    const val IS_FIRST_APP_LAUNCH = "IS_FIRST_APP_LAUNCH"
    const val PREFS_TOKEN_FILE = "prefs_token_file"

}
object FIREBASE_NOTIFICATIONS {
    const val BASE_URL = "https://fcm.googleapis.com"
    const val SERVER_KEY = "AAAA9vy8Bpg:APA91bHyfumwp_y_Gnv3qVb5h19WTpM2EfZ_a6Un6CoIhJ6KCSO3nyx10ip2eg9i4lM0_K79krSoVp6PoiKk3SWUQP1LxnYZ9KYQcEM9Q_ofCxsfhf-ragQiC9U3fPBOaALxAKcZ5VLR"
    const val CONTENT_TYPE = "application/json"
}


object SharedPreferencesConstants{
    const val USER_SESSION = "user_session"
    const val USER_CALENDER_SESSION = "user_calender_session"
}

object ERROR_CODES {
    const val SESSION_INVALID = 404
    const val UNAUTORIZED = 403
    const val BAD_INPUTS = "401"
}

object CALENDAR_MEALS_TAG {
    const val PEQUENO_ALMOCO = "PEQUENO ALMOÇO"
    const val LANCHE_DA_MANHA = "LANCHE DA MANHÃ"
    const val ALMOCO = "ALMOÇO"
    const val LANCHE_DA_TARDE = "LANCHE DA TARDE"
    const val JANTAR = "JANTAR"
    const val CEIA = "CEIA"
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

object RecipesSortingType {
    const val DATE = "DATE"
    const val LIKES = "LIKES"
    const val SAVES = "SAVES"
    const val RANDOM = "RANDOM"
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

object FollowType{
    const val NOT_FOLLOWER = -1
    const val FOLLOWERS = 0
    const val FOLLOWEDS = 1
}


object ToastType{
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