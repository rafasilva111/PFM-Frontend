package com.example.projectfoodmanager.presentation.favorites

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
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
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.example.projectfoodmanager.viewmodels.RecipeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
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

    private var searchMode: Boolean = false
    lateinit var user: User
    private var buttonPressed: Button? = null


    val TAG: String = "FavoritesFragmentFragment"
    private var oldFiltTag: String =""

    private val adapter by lazy {
        FavoritesRecipeListingAdapter(
            onItemClicked = { _, item ->
                    findNavController().navigate(R.id.action_favoritesFragment_to_receitaDetailFragment,Bundle().apply {
                    putParcelable("Recipe",item)
                })
                changeVisibilityMenu(false)

            },
            onLikeClicked = {item,like ->
                if(like){
                    recipeViewModel.addLikeOnRecipe(item.id)
                }
                else{
                    recipeViewModel.removeLikeOnRecipe(item.id)
                }

            },
            onSaveClicked = {item,saved ->
                
                if(saved){
                    recipeViewModel.addSaveOnRecipe(item.id)
                }
                else{
                    recipeViewModel.removeSaveOnRecipe(item.id)
                }

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
        bindObservers()
        user = sharedPreference.getUserSession()
        //valida shared preferences
        try {

            if (user.liked_recipes.isNullOrEmpty()) {
                Log.d(TAG, "onViewCreated: user.saved_recipes is empty")
                //Mensagem sem receitas
                binding.tvNoRecipes.visibility = View.VISIBLE

            } else {
                //Mensagem com receitas
                binding.tvNoRecipes.visibility = View.GONE

                // Primeira lista a aparecer
                val test= user.getLikedRecipes()
                adapter.updateList(user.getLikedRecipes(), user)
            }
        } catch (e: Exception) {
            Log.d(TAG, "onViewCreated: User had no shared prefences...")
            // se não tiver shared preferences o user não tem sessão válida
            //tera um comportamento diferente offilne
            authViewModel.logoutUser()
        }


        //filtros mas com as chipViews
        val chipGroup: ChipGroup = binding.chipGroup
        chipGroup.setOnCheckedStateChangeListener { group, checkedId ->
            if (checkedId.isNotEmpty())
                group.findViewById<Chip>(checkedId[0])?.let { updateView(it) }
        }



        // bottom filters
        binding.meatFiltIB.setOnClickListener {
            recipeViewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.CARNE)
            filterOnClick("meat")
        }
        binding.fishFiltIB.setOnClickListener {
            recipeViewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.PEIXE)
            filterOnClick("fish")
        }
        binding.soupFiltIB.setOnClickListener {
            recipeViewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.SOPA)
            filterOnClick("soup")
        }
        binding.vegiFiltIB.setOnClickListener {
            recipeViewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.VEGETARIANA)
            filterOnClick("vegi")
        }
        binding.fruitFiltIB.setOnClickListener {
            recipeViewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.FRUTA)
            filterOnClick("fruit")
        }
        binding.drinkFiltIB.setOnClickListener {
            recipeViewModel.getRecipesByTitleAndTags(RecipeListingFragmentFilters.BEBIDAS)
            filterOnClick("drink")
        }

        binding.recyclerView.adapter = adapter

        // coisas que só faz online

        if (isOnline(view.context)) {

            // todo check if recipes removed from sharedPreferences ( in case user in offline mode removes like)
            // caso sim atualizar a bd

            recipeViewModel.getUserLikedRecipes()


            //validação da shared preferences feita no observer


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



            // todo atualiza a lista de comments mediante http://{{dev}}/api/v1/recipe/comments
            //authViewModel.getUserBackgrounds()


        } else {
            // TODO offline mode

            toast("Está offline")



        }
    }

    private fun updateView(currentTabSelected: View) {

        when(currentTabSelected){
            binding.chipCurtidas -> {
                // gostos

                //list
                val recipes = user.getLikedRecipes()
                if (recipes.isEmpty()){
                    binding.tvNoRecipes.visibility = View.VISIBLE
                    binding.tvNoRecipes.text = getString(R.string.no_recipes_liked)
                }
                else
                    binding.tvNoRecipes.visibility = View.INVISIBLE

                adapter.updateList(recipes, user)

            }
            binding.chipGuardados ->{
                // favoritos

                //list
                val recipes = user.getSavedRecipes()
                if (recipes.isEmpty()){
                    binding.tvNoRecipes.visibility = View.VISIBLE
                    binding.tvNoRecipes.text = getString(R.string.no_recipes_saved)
                }
                else
                    binding.tvNoRecipes.visibility = View.INVISIBLE

                adapter.updateList(recipes, user)
            }
            binding.chipCriadas ->{
                // criadas

                //list
                val recipes = user.getCreateRecipes()
                if (recipes.isEmpty()){
                    binding.tvNoRecipes.visibility = View.VISIBLE
                    binding.tvNoRecipes.text = getString(R.string.no_recipes_created)
                }
                else
                    binding.tvNoRecipes.visibility = View.INVISIBLE

                adapter.updateList(recipes, user)

            }
            binding.chipCommented ->{
                // criadas

                //list
                // todo rafael falta implementar isto, só pode ser feito online
                binding.tvNoRecipes.visibility = View.VISIBLE
                binding.tvNoRecipes.text = getString(R.string.not_implemented)

                adapter.updateList(mutableListOf(), user)
            }
            binding.chipLastSeem ->{
                // criadas

                //list
                // todo rafael falta implementar isto, só pode ser feito online

                binding.tvNoRecipes.visibility = View.VISIBLE
                binding.tvNoRecipes.text = getString(R.string.not_implemented)

                adapter.updateList(mutableListOf(), user)
            }
        }
    }

    private fun showValidationErrors(error: String) {
        toast(error)
        Log.d(TAG, "bindObservers: $error.")

    }

    private fun filterOnClick(tag:String){

        val clToUpdate: ConstraintLayout? = binding.root.findViewWithTag(oldFiltTag + "CL") as? ConstraintLayout
        val tvToUpdate: TextView? = binding.root.findViewWithTag(oldFiltTag + "TV") as? TextView
        val ibToUpdate: ImageButton? = binding.root.findViewWithTag(oldFiltTag + "_filt_IB") as? ImageButton

        clToUpdate?.apply {
            backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.transparent))
            elevation = 0f
        }

        tvToUpdate?.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.main_color)))

        ibToUpdate?.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F3F3F3"))

        if (oldFiltTag == tag) {
            oldFiltTag = ""
            recipeViewModel.getRecipesPaginated()
            //TODO: Rafa-> No recipe listing acrescentou se isto, mas aqui como esta diferente não sei
            //currentPage = 1
            return
        }

        oldFiltTag = tag

        val cl: ConstraintLayout? = binding.root.findViewWithTag(tag + "CL") as? ConstraintLayout
        val tv: TextView? = binding.root.findViewWithTag(tag + "TV") as? TextView
        val ib: ImageButton? = binding.root.findViewWithTag(tag + "_filt_IB") as? ImageButton

        cl?.apply {
            backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.main_color))
            elevation = 3f
        }

        tv?.setTextColor(resources.getColor(R.color.white))

        ib?.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.color_1))
    }

    private fun bindObservers() {

        recipeViewModel.userLikedRecipes.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        // atualiza lista de likes mediante remote

                        try {
                            user = sharedPreference.getUserSession()

                            if(user!!.liked_recipes != it.data!!.result ){
                                user!!.liked_recipes = it.data.result
                            }

                            sharedPreference.saveUserSession(user!!)

                            if (user!!.liked_recipes.isNullOrEmpty()) {
                                Log.d(TAG, "onViewCreated: user.saved_recipes is empty")
                                //Mensagem sem receitas
                                binding.tvNoRecipes.visibility = View.VISIBLE

                            } else {
                                //Mensagem com receitas
                                binding.tvNoRecipes.visibility = View.GONE

                                // Primeira lista a aparecer
                                adapter.updateList(user!!.getLikedRecipes(),user!!)
                            }
                        } catch (e: Exception) {
                            Log.d(TAG, "onViewCreated: User had no shared prefences...")
                            // se não tiver shared preferences o user não tem sessão válida
                            //tera um comportamento diferente offilne
                            authViewModel.logoutUser()
                        }

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

        // Like function
        recipeViewModel.functionLikeOnRecipe.observe(viewLifecycleOwner) { it ->
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        binding.progressBar.hide()
                        toast(getString(R.string.recipe_liked))

                        val listOnAdapter = adapter.getAdapterList()
                        // updates local list
                        for (item in listOnAdapter) {
                            if (item.id == it.data) {
                                item.likes++
                                adapter.updateItem(
                                    listOnAdapter.indexOf(item),
                                    item,
                                    sharedPreference.addLikeToUserSession(item)
                                )
                                break
                            }
                        }
                    }
                    is NetworkResult.Error -> {
                        binding.progressBar.hide()
                        showValidationErrors(it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        binding.progressBar.show()
                    }
                }
            }
        }

        recipeViewModel.functionRemoveLikeOnRecipe.observe(viewLifecycleOwner) { it ->
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        
                        binding.progressBar.hide()
                        toast(getString(R.string.recipe_removed_liked))

                        val listOnAdapter = adapter.getAdapterList()


                        // updates local list
                        for (item in listOnAdapter.toMutableList()) {
                            if (item.id == it.data) {
                                item.likes--
                                adapter.updateItem(
                                    listOnAdapter.indexOf(item),
                                    item,
                                    sharedPreference.removeLikeFromUserSession(item)
                                )
                                break
                            }
                        }
                    }
                    is NetworkResult.Error -> {
                        binding.progressBar.hide()
                        showValidationErrors(it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        binding.progressBar.show()
                    }
                }
            }
        }

        // save function

        recipeViewModel.functionAddSaveOnRecipe.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let{
                when (it) {
                    is NetworkResult.Success -> { it
                        binding.progressBar.hide()
                        toast(getString(R.string.recipe_saved))

                        val listOnAdapter = adapter.getAdapterList()

                        // updates local list
                        for (item in listOnAdapter){
                            if (item.id == it.data){
                                adapter.updateItem(listOnAdapter.indexOf(item),item,sharedPreference.addSaveToUserSession(item))
                                break
                            }
                        }
                    }
                    is NetworkResult.Error -> {
                        binding.progressBar.hide()
                        showValidationErrors(it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        binding.progressBar.show()
                    }
                }
            }
        })

        recipeViewModel.functionRemoveSaveOnRecipe.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let{
                when (it) {
                    is NetworkResult.Success -> { it
                        binding.progressBar.hide()
                        toast(getString(R.string.recipe_removed_from_saves))

                        val listOnAdapter = adapter.getAdapterList()

                        // updates local list
                        for (item in listOnAdapter){
                            if (item.id == it.data){
                                adapter.updateItem(listOnAdapter.indexOf(item),item,sharedPreference.removeSaveFromUserSession(item))
                                break
                            }
                        }
                    }
                    is NetworkResult.Error -> {
                        binding.progressBar.hide()
                        showValidationErrors(it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        binding.progressBar.show()
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

    private fun changeVisibilityMenu(state: Boolean) {
        val menu = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        if (state) {
            menu!!.visibility = View.VISIBLE
        } else {
            menu!!.visibility = View.GONE
        }
    }
}

