package com.example.projectfoodmanager.recipe

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.projectfoodmanager.databinding.FragmentRecipeListingBinding
import com.example.projectfoodmanager.util.UiState
import com.example.projectfoodmanager.util.hide
import com.example.projectfoodmanager.util.show
import com.example.projectfoodmanager.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipeListingFragment : Fragment() {

    val TAG: String = "ReceitaListingFragment"
    lateinit var binding: FragmentRecipeListingBinding
    val viewModel: RecipeViewModel by viewModels()
    val adapter by lazy {
        NoteListingAdapter{
            
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecipeListingBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getRecipes()
        viewModel.recipe.observe(viewLifecycleOwner){state ->
            when(state){
                is UiState.Loading ->{
                        binding.progressBar.show()

                }
                is UiState.Success -> {
                    binding.progressBar.hide()
                }
                is UiState.Failure -> {
                    binding.progressBar.hide()
                    toast(state.error)
                }
            }
        }
    }
}