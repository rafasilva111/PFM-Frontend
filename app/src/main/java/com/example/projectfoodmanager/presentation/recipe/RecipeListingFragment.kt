package com.example.projectfoodmanager.presentation.recipe

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.recipe.list.RecipeResult
import com.example.projectfoodmanager.databinding.FragmentRecipeListingBinding
import com.example.projectfoodmanager.presentation.viewmodels.AuthViewModel
import com.example.projectfoodmanager.presentation.viewmodels.RecipeViewModel
import com.example.projectfoodmanager.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.floor

@AndroidEntryPoint
class RecipeListingFragment : Fragment() {

    // constantes (cuidado com esta merda)

    private var page:Int = 1
    private var searchPage:Int = 1
    private var stringToSearch: String? = null
    private var newSearch: Boolean = false
    private var recipeList: MutableList<RecipeResult> = mutableListOf()
    private var searchRecipeList: MutableList<RecipeResult> = mutableListOf()


    private var isFirstTimeCall = true
    private var snapHelper : SnapHelper = PagerSnapHelper()
    lateinit var manager: LinearLayoutManager
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    private var list: MutableList<Recipe> = arrayListOf()
    private var searchMode: Boolean = false

    val TAG: String = "RecipeListingFragment"
    lateinit var binding: FragmentRecipeListingBinding
    private val recipeViewModel by activityViewModels<RecipeViewModel>()
    private val authViewModel: AuthViewModel by viewModels()
    private val adapter by lazy {
        RecipeListingAdapter(
            onItemClicked = {pos,item ->

                findNavController().navigate(R.id.action_receitaListingFragment_to_receitaDetailFragment,Bundle().apply {
                    putParcelable("note",item)
                })

                changeVisib_Menu(false)
            },
            this.authViewModel,
            this.recipeViewModel
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //observer()
        //todo check for internet connection

        bindObservers()

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
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    binding.recyclerView.removeOnScrollListener(scrollListener)
                    //val visibleItemCount: Int = manager.childCount
                    val pastVisibleItem: Int =
                        manager.findLastCompletelyVisibleItemPosition()
                    //val pag_index = floor(((pastVisibleItem + 1) / FireStorePaginations.RECIPE_LIMIT).toDouble())

                    if (stringToSearch.isNullOrEmpty()){
                        if ((pastVisibleItem+2) >= recipeList.size ) {
                            page += 1
                            recipeViewModel.getRecipesPaginated(page)
                        }
                    }
                    else{
                        if ((pastVisibleItem+2) >= searchRecipeList.size ) {
                            page += 1
                            recipeViewModel.getRecipesByTitleAndTags(stringToSearch!!,page)
                        }
                    }

                    //Log.d(TAG, pag_index.toString())
                    //Log.d(TAG, visibleItemCount.toString())
                    Log.d(TAG, pastVisibleItem.toString())

                    binding.recyclerView.addOnScrollListener(scrollListener)

                }

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    isFirstTimeCall = true
                }

                super.onScrollStateChanged(recyclerView, newState)

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
                        // importante se não não funciona
                        newSearch = true
                        stringToSearch=text
                        recipeViewModel.getRecipesByTitleAndTags(text, searchPage)
                    }
                    else{
                        stringToSearch=null
                        adapter.updateList(recipeList)
                    }
                    return true
                }
            })
            recipeViewModel.getRecipesPaginated(page)


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
                newSearch = true
                stringToSearch=RecipeListingFragmentFilters.CARNE
                recipeViewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.CARNE)
            }
            binding.IBFish.setOnClickListener {
                newSearch = true
                stringToSearch=RecipeListingFragmentFilters.PEIXE
                recipeViewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.PEIXE)
            }
            binding.IBSoup.setOnClickListener {
                newSearch = true
                stringToSearch=RecipeListingFragmentFilters.SOPA
                recipeViewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.SOPA)
            }
            binding.IBVegi.setOnClickListener {
                newSearch = true
                stringToSearch=RecipeListingFragmentFilters.VEGETARIANA
                recipeViewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.VEGETARIANA)
            }
            binding.IBFruit.setOnClickListener {
                newSearch = true
                stringToSearch=RecipeListingFragmentFilters.FRUTA
                recipeViewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.FRUTA)
            }
            binding.IBDrink.setOnClickListener {
                newSearch = true
                stringToSearch=RecipeListingFragmentFilters.BEBIDAS
                recipeViewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.BEBIDAS)
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

    private fun showValidationErrors(error: String) {
        toast(error)
    }

    private fun bindObservers() {
        recipeViewModel.recipeResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let{
                when (it) {
                    is NetworkResult.Success -> { it
                        binding.progressBar.hide()
                        searchMode = false
                        page = it.data!!._metadata.page
                        if (it.data == null){
                            toast(getString(R.string.no_recipes_found))
                        }
                        else {
                            for (recipe in it.data.recipe_result) {
                                recipeList.add(recipe)
                            }
                            adapter.updateList(recipeList)

                        }
                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        binding.progressBar.show()

                    }
                }
            }
        })
        recipeViewModel.recipeSearchByTitleAndTagsResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let{
                when (it) {
                    is NetworkResult.Success -> { it
                        binding.progressBar.hide()
                        searchMode = true
                        if (it.data!!.recipe_result.isEmpty()){
                            //removing last page on recipes not found
                            page = it.data!!._metadata.page_count
                            toast(getString(R.string.no_recipes_found))
                        }
                        else {

                            // if new check
                            if (searchRecipeList.size != 0 && newSearch){
                                searchRecipeList = mutableListOf()
                                page=1
                            }
                            newSearch = false
                            for (recipe in it.data.recipe_result) {
                                searchRecipeList.add(recipe)
                            }
                            Log.d(TAG, "bindObservers: searchRecipeList: "+searchRecipeList.toString())
                            adapter.updateList(searchRecipeList)
                        }
                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        binding.progressBar.show()
                    }
                }
            }
        })
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