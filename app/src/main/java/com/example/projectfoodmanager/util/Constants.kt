package com.example.projectfoodmanager.util



val SPLASH_TIME:Long = 3000
val LOGIN_TIME:Long = 2000

object Constants {
    // TODO: PRIMEIRO PASSO MUDAR ESTA MERDA SEU ANIMAL
    //const val BASE_URL = "http://192.168.1.110:5000/"
    const val BASE_URL = "http://52.47.44.215/"
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