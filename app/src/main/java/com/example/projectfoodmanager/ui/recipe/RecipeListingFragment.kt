package com.example.projectfoodmanager.ui.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.MainActivity
import com.example.projectfoodmanager.R
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
        RecipeListingAdapter(
            onItemClicked = {pos,item ->

            },
            onDeleteClicked = {pos,item->

            },
            onEditClicked = { pos, item ->
            }

        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecipeListingBinding.inflate(layoutInflater)

        // Inflate the layout for this fragmen
        return binding.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = adapter
        binding.button.setOnClickListener{
            findNavController().navigate(R.id.action_receitaListingFragment_to_receitaDetailFragment)
        }
        viewModel.getRecipes()
        viewModel.recipe.observe(viewLifecycleOwner){state ->
            when(state){
                is UiState.Loading ->{
                        binding.progressBar.show()

                }
                is UiState.Success -> {
                    binding.progressBar.hide()
                    adapter.updateList(state.data.toMutableList())
                }
                is UiState.Failure -> {
                    binding.progressBar.hide()
                    toast(state.error)
                }
            }
        }
    }
}