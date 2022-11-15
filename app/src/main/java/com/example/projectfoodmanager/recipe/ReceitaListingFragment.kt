package com.example.projectfoodmanager.recipe

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.projectfoodmanager.databinding.FragmentReceitaListingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReceitaListingFragment : Fragment() {

    val TAG: String = "ReceitaListingFragment"
    lateinit var binding: FragmentReceitaListingBinding
    val viewModel: RecipeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReceitaListingBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getRecipes()
        viewModel.recipe.observe(viewLifecycleOwner){
            it.forEach {
                Log.e(TAG,it.toString())
            }
        }
    }
}