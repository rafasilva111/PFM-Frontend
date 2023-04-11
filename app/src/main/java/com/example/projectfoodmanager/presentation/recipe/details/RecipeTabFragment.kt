package com.example.projectfoodmanager.presentation.recipe.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.Recipe
import com.example.projectfoodmanager.databinding.FragmentRecipeDetailBinding
import com.example.projectfoodmanager.databinding.FragmentRecipeTabBinding


class RecipeTabFragment : Fragment() {
    lateinit var binding: FragmentRecipeTabBinding
    var objRecipe: Recipe? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (this::binding.isInitialized){
            return binding.root
        }else {
            // Inflate the layout for this fragment
            binding = FragmentRecipeTabBinding.inflate(layoutInflater)

            return binding.root
        }
    }    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        super.onViewCreated(view, savedInstanceState)

        updateUI()
    }

    private fun updateUI() {
        objRecipe = arguments?.getParcelable("note")
    }
}