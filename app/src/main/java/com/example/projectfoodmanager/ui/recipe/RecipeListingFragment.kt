package com.example.projectfoodmanager.ui.recipe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.Recipe
import com.example.projectfoodmanager.databinding.FragmentRecipeListingBinding
import com.example.projectfoodmanager.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.floor

@AndroidEntryPoint
class RecipeListingFragment : Fragment() {


    private var isFirstTimeCall = true
    lateinit var manager: LinearLayoutManager
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    private var list: MutableList<Recipe> = arrayListOf()


    val TAG: String = "RecipeListingFragment"


    lateinit var binding: FragmentRecipeListingBinding
    val viewModel: RecipeViewModel by viewModels()
    val adapter by lazy {
        RecipeListingAdapter(
            onItemClicked = {pos,item ->
                findNavController().navigate(R.id.action_receitaListingFragment_to_receitaDetailFragment,Bundle().apply {
                    putParcelable("note",item)
                })
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
        manager = LinearLayoutManager(activity)
        binding.recyclerView.layoutManager = manager

        setRecyclerViewScrollListener()



        return binding.root
        }

    private fun setRecyclerViewScrollListener() {
        scrollListener = object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (isFirstTimeCall) {
                        isFirstTimeCall = false;

                        binding.recyclerView.removeOnScrollListener(scrollListener)
                        val visibleItemCount: Int = manager.childCount
                        val pastVisibleItem:Int = manager.findLastCompletelyVisibleItemPosition()
                        val pag_index = floor(((pastVisibleItem+1)/FireStorePaginations.RECIPE_LIMIT).toDouble())
                        if ((pastVisibleItem+1)%FireStorePaginations.RECIPE_LIMIT.toInt()==0){
                            viewModel.getRecipesPaginated((pag_index*FireStorePaginations.RECIPE_LIMIT).toLong())
                        }
                        Log.d(TAG, pag_index.toString())
                        Log.d(TAG, visibleItemCount.toString())
                        Log.d(TAG, pastVisibleItem.toString())
                        binding.recyclerView.addOnScrollListener(scrollListener)
                    }
                }

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    isFirstTimeCall = true;
                }

                super.onScrollStateChanged(recyclerView, newState)
            }
        }
        binding.recyclerView.addOnScrollListener(scrollListener)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = adapter

        viewModel.getRecipesPaginated(0)

        viewModel.recipe.observe(viewLifecycleOwner){state ->
            when(state){
                is UiState.Loading ->{
                        binding.progressBar.show()

                }
                is UiState.Success -> {
                    binding.progressBar.hide()
                    for (item in state.data.toMutableList())
                        if (list.indexOf(item)==-1)
                            list.add(item)
                    adapter.updateList(list)
                }
                is UiState.Failure -> {
                    binding.progressBar.hide()
                    toast(state.error)
                }
            }
        }
    }


}