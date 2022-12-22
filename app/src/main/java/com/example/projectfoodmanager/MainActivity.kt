package com.example.projectfoodmanager

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.projectfoodmanager.databinding.ActivityMainMenuBinding
import com.example.projectfoodmanager.ui.auth.LoginActivity
import com.example.projectfoodmanager.ui.auth.AuthViewModel
import com.example.projectfoodmanager.ui.profile.ProfileFragment
import com.example.projectfoodmanager.ui.recipe.Favorites.FavoritesFragment
import com.example.projectfoodmanager.ui.recipe.RecipeListingFragment
import com.example.projectfoodmanager.util.UiState
import com.example.projectfoodmanager.util.toast
import com.google.android.material.bottomnavigation.BottomNavigationView


import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainMenuBinding
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var navController: NavController
    val authViewModel: AuthViewModel by viewModels()

    val TAG: String = "ReceitaListingFragment"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startUI()
    }

    private fun setNavController(){
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        navController = navHostFragment.navController
        setupWithNavController(bottomNav,navController)
    }

    private fun startUI() {

        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomNav = findViewById(R.id.bottomNavigationView)
        setNavController()
        bottomNav.setOnItemSelectedListener {
            when (it.itemId){
                R.id.recipes -> {
                    val connected_to_internet:Boolean = isOnline(this)
                    val fragment:Fragment = RecipeListingFragment()
                    fragment.arguments = Bundle().apply {
                        putBoolean("connectivity",connected_to_internet)
                    }
                    replaceFragment(RecipeListingFragment())
                }
                R.id.profile -> replaceFragment(ProfileFragment())
                R.id.favorites -> replaceFragment(FavoritesFragment())
            }
            true
        }

    }


    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host,fragment)
        fragmentTransaction.commit()
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

}