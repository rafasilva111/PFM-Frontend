package com.example.projectfoodmanager.util



const val SPLASH_TIME:Long = 1000
const val LOGIN_TIME:Long = 2000



object Constants {
    // TODO: Primeira coisa a fazer quando testar
    //rui const val BASE_URL = "http://192.168.1.104:5000/"

    const val BASE_URL = "http://192.168.1.110:8000" // dev rafa
    //const val BASE_URL = "http://172.162.241.76" // prod


    const val PREFS_TOKEN_FILE = "prefs_token_file"
    const val MAX_CALENDER_DAYS =15L

}

object Session{
    const val REFRESH_TOKEN = "refresh_token"
    const val REFRESH_TOKEN_EXPIRES = "refresh_token_expires"
    const val ACCESS_TOKEN = "access_token"
    const val ACCESS_TOKEN_EXPIRES = "access_token_expires"
}

object SharedPreferencesMetadata{
    const val CALENDER_ENTRYS = "CALENDER_ENTRYS"
    const val SHOPPING_LIST = "SHOPPING_LIST"
    const val RECIPES_BACKGROUND = "RECIPES_BACKGROUND"
}

object SharedPreferencesConstants{

    const val IS_FIRST_APP_LAUNCH = "IS_FIRST_APP_LAUNCH"
    const val IS_FIRST_PORTION_ASK = "IS_FIRST_PORTION_ASK"

    const val METADATA = "shared_preferences_metadata"
    const val USER_SESSION = "user_session"
    const val USER_SESSION_BACKGROUND_RECIPES="shared_preferences_background_recipes"
    const val USER_SESSION_CALENDER = "user_session_calender"
    const val USER_SESSION_SHOPPING_LISTS = "user_session_shopping_list"
}

object CALENDAR_MEALS_TAG {
    const val PEQUENO_ALMOCO = "PEQUENO ALMOÇO"
    const val LANCHE_DA_MANHA = "LANCHE DA MANHÃ"
    const val ALMOCO = "ALMOÇO"
    const val LANCHE_DA_TARDE = "LANCHE DA TARDE"
    const val JANTAR = "JANTAR"
    const val CEIA = "CEIA"
}

object ActionResultCodes{
    const val GALLERY_REQUEST_CODE = 1
}

object FireStorage{
    const val user_profile_images = "images/users/profile/"
}

object PaginationNumber{
    const val DEFAULT: Int = 5
    const val COMMENTS: Int = 20
}

object SharedPrefConstants {
    const val LOCAL_SHARED_PREF = "local_shared_pref"
}

object RecipesSortingType {
    const val ALL = "ALL"
    const val DATE = "DATE"
    const val VERIFIED = "VERIFIED"
    const val SUGGESTION = "SUGESTION"
    const val PERSONALIZED_SUGGESTION = "SUGESTION"
    const val LIKES = "LIKES"
    const val SAVES = "SAVES"
    const val RANDOM = "RANDOM"
}

object UserType{
    const val NORMAL = "N"
    const val COMPANY = "C"
    const val VIP = "V"
    const val ADMIN = "A"

}

object SEX{
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
    const val CARNE ="carne"
    const val PEIXE ="peixe"
    const val SOPAS ="sopas"
    const val VEGETARIANA ="veg"
    const val FRUTA ="fruta"
    const val BEBIDAS ="bebida"
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

object FragmentRecipeLikesChipsTag{
    val LIKED =  1
    val SAVED =  2
    val CREATED =  3
    val COMMENTED =  4
    val LAST_SEEN =  5
}

object ProfileType{
    const val PUBLIC = "PUBLIC"
    const val PRIVATE = "PRIVATE"
}


object Constraints{
    const val USER_MIN_WEIGHT = 50F
    const val USER_MAX_WEIGHT = 200F
    const val USER_MIN_HEIGHT = 120F
    const val USER_MAX_HEIGHT = 200F
}

object Error{
    const val ON_WEIGHT = "weight"
    const val ON_HEIGHT = "height"
}

object FirebaseMessagingTopics{
    const val NOTIFICATION_USER_TOPIC_BASE = "User"
}

object FirebaseNotificationCode{
    const val FOLLOWED_USER = 1
    const val FOLLOW_REQUEST = 2
    const val FOLLOW_CREATED_RECIPE = 3
    const val HEALTH = 4
    const val COMMENT = 5
    const val SECURITY = 6
    const val SYSTEM = 7
    const val LIKE = 8
    const val RECIPE_CREATED = 9
    const val COMMENT_LIKED = 10
}

object FragmentsToOpen{
    const val FRAGMENT_COMMENTS = 1

}