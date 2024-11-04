package com.example.projectfoodmanager.presentation.profile.user.recipe

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeSimplified
import com.example.projectfoodmanager.databinding.FragmentProfileRecipeListingBinding
import com.example.projectfoodmanager.presentation.profile.user.ProfileRecipesAdapter
import com.example.projectfoodmanager.util.DEFAULT_NR_OF_IMAGES_BY_RECIPE_CARD
import com.example.projectfoodmanager.util.Helper.Companion.changeMenuVisibility
import com.example.projectfoodmanager.util.Helper.Companion.changeTheme
import com.example.projectfoodmanager.util.Helper.Companion.isOnline
import com.example.projectfoodmanager.util.RecipePreloadModelProvider
import com.example.projectfoodmanager.util.RecipesSortingType
import com.example.projectfoodmanager.util.ToastType
import com.example.projectfoodmanager.util.hide
import com.example.projectfoodmanager.util.listeners.ImageLoadingListener
import com.example.projectfoodmanager.util.network.NetworkResult
import com.example.projectfoodmanager.util.sharedpreferences.SharedPreference
import com.example.projectfoodmanager.util.sharedpreferences.TokenManager
import com.example.projectfoodmanager.util.toast
import com.example.projectfoodmanager.viewmodels.RecipeViewModel
import javax.inject.Inject


class ProfileRecipeListingFragment : Fragment(), ImageLoadingListener {

    /** binding */
    private lateinit var binding: FragmentProfileRecipeListingBinding

    /** viewModels */
    private val recipeViewModel: RecipeViewModel by activityViewModels<RecipeViewModel>()

    /** Constants */
    private val TAG: String = "ProfileRecipeListingFragment"

    // RecyclerView
    private lateinit var manager: LinearLayoutManager
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    private lateinit var preloadModelProvider: RecipePreloadModelProvider

    private var sortBySelected: String = RecipesSortingType.ALL

    // Pagination
    private var noMoreRecipesMessagePresented = false

    /** injects */
    @Inject
    lateinit var sharedPreference: SharedPreference
    @Inject
    lateinit var tokenManager: TokenManager


    /** Adapters */

    private val adapter by lazy {
        ProfileRecipesAdapter(
            onItemClicked = { position, item ->
                val bundle = Bundle()
                bundle.putInt("recipe_id", item.id)

                findNavController().navigate(R.id.action_profileRecipeListingFragment_to_receitaDetailFragment, bundle)
            },
            this
        )
    }

    /** Interfaces */

    override fun onImageLoaded() {
        requireActivity().runOnUiThread {
            if (binding.recipeRV.visibility != View.VISIBLE && binding.recipeRV.visibility != View.GONE) {
                adapter.imagesLoaded++

                val firstVisibleItemPosition = manager.findFirstVisibleItemPosition()
                val lastVisibleItemPosition = manager.findLastVisibleItemPosition()
                val visibleItemCount = lastVisibleItemPosition - firstVisibleItemPosition + 1

                // If all visible images are loaded, hide the progress bar
                if (adapter.imagesLoaded >= visibleItemCount) {
                    showRecyclerView()
                }
            }
        }
    }

    /**
     *  Android LifeCycle
     * */

    override fun onCreate(savedInstanceState: Bundle?) {

        arguments?.let {

            sortBySelected = arguments?.getString("sortby")!!

        }

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::binding.isInitialized) {
            binding = FragmentProfileRecipeListingBinding.inflate(layoutInflater)

            manager = LinearLayoutManager(activity)
            manager.orientation=LinearLayoutManager.VERTICAL
            manager.reverseLayout=false
            binding.recipeRV.layoutManager = manager
            binding.recipeRV.itemAnimator = null
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setUI()
        bindObservers()

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        // Notas : loadUI tem que ser carregada sempre que o fragment começa, porque temos de ter sempre a copia dos dados mais frescos sempre
        // que entramos na view, esta depois poderá ser um load offline ou um load online
        loadUI()
        super.onStart()
    }

