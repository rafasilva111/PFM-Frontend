package com.example.projectfoodmanager.presentation.profile.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.recipe.RecipeSimplified
import com.example.projectfoodmanager.data.model.modelResponse.user.profile.UserProfile
import com.example.projectfoodmanager.databinding.FragmentProfileBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.changeMenuVisibility
import com.example.projectfoodmanager.util.Helper.Companion.changeTheme
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage
import com.example.projectfoodmanager.util.listeners.ImageLoadingListener
import com.example.projectfoodmanager.util.network.NetworkResult
import com.example.projectfoodmanager.util.sharedpreferences.SharedPreference
import com.example.projectfoodmanager.util.sharedpreferences.TokenManager
import com.example.projectfoodmanager.viewmodels.RecipeViewModel
import com.example.projectfoodmanager.viewmodels.UserViewModel

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment : Fragment() {


    /** binding */
    private lateinit var binding: FragmentProfileBinding

    /** viewModels */
    private val userViewModel by activityViewModels<UserViewModel>()
    private val recipeViewModel by activityViewModels<RecipeViewModel>()

    /** variables */
    private val TAG: String = "ProfileFragment"
    private var userId: Int = -1
    private lateinit var user: UserProfile
    private lateinit var recipeListed: MutableList<RecipeSimplified>

    private lateinit var mostPopularRVManager: LinearLayoutManager
    private lateinit var lastestRVManager: LinearLayoutManager
    private lateinit var preloadModelProvider: RecipePreloadModelProvider



    // Pagination
    private var nextPage:Boolean = true
    private var currentPage:Int = 1
    private var noMoreRecipesMessagePresented:Boolean = true


    /** injects */
    @Inject
    lateinit var tokenManager: TokenManager
    @Inject
    lateinit var sharedPreference: SharedPreference

    /** adapters */
    private val mostPopularRecipesAdapter by lazy {
        ProfileRecipesAdapter(
            onItemClicked = { position, item ->
                val bundle = Bundle()
                bundle.putInt("recipe_id", item.id)


                //findNavController().navigate(R.id.action_profileFragment_to_recipeFragment, bundle)
            },
            imageLoadingListener = object : ImageLoadingListener {
                override fun onImageLoaded() {
                    //binding.progressBar.isVisible = false
                }
            }
        )

    }

    private val latestRecipesAdapter by lazy {
        ProfileRecipesAdapter(
            onItemClicked = { position, item ->
                val bundle = Bundle()
                bundle.putInt("recipe_id", item.id)

                //findNavController().navigate(R.id.action_profileFragment_to_recipeFragment, bundle)
            },
            imageLoadingListener = object : ImageLoadingListener {
                override fun onImageLoaded() {
                    //binding.progressBar.isVisible = false
                }
            }
        )

    }


    /** Interfaces */

//    override fun onImageLoaded() {
//        requireActivity().runOnUiThread {
//            if (binding.recyclerView.visibility != View.VISIBLE && binding.recyclerView.visibility != View.GONE) {
//                adapter.imagesLoaded++
//
//                val firstVisibleItemPosition = manager.findFirstVisibleItemPosition()
//                val lastVisibleItemPosition = manager.findLastVisibleItemPosition()
//                val visibleItemCount = lastVisibleItemPosition - firstVisibleItemPosition + 1
//
//                // If all visible images are loaded, hide the progress bar
//                if (adapter.imagesLoaded >= visibleItemCount * DEFAULT_NR_OF_IMAGES_BY_RECIPE_CARD) {
//                    showRecyclerView()
//                }
//            }
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View {

        if (!this::binding.isInitialized) {
            binding = FragmentProfileBinding.inflate(layoutInflater)
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        userId = arguments?.getInt("user_id",-1)!!
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setUI()
        super.onViewCreated(view, savedInstanceState)
        bindObservers()
    }

    override fun onStart() {
        loadUI()
        super.onStart()
    }

    private fun setUI() {


        /**
         * Title
         */

        binding.header.notificationIB.visibility = View.VISIBLE

        /**
         * Buttons
         */

        binding.header.backIB.setOnClickListener {
            findNavController().navigateUp()
        }

        /**
         * Recycler View ScrollView Listener
         */

        mostPopularRVManager = LinearLayoutManager(activity)
        mostPopularRVManager.orientation = RecyclerView.HORIZONTAL

        lastestRVManager = LinearLayoutManager(activity)
        lastestRVManager.orientation = RecyclerView.HORIZONTAL


        binding.mostPopularRecipesRV.layoutManager = mostPopularRVManager
        binding.latestRecipesRV.layoutManager = lastestRVManager


    }

    private fun loadUI(){

        userViewModel.getUserAccount(userId)
        recipeViewModel.getRecipes(userId=userId, pageSize = 6, by = RecipesSortingType.LIKES)
        recipeViewModel.getRecipes(userId=userId, pageSize = 6, by = RecipesSortingType.DATE)
        //recipeViewModel.getRecipes(userId=userId, pageSize = 6, by = "user")

        /**
         *  General
         * */

        val activity = requireActivity()

        changeMenuVisibility(false,activity)
        changeTheme(false, requireActivity(), requireContext())

        if (::user.isInitialized)
            loadUserUI()

        /**
         * Profile Recipes Recycler ScrollView
         */


        binding.mostPopularRecipesRV.adapter = mostPopularRecipesAdapter
        binding.latestRecipesRV.adapter = latestRecipesAdapter


    }

    private fun loadUserUI() {


        binding.header.titleTV.text = user.name


        /**
         * Image
         */

        loadUserImage(binding.profileIV, user.imgSource)

        /**
         * Info
         */

        if (user.userType == UserType.VIP) {
            binding.profileCV.foreground = ContextCompat.getDrawable(requireContext(), R.drawable.border_vip);
            binding.vipIV.visibility = View.VISIBLE
        }else{
            binding.profileCV.foreground = null;
            binding.vipIV.visibility = View.GONE
        }

        binding.nameTV.text = getString(R.string.full_name, user.name)

        if (user.userType == UserType.VIP) {
            binding.profileCV.foreground = ContextCompat.getDrawable(requireContext(), R.drawable.border_vip);
            binding.vipIV.visibility = View.VISIBLE
        }

        if (user.verified) {
            binding.verifyUserIV.visibility = View.VISIBLE
        }

        binding.descTV.text = user.description.ifEmpty { "Sem descrição." }

        binding.nFollowedsTV.text = user.followsCount.toString()
        binding.nFollowersTV.text = user.followersCount.toString()

        binding.nRecipesTV.text = user.recipesCreated.toString()

        binding.seeMore1MB.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("sortby", RecipesSortingType.LIKES)
            findNavController().navigate(R.id.action_profileFragment_to_profileRecipeListingFragment, bundle)
        }

        binding.seeMore2MB.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("sortby", RecipesSortingType.DATE)
            findNavController().navigate(R.id.action_profileFragment_to_profileRecipeListingFragment, bundle)
        }
    }

    private fun showValidationErrors(error: String) {
        //toast(String.format(resources.getString(R.string.txt_error_message, error)))
    }

    private fun setItemsToImagePreload(recipeList: MutableList<RecipeSimplified>, sortingType: String) {
        if (! ::preloadModelProvider.isInitialized){
            preloadModelProvider = RecipePreloadModelProvider(recipeList, requireContext())
            val preloader = RecyclerViewPreloader(
                Glide.with(this),
                preloadModelProvider,
                ViewPreloadSizeProvider(),
                5, /* maxPreload */
            )

            when(sortingType){
                RecipesSortingType.LIKES -> {
                    binding.mostPopularRecipesRV.addOnScrollListener(preloader)
                }
                RecipesSortingType.DATE -> {
                    binding.latestRecipesRV.addOnScrollListener(preloader)
                }
            }

        }
        preloadModelProvider.setItems(recipeList)
    }

    private fun bindObservers() {
        userViewModel.getUserAccountLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {

                        user = result.data!!
                        loadUserUI()
                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(result.message.toString())
                    }
                    is NetworkResult.Loading -> {
                        //binding.progressBar.isVisible = true
                    }
                }
            }
        }

        /**
         * Recipes
         */

        recipeViewModel.functionGetRecipes.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {

                        // Check if list empty
                        if(result.data!!.result.isEmpty()){

                            // Hide topic
                            binding.noRecipesTV.visibility=View.VISIBLE
                            return@let
                        }else{

                            // Show topic
                            binding.noRecipesTV.visibility=View.GONE


                            val palavras  = listOf(RecipesSortingType.LIKES,RecipesSortingType.DATE)



                            val test = palavras.find { palavra -> result.data._metadata.currentPage!!.contains(palavra) }

                            when(test){
                                RecipesSortingType.LIKES -> {
                                    mostPopularRecipesAdapter.setItems(result.data.result)
                                    binding.mostPopularRecipesCL.visibility = View.VISIBLE
                                }
                                RecipesSortingType.DATE -> {
                                    latestRecipesAdapter.setItems(result.data.result)
                                    binding.latestRecipesCL.visibility = View.VISIBLE
                                }
                                else -> {
                                    mostPopularRecipesAdapter.setItems(result.data.result)
                                }
                            }

                            setItemsToImagePreload(result.data.result, result.data._metadata.currentPage.toString())

                        }

                    }
                    is NetworkResult.Error -> {
                        //binding.progressBar.hide()
                        toast(result.message.toString(), type = ToastType.ERROR)
                    }
                    is NetworkResult.Loading -> {
                        binding.noRecipesTV.visibility = View.GONE
                        //binding.offlineTV.visibility = View.GONE
                    }
                }
            }
        }
    }



}