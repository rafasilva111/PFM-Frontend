package com.example.projectfoodmanager.presentation.profile.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Recipe
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.FragmentProfileBinding
import com.example.projectfoodmanager.presentation.recipe.RecipeListingFragment
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.changeMenuVisibility
import com.example.projectfoodmanager.util.Helper.Companion.changeStatusBarColor
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage
import com.example.projectfoodmanager.viewmodels.RecipeViewModel
import com.example.projectfoodmanager.viewmodels.UserViewModel

import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment : Fragment() {


    // binding
    private lateinit var binding: FragmentProfileBinding

    // viewModels
    private val userViewModel by activityViewModels<UserViewModel>()
    private val recipeViewModel by activityViewModels<RecipeViewModel>()

    // constants
    private val TAG: String = "ProfileFragment"
    private lateinit var user: User
    private lateinit var recipeListed: MutableList<Recipe>

    private var manager: GridLayoutManager = GridLayoutManager(activity?.applicationContext, 3)


    // Pagination

    private var nextPage:Boolean = true
    private var currentPage:Int = 1
    private var noMoreRecipesMessagePresented:Boolean = true

    private var scrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                // prevent missed calls to api // needs to be reseted on search so it could be a next page

                if (nextPage){
                    val pastVisibleItem: Int = manager.findLastCompletelyVisibleItemPosition()

                    if ((pastVisibleItem + 1)  >= recipeListed.size){
                        recipeViewModel.getRecipes(page = ++currentPage, userId = user.id, pageSize = 15)
                    }
                }
                else if (!noMoreRecipesMessagePresented){
                    noMoreRecipesMessagePresented = true
                    toast("Sorry cant find more recipes.",ToastType.ALERT)
                }


            }

            super.onScrollStateChanged(recyclerView, newState)
        }
    }

    // injects
    @Inject
    lateinit var tokenManager: TokenManager
    @Inject
    lateinit var sharedPreference: SharedPreference

    // adapters
    private val profileRecipesAdapter by lazy {
        ProfileRecipesAdapter(
            onItemClicked = { selectedDate ->

            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View {

        if (!this::binding.isInitialized) {
            binding = FragmentProfileBinding.inflate(layoutInflater)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val userId = arguments?.getInt("user_id",-1)!!


        userViewModel.getUserAccount(userId)
        recipeViewModel.getRecipes(userId=userId, pageSize = 15)

        setUI()


        super.onViewCreated(view, savedInstanceState)
        bindObservers()
    }

    private fun setUI() {

        /**
         *  General
         * */

        val activity = requireActivity()
        changeMenuVisibility(false,activity)
        changeStatusBarColor(true,activity,requireContext())



        /**
         * Buttons
         */

        binding.backIB.setOnClickListener {
            findNavController().navigateUp()
        }

        /**
         * Recycler View ScrollView Listener
         */

        binding.recipesRV.layoutManager = manager


    }

    private fun loadUI(){

        /**
         * Image
         */

        loadUserImage(binding.profileIV, user.img_source)

        /**
         * Info
         */

        binding.nameTV.text =  getString(R.string.full_name, user.name)

        if(user.user_type == UserType.VIP){
            binding.profileCV.foreground = ContextCompat.getDrawable(requireContext(), R.drawable.border_vip);
            binding.vipIV.visibility=View.VISIBLE
        }

        binding.descTV.text = user.description

        binding.nFollowedsTV.text = user.followeds.toString()
        binding.nFollowersTV.text = user.followers.toString()

        /**
         * Profile Recipes Recycler ScrollView
         */


        binding.recipesRV.layoutManager = manager
        binding.recipesRV.adapter = profileRecipesAdapter

        binding.recipesRV.addOnScrollListener(scrollListener)


    }




    private fun showValidationErrors(error: String) {
        toast(String.format(resources.getString(R.string.txt_error_message, error)))
    }

    private fun bindObservers() {
        userViewModel.getUserAccountLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {

                        user = result.data!!
                        loadUI()
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

        recipeViewModel.recipesResponseLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {


                        // sets page data

                        currentPage = result.data!!._metadata.current_page
                        nextPage = result.data._metadata.next != null

                        // check if list empty

                       /* if(result.data.result.isEmpty()){
                            binding.offlineTV.text = getString(R.string.no_recipes_found)
                            binding.offlineTV.visibility=View.VISIBLE
                            adapter.updateList(mutableListOf())
                            return@let
                        }else{
                            binding.offlineTV.visibility=View.GONE

                        }*/

                        // checks if new search

                        if (currentPage == 1){
                            recipeListed = result.data.result
                            noMoreRecipesMessagePresented = false
                        }
                        else{
                            recipeListed += result.data.result
                        }

                        profileRecipesAdapter.updateList(result.data.result)
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
    }



}