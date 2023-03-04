package com.example.projectfoodmanager.presentation.recipe

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.Recipe
import com.example.projectfoodmanager.util.Resource
import com.example.projectfoodmanager.databinding.FragmentRecipeListingBinding
import com.example.projectfoodmanager.presentation.viewmodels.AuthViewModel
import com.example.projectfoodmanager.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.floor

@AndroidEntryPoint
class RecipeListingFragment : Fragment() {


    private var isFirstTimeCall = true
    private var snapHelper : SnapHelper = PagerSnapHelper()
    lateinit var manager: LinearLayoutManager
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    private var list: MutableList<Recipe> = arrayListOf()
    private var searchMode: Boolean = false

    val TAG: String = "RecipeListingFragment"


    lateinit var binding: FragmentRecipeListingBinding
    val viewModel: RecipeViewModel by viewModels()
    private val authModel: AuthViewModel by viewModels()
    private val adapter by lazy {
        RecipeListingAdapter(
            onItemClicked = {pos,item ->

                findNavController().navigate(R.id.action_receitaListingFragment_to_receitaDetailFragment,Bundle().apply {
                    putParcelable("note",item)
                })

                changeVisib_Menu(false)
            },
            this.authModel,
            this.viewModel
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //observer()
        //todo check for internet connection

        //authModel.getUserSession()

        if (this::binding.isInitialized){
            return binding.root
        }else {
            binding = FragmentRecipeListingBinding.inflate(layoutInflater)
            manager = LinearLayoutManager(activity)
            manager.orientation=LinearLayoutManager.HORIZONTAL
            manager.reverseLayout=false
            binding.recyclerView.layoutManager = manager
            snapHelper.attachToRecyclerView(binding.recyclerView)


            setRecyclerViewScrollListener()
            return binding.root
        }
        }


    override fun onResume() {
        changeVisib_Menu(true)
        super.onResume()
    }

    private fun setRecyclerViewScrollListener() {
        scrollListener = object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!searchMode) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (isFirstTimeCall) {
                            isFirstTimeCall = false;
                            binding.recyclerView.removeOnScrollListener(scrollListener)
                            val visibleItemCount: Int = manager.childCount
                            val pastVisibleItem: Int =
                                manager.findLastCompletelyVisibleItemPosition()
                            val pag_index =
                                floor(((pastVisibleItem + 1) / FireStorePaginations.RECIPE_LIMIT).toDouble())
                            if ((pastVisibleItem + 1) % FireStorePaginations.RECIPE_LIMIT.toInt() == 0) {
                                viewModel.getRecipesPaginated(false)
                            }
                            Log.d(TAG, pag_index.toString())
                            Log.d(TAG, visibleItemCount.toString())
                            Log.d(TAG, pastVisibleItem.toString())


                            binding.recyclerView.addOnScrollListener(scrollListener)
                        }
                    }

                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        isFirstTimeCall = true
                    }

                    super.onScrollStateChanged(recyclerView, newState)
                }
            }
        }
        binding.recyclerView.addOnScrollListener(scrollListener)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        changeVisib_Menu(true)

        if (isOnline(view.context)) {
            binding.recyclerView.adapter = adapter
            binding.SVsearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(text: String?): Boolean {
                    if (text != null && text != "") {
                        viewModel.getRecipesByTitle(text, true)
                    }

                    return true
                }
            })


            viewModel.getRecipesPaginated(true)
            var firstTimeLoading = true
            viewModel.recipe.observe(viewLifecycleOwner) { state ->

                when (state) {
                    is UiState.Loading -> {
                        if (firstTimeLoading)
                            binding.progressBar.show()
                        firstTimeLoading = false

                    }
                    is UiState.Success -> {
                        binding.progressBar.hide()
                        for (item in state.data.toMutableList())
                            if (list.indexOf(item) == -1)
                                list.add(item)
                        adapter.updateList(list)
                    }
                    is UiState.Failure -> {
                        binding.progressBar.hide()
                        toast(state.error)
                    }
                }
            }
            viewModel.recipe_search.observe(viewLifecycleOwner) { state ->
                val lista: MutableList<Recipe> = arrayListOf()
                when (state) {

                    is UiState.Loading -> {
                        adapter.updateList(arrayListOf())
                        binding.progressBar.show()

                    }
                    is UiState.Success -> {
                        binding.progressBar.hide()
                        for (item in state.data.toMutableList())
                            if (lista.indexOf(item) == -1)
                                lista.add(item)
                        adapter.updateList(lista)
                    }
                    is UiState.Failure -> {
                        binding.progressBar.hide()
                        toast(state.error)
                    }
                }
            }

            //nav search toppom

            binding.SSUGESTOES.setOnClickListener {
                toast("Em desenvolvimento...")
            }
            binding.SMELHORES.setOnClickListener {
                toast("Em desenvolvimento...")
            }
            binding.SRECENTES.setOnClickListener {
                toast("Em desenvolvimento...")
            }
            binding.SPERSONALIZADAS.setOnClickListener {
                toast("Em desenvolvimento...")
            }


            //nav search bottom

            binding.IBMeat.setOnClickListener {
                viewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.CARNE, true)
            }
            binding.IBFish.setOnClickListener {
                viewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.PEIXE, true)
            }
            binding.IBSoup.setOnClickListener {
                viewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.SOPA, true)
            }
            binding.IBVegi.setOnClickListener {
                viewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.VEGETARIANA, true)
            }
            binding.IBFruit.setOnClickListener {
                viewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.FRUTA, true)
            }
            binding.IBDrink.setOnClickListener {
                viewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.BEBIDAS, true)
            }
        }
        else{
            binding.offlineText.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        }
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if ( connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }


    /*fun observer() {
        authModel.user.observe(viewLifecycleOwner){ response ->
            when(response){
                is Resource.Loading -> {
                    Log.i(TAG,"Loading...")
                }
                is Resource.Success -> {
                    response.data
                    val user = response.data
                    binding.tvName.text = user!!.first_name + " " + user!!.last_name
                    Log.i(TAG,"${response.data}")
                }
                is Resource.Error -> {
                    if (response.code == ERROR_CODES.SESSION_INVALID){
                        findNavController().navigate(R.id.action_recipeListingFragment_to_loginFragment)
                        //todo delete user prefs
                        toast(getString(R.string.invalid_session))
                    }
                    Log.i(TAG,"${response.message}")
                }
                else -> {}
            }
        }
    }*/

    private fun changeVisib_Menu(state : Boolean){
        val menu = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        if(state){
            menu!!.visibility=View.VISIBLE
        }else{
            menu!!.visibility=View.GONE
        }
    }

}