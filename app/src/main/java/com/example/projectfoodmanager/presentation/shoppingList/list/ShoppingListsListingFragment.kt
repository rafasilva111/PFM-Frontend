package com.example.projectfoodmanager.presentation.shoppingList.list
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelRequest.calender.shoppingList.ShoppingListRequest
import com.example.projectfoodmanager.databinding.FragmentShoppingListListingBinding
import com.example.projectfoodmanager.util.NetworkResult
import com.example.projectfoodmanager.util.SharedPreference
import com.example.projectfoodmanager.util.ToastType
import com.example.projectfoodmanager.util.toast
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.example.projectfoodmanager.viewmodels.ShoppingListViewModel

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ShoppingListsListingFragment : Fragment() {

    // binding
    lateinit var binding: FragmentShoppingListListingBinding

    // viewModels
    private val shoppingListViewModel by activityViewModels<ShoppingListViewModel>()

    // constants
    val TAG: String = "ShoppingListListingFragment"

    // injects
    @Inject
    lateinit var sharedPreference: SharedPreference

    // adapters
    private val calenderShoppingListAdapter by lazy {
        ShoppingListsListingAdapter(
            context,
            onItemClicked = { _, item ->
                findNavController().navigate(R.id.action_shoppingListListingFragment_to_shoppingListFragment,Bundle().apply {
                    putParcelable("shopping_list",item)
                })
            },
            onItemArchive = { _, item ->
                shoppingListViewModel.archiveShoppingList(item.id,createShoppingListRequest())
            },
            onItemEdit = { _, item ->
                toast("Not implemented yet.", ToastType.ALERT)
            },
            onItemDelete = { _, item ->
                shoppingListViewModel.deleteShoppingList(item.id)
            }

        )
    }

    private fun createShoppingListRequest(): ShoppingListRequest {
        return ShoppingListRequest(archived = true)
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

        calenderShoppingListAdapter.updateList(sharedPreference.getAllShoppingList())


        // backing button
        binding.backIB.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun bindObservers() {

        shoppingListViewModel.getUserShoppingLists.observe(viewLifecycleOwner) { event ->
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

        shoppingListViewModel.putShoppingListLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        toast("Shopping List successfully archived.")
                        calenderShoppingListAdapter.updateList(sharedPreference.getAllShoppingList())
                    }
                    is NetworkResult.Error -> {

                    }
                    is NetworkResult.Loading -> {


                    }
                }
            }
        }

        shoppingListViewModel.deleteShoppingListLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        toast("Shopping List successfully deleted.")
                        calenderShoppingListAdapter.updateList(sharedPreference.getAllShoppingList())
                    }
                    is NetworkResult.Error -> {

                    }
                    is NetworkResult.Loading -> {


                    }
                }
            }
        }
    }
}