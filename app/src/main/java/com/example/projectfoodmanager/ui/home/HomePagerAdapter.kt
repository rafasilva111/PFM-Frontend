package com.example.projectfoodmanager.ui.home

import android.icu.text.Transliterator.Position
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.projectfoodmanager.ui.profile.ProfileFragment
import com.example.projectfoodmanager.ui.recipe.RecipeListingFragment
import com.example.projectfoodmanager.util.HomeTabs

class HomePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = HomeTabs.values().size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            HomeTabs.NOTES.index -> RecipeListingFragment.newInstance(HomeTabs.NOTES.name)
            HomeTabs.TASKS.index -> ProfileFragment.newInstance(HomeTabs.TASKS.name)
            else -> throw IllegalStateException("Fragment not found")
        }
    }
}