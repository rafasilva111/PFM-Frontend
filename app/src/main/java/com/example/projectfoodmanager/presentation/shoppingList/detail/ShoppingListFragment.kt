package com.example.projectfoodmanager.presentation.shoppingList.detail

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectfoodmanager.data.model.modelResponse.shoppingList.ShoppingList
import com.example.projectfoodmanager.databinding.FragmentShoppingListDetailBinding
import com.example.projectfoodmanager.util.SharedPreference
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ShoppingListFragment : Fragment() {


    // binding
    private lateinit var binding: FragmentShoppingListDetailBinding

    // viewModels


    // constants
    private val TAG: String = "ShoppingListFragment"
    private var shoppingList: ShoppingList? = null

    // injects
    @Inject
    lateinit var sharedPreference: SharedPreference

    // adapters
    private val shoppingListAdapter by lazy {
        ShoppingListAdapter(
            requireContext(),
            onItemClicked = { pos, item ->
                // Handle item click here
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View {
        binding = FragmentShoppingListDetailBinding.inflate(layoutInflater)

        binding.shoppingListRV.layoutManager = LinearLayoutManager(activity)
        binding.shoppingListRV.adapter = shoppingListAdapter

        bindObservers()

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.backIB.setOnClickListener {
            findNavController().navigateUp()
        }

        if (Build.VERSION.SDK_INT >= 33) {
            // TIRAMISU
            shoppingList = arguments?.getParcelable("shopping_list", ShoppingList::class.java)
        } else {
            shoppingList = arguments?.getParcelable("shopping_list")
        }

        shoppingList?.let { it -> shoppingListAdapter.updateList(it.shoppingIngredients) }


    }

    private fun bindObservers() {

    }
}
