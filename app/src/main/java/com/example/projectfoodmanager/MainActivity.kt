package com.example.projectfoodmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
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
    lateinit var binding: ActivityMainMenuBinding
    val TAG: String = "ReceitaListingFragment"
    protected var session: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ss:String = intent.getStringExtra("Key").toString()
        this.session == "ss"
        if (session != "value") {
            startActivity(Intent(this, LoginActivity::class.java))
        }




        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(RecipeListingFragment())
        binding.bottomNavigationView.setOnItemSelectedListener {

            when (it.itemId) {

                R.id.recipes ->replaceFragment(RecipeListingFragment())
                R.id.calender ->replaceFragment(CalenderFragment())
                R.id.favorites ->replaceFragment(FavoritesFragment())
                R.id.goal ->replaceFragment(GoalFragment())
                R.id.profile ->replaceFragment(ProfileFragment())
                else -> {
                    Log.d(TAG, "onCreate: ")
                }
            }
            true
        }


    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
    }

}