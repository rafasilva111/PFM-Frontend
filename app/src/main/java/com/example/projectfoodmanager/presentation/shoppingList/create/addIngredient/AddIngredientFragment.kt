package com.example.projectfoodmanager.presentation.shoppingList.create.addIngredient

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.projectfoodmanager.databinding.FragmentAddIngredientBinding
import com.example.projectfoodmanager.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddIngredientFragment : Fragment() {

    // binding
    lateinit var binding: FragmentAddIngredientBinding

    // viewModels
    val userViewModel: UserViewModel by viewModels()

    // constants
    val TAG: String = "AddIngredientFragment"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View {
        binding = FragmentAddIngredientBinding.inflate(layoutInflater)


        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}