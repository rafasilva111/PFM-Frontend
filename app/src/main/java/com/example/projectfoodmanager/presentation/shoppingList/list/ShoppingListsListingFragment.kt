package com.example.projectfoodmanager.presentation.shoppingList.list
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.databinding.FragmentShoppingListListingBinding
import com.example.projectfoodmanager.util.NetworkResult
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.example.projectfoodmanager.viewmodels.ShoppingListViewModel

import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ShoppingListsListingFragment : Fragment() {
    lateinit var binding: FragmentShoppingListListingBinding

    val TAG: String = "ShoppingListListingFragment"

    val authViewModel: AuthViewModel by viewModels()
    private val shoppingListViewModel by activityViewModels<ShoppingListViewModel>()

    // adapters
    private val calenderShoppingListAdapter by lazy {
        ShoppingListsListingAdapter(
            context,
            onItemClicked = { _, item ->
                findNavController().navigate(R.id.action_shoppingListListingFragment_to_shoppingListFragment,Bundle().apply {
                    putParcelable("shopping_list",item)
                })
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View {
        binding = FragmentShoppingListListingBinding.inflate(layoutInflater)

        binding.shoppingListRV.layoutManager = LinearLayoutManager(activity)
        binding.shoppingListRV.adapter = calenderShoppingListAdapter

        bindObservers()

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        shoppingListViewModel.getShoppingLists()

        binding.backIB.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun bindObservers() {

        shoppingListViewModel.getShoppingLists.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        binding.progressBar.isGone = true

                        result.data?.let { it ->
                            if (it.result.isEmpty())
                                binding.noShoppingLists.visibility = View.VISIBLE
                            else
                                calenderShoppingListAdapter.updateList(it.result)
                        }

                    }
                    is NetworkResult.Error -> {

                    }
                    is NetworkResult.Loading -> {

                        binding.progressBar.isVisible = true
                    }
                }
            }
        }
    }
}