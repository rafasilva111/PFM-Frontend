package com.example.projectfoodmanager.presentation.favorites

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.FragmentFavoritesBinding
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.example.projectfoodmanager.viewmodels.RecipeViewModel
import com.example.projectfoodmanager.util.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.floor


@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    @Inject
    lateinit var sharedPreference: SharedPreference
    @Inject
    lateinit var tokenManager: TokenManager

    private val authViewModel: AuthViewModel by viewModels()
    private val recipeViewModel: RecipeViewModel by viewModels()

    lateinit var binding: FragmentFavoritesBinding

    private var isFirstTimeCall = true
    private var snapHelper: SnapHelper = PagerSnapHelper()
    lateinit var manager: LinearLayoutManager
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    private var listFavorited: MutableList<Recipe> = arrayListOf()
    private var listLiked: MutableList<Recipe> = arrayListOf()
    private var searchMode: Boolean = false
    private var user: User? = null
    private var buttonPressed: Button? = null

    val TAG: String = "FavoritesFragmentFragment"

    private val adapter by lazy {
        FavoritesRecipeListingAdapter(
            onItemClicked = { pos, item ->

//                findNavController().navigate(R.id.action_receitaListingFragment_to_receitaDetailFragment,Bundle().apply {
//                    putParcelable("note",item)
//                })
            },
            authViewModel,
            recipeViewModel
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //todo check for internet connection
        if (this::binding.isInitialized) {
            return binding.root
        } else {
            binding = FragmentFavoritesBinding.inflate(layoutInflater)
            manager = LinearLayoutManager(activity)
            manager.orientation = LinearLayoutManager.HORIZONTAL
            manager.reverseLayout = false
            binding.recyclerView.layoutManager = manager
            snapHelper.attachToRecyclerView(binding.recyclerView)


            //setRecyclerViewScrollListener()
            return binding.root
        }
    }


    private fun setRecyclerViewScrollListener() {
        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (!searchMode) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (isFirstTimeCall) {
                            isFirstTimeCall = false
                            binding.recyclerView.removeOnScrollListener(scrollListener)
                            val visibleItemCount: Int = manager.childCount
                            val pastVisibleItem: Int =
                                manager.findLastCompletelyVisibleItemPosition()
                            val pag_index =
                                floor(((pastVisibleItem + 1) / FireStorePaginations.RECIPE_LIMIT).toDouble())
//                            if ((pastVisibleItem + 1) % FireStorePaginations.RECIPE_LIMIT.toInt() == 0) {
//                                recipeViewModel.getRecipesPaginatedOld(false)
//                            }
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
        if (isOnline(view.context)) {
            binding.recyclerView.adapter = adapter
            bindObservers()



            //valida shared preferences
            try {
                user = sharedPreference.getUserSession()
                if (user!!.liked_recipes.isNullOrEmpty()) {
                    Log.d(TAG, "onViewCreated: user.saved_recipes is empty")
                    //Mensagem sem receitas
                    binding.tvNoRecipes.visibility = View.VISIBLE

                } else {
                    //Mensagem com receitas
                    binding.tvNoRecipes.visibility = View.GONE

                    // Primeira lista a aparecer
                    adapter.updateList(user!!.liked_recipes.toMutableList(),user!!)
                }
            } catch (e: Exception) {
                Log.d(TAG, "onViewCreated: User had no shared prefences...")
                // se não tiver shared preferences o user não tem sessão válida
                //tera um comportamento diferente offilne
                authViewModel.logoutUser()
            }

            binding.SVsearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(text: String?): Boolean {
                    if (text != null && text != "") {
                        recipeViewModel.getRecipesByTitleAndTags(text)
                    }

                    return true
                }
            })

            binding.btnLiked.setBackgroundResource(R.drawable.bg_default)

            //todo: RAFA
            // top nav bar
            binding.btnLiked.setOnClickListener {
                binding.cvCreateRecipe.visibility = View.GONE
                toast(getString(R.string.get_liked_recipes))
                binding.tvNoRecipes.isVisible = user!!.liked_recipes.isEmpty()


                if (buttonPressed != binding.btnLiked) {
                    binding.btnLiked.setBackgroundResource(R.drawable.bg_default)
                    buttonPressed = binding.btnLiked
                    buttonPressed?.background= resources.getDrawable(R.drawable.bg_default)

                }

                adapter.updateList(user!!.liked_recipes.toMutableList(), user!!)

            }

            binding.btnSaved.setOnClickListener {
                binding.cvCreateRecipe.visibility = View.GONE
                toast(getString(R.string.get_saved_recipes))
                binding.tvNoRecipes.isVisible = user!!.saved_recipes.isEmpty()

                if (buttonPressed != binding.btnSaved) {
                    buttonPressed?.setBackgroundColor(resources.getColor(R.color.bordeux))
                    buttonPressed = binding.btnSaved
                    buttonPressed?.setBackgroundColor(resources.getColor(R.color.black))
                }

                adapter.updateList(user!!.saved_recipes.toMutableList(), user!!)

            }

            binding.btnCreated.setOnClickListener {
                binding.cvCreateRecipe.visibility = View.VISIBLE
                //Todo: RAFAEL
                toast("Em desenvolvimento...")
                adapter.updateList(mutableListOf(), user!!)

            }

            binding.btnComment.setOnClickListener {
                binding.cvCreateRecipe.visibility = View.GONE
                //Todo: RAFAEL
                toast("Em desenvolvimento...")
                adapter.updateList(mutableListOf(), user!!)

            }

            binding.btnRecentes.setOnClickListener {
                binding.cvCreateRecipe.visibility = View.GONE
                //Todo: RAFAEL
                toast("Em desenvolvimento...")
                adapter.updateList(mutableListOf(), user!!)

            }

            // bottom nav bar

            binding.IBMeat.setOnClickListener {
                recipeViewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.CARNE)
            }
            binding.IBFish.setOnClickListener {
                recipeViewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.PEIXE)
            }
            binding.IBSoup.setOnClickListener {
                recipeViewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.SOPA)
            }
            binding.IBVegi.setOnClickListener {
                recipeViewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.VEGETARIANA)
            }
            binding.IBFruit.setOnClickListener {
                recipeViewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.FRUTA)
            }
            binding.IBDrink.setOnClickListener {
                recipeViewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.BEBIDAS)
            }
        } else {
            // TODO offline mode
            toast("Está offline")
        }
    }


    private fun bindObservers() {
        authViewModel.userLogoutResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        tokenManager.deleteToken()
                        sharedPreference.deleteUserSession()
                        toast(getString(R.string.user_had_no_shared_preferences))
                        findNavController().navigate(R.id.action_profile_to_login)
                    }
                    is NetworkResult.Error -> {
                        Log.d(TAG, "bindObservers: ${it.message}.")
                    }
                    is NetworkResult.Loading -> {
                        //binding.progressBar.isVisible = true
                    }
                }
            }
        })
    }

    /* private fun observer(){
         var firstTimeLoading = true

         authModel.getFavoriteRecipeList.observe(viewLifecycleOwner){state ->

             when(state){
                 is UiState.Loading ->{
                     if (firstTimeLoading)
                         binding.progressBar.show()
                     firstTimeLoading = false

                 }
                 is UiState.Success -> {
                     binding.progressBar.hide()
                     Log.d(TAG, "onViewCreated: "+state.data)
                     for (item in state.data.toMutableList())
                         if (listFavorited.indexOf(item)==-1)
                             listFavorited.add(item)
                     adapter.updateList(listFavorited)
                 }
                 is UiState.Failure -> {
                     binding.progressBar.hide()
                     toast(state.error)
                 }
             }
         }

         authModel.getLikedRecipeList.observe(viewLifecycleOwner){state ->

             when(state){
                 is UiState.Loading ->{
                     if (firstTimeLoading)
                         binding.progressBar.show()
                     firstTimeLoading = false

                 }
                 is UiState.Success -> {
                     binding.progressBar.hide()
                     Log.d(TAG, "onViewCreated: "+state.data)
                     for (item in state.data.toMutableList())
                         if (listLiked.indexOf(item)==-1)
                             listLiked.add(item)
                     adapter.updateList(listLiked)
                 }
                 is UiState.Failure -> {
                     binding.progressBar.hide()
                     toast(state.error)
                 }
             }
         }


         viewModel.recipe_search.observe(viewLifecycleOwner){state ->
             val lista:MutableList<Recipe> = arrayListOf()
             when(state){

                 is UiState.Loading ->{
                     adapter.updateList(arrayListOf())
                     binding.progressBar.show()

                 }
                 is UiState.Success -> {
                     binding.progressBar.hide()
                     for (item in state.data.toMutableList())
                         if (lista.indexOf(item)==-1)
                             lista.add(item)
                     adapter.updateList(lista)
                 }
                 is UiState.Failure -> {
                     binding.progressBar.hide()
                     toast(state.error)
                 }
             }
         }
     }
 */
    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
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


}