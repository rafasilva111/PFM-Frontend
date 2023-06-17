package com.example.projectfoodmanager.presentation.recipe.details

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe

class FragmentAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    recipe: Recipe
    ): FragmentStateAdapter(fragmentManager,lifecycle) {

    private var recipe: Recipe = recipe


    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {

        return if(position==0)
            RecipeTabFragment(recipe)
        else
            NutritionTabFragment(recipe)

    }


}