package com.example.projectfoodmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.replace
import com.example.projectfoodmanager.databinding.ActivityTestMainMenuBinding
import com.example.projectfoodmanager.databinding.FragmentRecipeListingBinding
import com.example.projectfoodmanager.recipe.RecipeListingFragment
import com.example.projectfoodmanager.views.*

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityTestMainMenuBinding
    val TAG: String = "ReceitaListingFragment"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTestMainMenuBinding.inflate(layoutInflater)
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
        //TODO

        //check internet


        //send login




    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
    }
}