    private fun loadUI() {



        /**
         *  Navigations
         * */






    }


    private fun setUI() {

        /**
         * General
         */

        val activity = requireActivity()
        changeMenuVisibility(false, activity)
        changeTheme(false,activity,requireContext())

        Glide.with(this).load(R.drawable.recipe_book).into(binding.loader.loadIV)


        binding.header.backIB.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.loader.tryAgainBtn.setOnClickListener {
            binding.loader.loadingLL.visibility = View.VISIBLE
            binding.loader.errorLL.visibility = View.GONE
            updateView(sortBySelected)
        }

        setRecyclerViewScrollListener()

        when(sortBySelected){
            RecipesSortingType.LIKES -> {
                binding.header.titleTV.text = "Most Liked Recipes"
            }
            RecipesSortingType.VERIFIED -> {
                binding.header.titleTV.text = "Verified Recipes"
            }
            RecipesSortingType.ALL-> {
                binding.header.titleTV.text = "All Recipes"
            }
            RecipesSortingType.DATE-> {
                binding.header.titleTV.text = "Most Recent Recipes"
            }
            RecipesSortingType.SUGGESTIONS-> {
                binding.header.titleTV.text = "Suggestions"
            }
            RecipesSortingType.PERSONALIZED_SUGGESTIONS-> {
                binding.header.titleTV.text = "Personalized Suggestions"
            }
            RecipesSortingType.SAVES-> {
                binding.header.titleTV.text = "Most Saved Recipes"
            }
        }

        if (isOnline(requireContext())) {
            binding.recipeRV.adapter = adapter

            // if firstTime
            if (currentPosition == -1)
                updateView(sortBySelected)
            else{
                // If user entered in a recipe, update the item in the list
                recipeViewModel.getRecipe(adapter.getItems()[currentPosition].id)
            }
            // get recipes for first time

        } else {
            binding.loader.errorLL.visibility = View.VISIBLE
            binding.loader.messageTV.text = "No internet connection"
            hideRecyclerView()
        }
    }

    private fun bindObservers() {
        recipeViewModel.functionGetRecipes.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {

                        // Control Variables
                        currentPage = result.data!!._metadata.page
                        nextPage = result.data._metadata.nextPage != null

                        noMoreRecipesMessagePresented = nextPage

                        // Check if list empty
                        if(result.data.result.isEmpty()){

                            binding.noRecipesTV.visibility=View.VISIBLE
                            return@let
                        }else{
                            binding.noRecipesTV.visibility=View.GONE
                        }

                        val totalItems = result.data._metadata.totalItems
                        binding.nrRecipesTV.text = "Resultado:  $totalItems ${if (totalItems > 1) "Receitas" else "Receita"}"

                        // Checks if new search
                        if (currentPage == 1){
                            adapter.setItems(result.data.result)
                            setItemsToImagePreload(result.data.result)
                        }
                        else{
                            adapter.addItems(result.data.result)
                        }

                        binding.loader.loadingLL.visibility = View.GONE
                        binding.recipeListingCL.visibility = View.VISIBLE

                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(result.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        binding.loader.errorLL.visibility = View.GONE

                        //binding.offlineTV.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun showValidationErrors(error: String) {
        binding.loader.loadingLL.visibility = View.GONE
        binding.loader.errorLL.visibility = View.VISIBLE

        //TODO: set title
        binding.loader.titleTV.text = "Ups! Something went wrong."
        binding.loader.messageTV.text = error
    }

    /**
     *  Functions
     * */

    private fun setItemsToImagePreload(recipeList: MutableList<RecipeSimplified>) {
        if (! ::preloadModelProvider.isInitialized){
            initImagePreload(recipeList)
        }
        preloadModelProvider.setItems(recipeList)
    }

    private fun initImagePreload(recipeList: MutableList<RecipeSimplified>) {
        preloadModelProvider = RecipePreloadModelProvider(recipeList, requireContext())
        val preloader = RecyclerViewPreloader(
            Glide.with(this),
            preloadModelProvider,
            ViewPreloadSizeProvider(),
            5, /* maxPreload */
        )
        binding.recipeRV.addOnScrollListener(preloader)
    }

    private fun setRecyclerViewScrollListener() {
        scrollListener = object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

                val pastVisibleItemSinceLastFetch: Int = manager.findLastVisibleItemPosition()

                // if User is on the penultimate recipe of currenct page, get next page
                if (pastVisibleItemSinceLastFetch >= (adapter.itemCount - 3) && pastVisibleItemSinceLastFetch <= adapter.itemCount){
                    if (isOnline(requireContext())){
                        if (nextPage){

                            recipeViewModel.getRecipes(page = ++currentPage, searchString = searchString,searchTag= searchTag, by = sortedBy)

                            // prevent double request, this variable is change after response from getRecipes
                            nextPage = false
                        }
                    }else {
                        toast("No internet connection",ToastType.ALERT)
                    }
                }

                // if User is on the last recipe of currenct page, and no next page present notice to user
                if (pastVisibleItemSinceLastFetch == (adapter.itemCount - 1))
                    if (!nextPage && !noMoreRecipesMessagePresented){
                        noMoreRecipesMessagePresented = true
                        toast("Sorry cant find more recipes.",ToastType.ALERT)
                    }


                super.onScrollStateChanged(recyclerView, newState)

            }
        }
        binding.recipeRV.addOnScrollListener(scrollListener)

    }

