package com.example.projectfoodmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.projectfoodmanager.databinding.ActivityMainMenuBinding
import com.example.projectfoodmanager.databinding.FragmentRegisterBinding
import com.example.projectfoodmanager.ui.LoginActivity
import com.example.projectfoodmanager.ui.profile.ProfileFragment
import com.example.projectfoodmanager.ui.recipe.RecipeListingFragment
import com.example.projectfoodmanager.ui.views.CalenderFragment
import com.example.projectfoodmanager.ui.views.FavoritesFragment
import com.example.projectfoodmanager.ui.views.GoalFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainMenuBinding
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    val TAG: String = "ReceitaListingFragment"
    protected var session: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var ss:String = intent.getStringExtra("Key").toString()
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)



        bottomNav = findViewById(R.id.bottomNavigationView)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        navController = navHostFragment.navController
        setupWithNavController(bottomNav,navController)

        bottomNav.setOnItemSelectedListener {
            when (it.itemId){
                R.id.recipes -> replaceFragment(RecipeListingFragment())
                R.id.profile -> replaceFragment(ProfileFragment())
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


}