    private fun updateView(currentTabSelected: String) {

        hideRecyclerView()

        when(currentTabSelected){
            RecipesSortingType.VERIFIED -> {
                sortedBy = RecipesSortingType.VERIFIED
            }
            RecipesSortingType.ALL-> {
                sortedBy = RecipesSortingType.ALL

            }
            RecipesSortingType.DATE-> {
                sortedBy = RecipesSortingType.DATE
            }
            RecipesSortingType.SUGGESTIONS-> {
                toast("Sorry, not implement yet...")
                binding.progressBar.hide()
                return
            }
            RecipesSortingType.PERSONALIZED_SUGGESTIONS-> {
                toast("Sorry, not implement yet...")
                binding.progressBar.hide()
                return
            }
            RecipesSortingType.RANDOM-> {
                sortedBy = RecipesSortingType.RANDOM
            }
            RecipesSortingType.LIKES-> {
                sortedBy = RecipesSortingType.LIKES
            }
            RecipesSortingType.SAVES-> {
                sortedBy = RecipesSortingType.SAVES
            }
        }

        // If first loading

        recipeViewModel.getRecipes(
            page = 1,
            searchString = searchString,
            searchTag = searchTag,
            by = sortedBy
        )

        //slowly move to position 0
        binding.recipeRV.layoutManager?.smoothScrollToPosition(binding.recipeRV, null, 0)


    }

    private fun showRecyclerView() {
        binding.loader.loadingLL.visibility = View.GONE
        binding.recipeRV.visibility = View.VISIBLE
        // Reset the number of images loaded
        adapter.imagesLoaded = 0

        if ( currentPosition != -1) {
            binding.recipeRV.scrollToPosition(currentPosition)

            // Reset current position
            currentPosition = -1
        }
    }

    private fun hideRecyclerView() {

        binding.loader.loadingLL.visibility = View.VISIBLE
        binding.recipeRV.visibility = View.INVISIBLE
    }
    /**
     *  Object
     * */

    companion object {

        private var itemsListed: MutableList<RecipeSimplified> = mutableListOf()

        // pagination
        private var currentPage:Int = 1
        private var currentPosition:Int = -1
        private var nextPage:Boolean = true

        // Filters
        private var searchTag: String = ""
        private var searchString: String = ""
        private var sortedBy: String = RecipesSortingType.VERIFIED

    }
}