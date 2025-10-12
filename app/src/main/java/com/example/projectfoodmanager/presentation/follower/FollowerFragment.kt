package com.example.projectfoodmanager.presentation.follower

import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.follows.UserToFollow
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.FragmentFollowerBinding
import com.example.projectfoodmanager.presentation.follower.FollowerFragment.Companion.SelectedTab.FIND
import com.example.projectfoodmanager.presentation.follower.FollowerFragment.Companion.SelectedTab.FOLLOWERS
import com.example.projectfoodmanager.presentation.follower.FollowerFragment.Companion.SelectedTab.FOLLOWS
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.changeMenuVisibility
import com.example.projectfoodmanager.util.Helper.Companion.changeTheme
import com.example.projectfoodmanager.util.Helper.Companion.formatNameToNameUpper
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage
import com.example.projectfoodmanager.util.listeners.ImageLoadingListener
import com.example.projectfoodmanager.util.network.NetworkResult
import com.example.projectfoodmanager.util.sharedpreferences.SharedPreference
import com.example.projectfoodmanager.viewmodels.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FollowerFragment : Fragment(), ImageLoadingListener {



    /** Binding */
    lateinit var binding: FragmentFollowerBinding

    /** ViewModels */
    private val userViewModel by activityViewModels<UserViewModel>()

    /** Constants */

    lateinit var manager: LinearLayoutManager

    // Tab Control
    private var selectedTab: String = FOLLOWERS
    private var currentTab: View? = null

    private var itemPosition: Int = -1

    private var userId: Int? = null
    private var userName: String? = null
    private lateinit var currentUser: User


    // pagination and search
    private var newSearch: Boolean = false
    private var noMoreRecipesMessagePresented = false

    // debouncer
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var searchJob: Job? =null

    // to update items on FindFollowsListingAdapter
    private var followersListingUpdatePosition = -1
    // to update items on FindFollowsListingAdapter
    private var findFollowsListingUpdatePosition = -1

    /** Injections */

    @Inject
    lateinit var sharedPreference: SharedPreference

    /** Interfaces */
    override fun onImageLoaded() {
        requireActivity().runOnUiThread {
            if (binding.followerRV.visibility != View.VISIBLE) {
                adapterFollowers.imagesLoaded++

                val firstVisibleItemPosition = manager.findFirstVisibleItemPosition()
                val lastVisibleItemPosition = manager.findLastVisibleItemPosition()
                val visibleItemCount = lastVisibleItemPosition - firstVisibleItemPosition + 1

                // If all visible images are loaded, hide the progress bar
                if (adapterFollowers.imagesLoaded >= visibleItemCount) {
                    binding.progressBar.hide()
                    binding.followerRV.visibility = View.VISIBLE
                }
            }
        }
    }


    /** Adapters */
    private val adapterFollowers by lazy {
        FollowerListingAdapter(
            onItemClicked = { userId ->

                findNavController().navigate(R.id.action_followerFragment_to_profileFragment,Bundle().apply {
                    putInt("userId",userId)
                })

            },
            onActionBTNClicked = { position, userId ->
                userViewModel.postFollowRequest(userId)
                followersListingUpdatePosition = position
            },
            onRemoveBTNClicked = { position,userId ->

                itemPosition = position
                val title:String
                val message:String


                if (selectedTab== FOLLOWERS) {
                    title = "Remover seguidor?"
                    message = "Tem a certeza que pretende remover este seguidor?"
                }else{
                    title = "Deixar de seguir?"
                    message = "Tem a certeza que pretende deixar de seguir?"

                }

                MaterialAlertDialogBuilder(requireContext())
                    .setIcon(R.drawable.ic_follower_remove)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Sim") { _, _ ->
                        // Remove Follower or Followed

                        if(selectedTab== FOLLOWERS){
                            userViewModel.deleteFollower(userId)
                        }
                        else{
                            userViewModel.deleteFollow(userId)
                        }

                        followersListingUpdatePosition = position

                    }
                    .setNegativeButton("Não") { dialog, _ ->
                        // Close Dialog
                        dialog.dismiss()
                    }
                    .show()

            },
            this

        )
    }

    private val adapterFindFollows by lazy {
        FindFollowsListingAdapter(
            onItemClicked = {userId ->

                findNavController().navigate(R.id.action_followerFragment_to_profileFragment,Bundle().apply {
                    putInt("userId",userId)
                })

            },
            onActionBTNClicked = { position, userId ->
                userViewModel.postFollowRequest(userId)
                findFollowsListingUpdatePosition = position

            },
            onRemoveFollowRequestBTN = { position,userId ->
                MaterialAlertDialogBuilder(requireContext())
                    .setIcon(R.drawable.ic_follower_remove)
                    .setTitle(getString(R.string.fragment_follower_remove_follow_request_title))
                    .setMessage(getString(R.string.fragment_follower_remove_follow_request_message))
                    .setPositiveButton("Sim") { _, _ ->
                        // Remove Follow request
                        userViewModel.deleteFollowRequest(userId)
                        findFollowsListingUpdatePosition = position

                    }
                    .setNegativeButton("Não") { dialog, _ ->
                        // Close Dialog
                        dialog.dismiss()
                    }
                    .show()

            },
            this
        )
    }

    /**
     *  Android LifeCycle
     * */

    override fun onCreate(savedInstanceState: Bundle?) {

        arguments?.let {

            val userIdHelper = it.getInt("userId")
            userId =  if (userIdHelper != 0) userIdHelper else null
            userName = it.getString("userName")
            selectedTab = it.getString("follow_type", FOLLOWERS)
        }

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        if (!this::binding.isInitialized) {
            binding = FragmentFollowerBinding.inflate(layoutInflater)
            manager = LinearLayoutManager(activity)
            binding.followerRV.layoutManager = manager
            binding.followerRV.adapter = adapterFollowers


        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObservers()
    }

    override fun onStart() {
        setUI()
        super.onStart()
    }

    /**
     *  General
     * */


    private fun setUI() {

        /**
         *  General
         * */

        val activity = requireActivity()
        changeMenuVisibility(false, activity)
        changeTheme(false, activity, requireContext())


        currentUser = sharedPreference.getUserSession()

        /**
         * Info
         */

        binding.nameProfileTV.text= formatNameToNameUpper(userName!!)

        updateView(selectedTab)

        /**
         * Pagination
         */
        binding.followerRV.setOnScrollListener (object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (nextPage){

                        val pastVisibleItem: Int =  manager.findLastCompletelyVisibleItemPosition()

                        if ((pastVisibleItem + 5) >= adapterFindFollows.itemCount){

                            when (selectedTab) {
                                FIND -> {
                                    userViewModel.getUsersToFollow(page = ++currentPage,searchString = searchString)
                                }
                                FOLLOWERS -> {
                                    userViewModel.getFollowers(page = ++currentPage,userId = userId,searchString = searchString)
                                }
                                FOLLOWS -> {
                                    userViewModel.getFollows(page = ++currentPage,userId = userId,searchString = searchString)
                                }
                            }


                        }




                        /*if (followType == NOT_FOLLOWER){
                            val pastVisibleItem: Int =  manager.findLastCompletelyVisibleItemPosition()

                            if ((pastVisibleItem + 1) >= adapterFindFollows.getList().size){

                                userViewModel.getUsersToFollow(page = ++currentPage,searchString =stringToSearch)
                            }
                        }*/

                        //Log.d(TAG, pag_index.toString())
                        //Log.d(TAG, visibleItemCount.toString())
                        //Log.d(TAG, pastVisibleItem.toString())
                    }
                    else if (!noMoreRecipesMessagePresented){
                        noMoreRecipesMessagePresented = true
                        toast("Sorry cant find more users...",ToastType.ALERT)
                    }


                }

                super.onScrollStateChanged(recyclerView, newState)

            }
        })

        /**
         * Search filter
         */

        binding.SVsearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(text: String?): Boolean {
                if (text != null && text != "") {

                    // debouncer
                    searchJob?.cancel()

                    searchJob = coroutineScope.launch {
                        delay(DEBOUNCER_STRING_SEARCH)
                        if (searchString == text) {

                            // Control Variables
                            currentPage = 1
                            newSearch = true

                            // Reset
                            binding.progressBar.show()
                            binding.followerRV.visibility = View.INVISIBLE
                            binding.noItemsTV.visibility = View.INVISIBLE

                            when (selectedTab) {
                                FIND -> {
                                    userViewModel.getUsersToFollow(searchString = searchString)
                                }
                                FOLLOWERS -> {
                                    userViewModel.getFollowers(userId = userId,searchString = searchString)
                                }
                                FOLLOWS -> {
                                    userViewModel.getFollows(userId = userId,searchString = searchString)
                                }
                            }
                        }
                    }

                    searchString = text

                } // se já fez pesquisa e text vazio ( stringToSearch != null) e limpou o texto
                else if (searchString != "" && text == "") {
                    searchString = ""

                    when (selectedTab) {
                        FIND -> {
                            userViewModel.getUsersToFollow()
                        }
                        FOLLOWERS -> {
                            userViewModel.getFollowers(userId = userId)
                        }
                        FOLLOWS -> {
                            userViewModel.getFollows(userId = userId)
                        }
                    }

                } else {
                    searchString = ""
                }

                //slowly move to position 0
                binding.followerRV.layoutManager?.smoothScrollToPosition(binding.followerRV, null, 0)
                return true
            }
        })

        /**
         * Navigation
         */

        binding.backRegIB.setOnClickListener {
            findNavController().navigateUp()
        }


        binding.followersBTN.setOnClickListener {
            updateView(FOLLOWERS)

        }

        binding.followedsBTN.setOnClickListener {
            updateView(FOLLOWS)
        }

        binding.findFollowsBTN.setOnClickListener {
            updateView(FIND)
        }

        binding.requestFollowCV.setOnClickListener {

            findNavController().navigate(R.id.action_followerFragment_to_followRequestFragment)
        }
    }

    private fun bindObservers() {

        /**
         * Followers Tab
         * */

        /** Get followers */

        userViewModel.getUserFollowersLiveData.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        binding.followerRV.adapter = adapterFollowers

                        binding.progressBar.hide()

                        // sets page data

                        currentPage = result.data!!._metadata.page
                        nextPage = result.data._metadata.nextPage != null

                        noMoreRecipesMessagePresented = nextPage

                        // check if list empty

                        if(result.data.result.isEmpty()){

                            binding.noItemsTV.text=getString(R.string.COMMON_NO_FOLLOWERS)
                            binding.noItemsTV.visibility=View.VISIBLE
                            adapterFollowers.removeItems()
                            return@let
                        }else{
                            binding.noItemsTV.visibility=View.VISIBLE

                        }

                        // checks if new search

                        if (currentPage == 1){
                            usersListed = result.data.result
                            adapterFollowers.setItems(result.data.result,selectedTab)
                        }
                        else{
                            usersListed += result.data.result
                            adapterFollowers.addItems(result.data.result)
                        }


                    }
                    is NetworkResult.Error -> {
                        binding.progressBar.hide()
                        toast(result.message.toString(), type = ToastType.ERROR)
                    }
                    is NetworkResult.Loading -> {

                    }
                }
            }
        }

        /** Follow requests */

        userViewModel.getFollowRequestsLiveData.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {

                        if(it.data!!.result.isEmpty()) {
                            binding.requestFollowCV.visibility = View.GONE
                        }else{
                            // set number
                            binding.nRequestBadgeTV.text = it.data._metadata.totalItems.toString()
                            // set image
                            loadUserImage(binding.imgFirstReqIV,it.data.result[0].follower.imgSource)

                            binding.requestFollowCV.visibility=View.VISIBLE
                        }

                    }
                    is NetworkResult.Error -> {
                        toast(it.message.toString(), type = ToastType.ERROR)
                    }
                    is NetworkResult.Loading -> {
                        //todo rui falta progress bar
                        //binding.progressBar.show()

                    }
                }
            }
        }

        /** Delete follower */

        userViewModel.deleteFollowerLiveData.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        adapterFollowers.removeItem(followersListingUpdatePosition)

                        if (adapterFollowers.getItems().size == 0){
                            binding.noItemsTV.text = getString(R.string.COMMON_NO_FOLLOWERS)
                            binding.noItemsTV.visibility=View.VISIBLE


                        }else{
                            binding.noItemsTV.visibility=View.INVISIBLE
                        }


                    }
                    is NetworkResult.Error -> {
                        toast(it.message.toString(), type = ToastType.ERROR)
                    }
                    is NetworkResult.Loading -> {
                        //todo rui falta progress bar
                        //binding.progressBar.show()

                    }
                }
            }
        }

        /**
         * Follows Tab
         * */

        /** Get follows */

        userViewModel.getUserFollowsLiveData.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {

                        binding.progressBar.hide()
                        binding.followerRV.adapter = adapterFollowers


                        // sets page data

                        currentPage = result.data!!._metadata.page
                        nextPage = result.data._metadata.nextPage != null

                        noMoreRecipesMessagePresented = nextPage

                        // check if list empty

                        if(result.data.result.isEmpty()){
                            binding.noItemsTV.text=getString(R.string.COMMON_NO_FOLLOWS)
                            binding.noItemsTV.visibility=View.VISIBLE
                            adapterFollowers.removeItems()
                            return@let
                        }else{
                            binding.noItemsTV.visibility=View.GONE

                        }

                        // checks if new search

                        if (currentPage == 1){
                            usersListed = result.data.result
                            adapterFollowers.setItems(result.data.result,selectedTab)
                        }
                        else{
                            usersListed += result.data.result
                            adapterFollowers.addItems(result.data.result)
                        }



                    }
                    is NetworkResult.Error -> {
                        toast(result.message.toString(), type = ToastType.ERROR)
                    }
                    is NetworkResult.Loading -> {

                    }
                }
            }
        }

        userViewModel.deleteFollowLiveData.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        adapterFollowers.removeItem(followersListingUpdatePosition)
                        if (adapterFollowers.getItems().size == 0){
                            binding.noItemsTV.text = getString(R.string.COMMON_NO_FOLLOWERS)
                            binding.noItemsTV.visibility=View.VISIBLE


                        }else{
                            binding.noItemsTV.visibility=View.INVISIBLE
                        }


                    }
                    is NetworkResult.Error -> {
                        toast(it.message.toString(), type = ToastType.ERROR)
                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        }

        /** Delete follower */

        userViewModel.deleteFollowLiveData.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        adapterFollowers.removeItem(followersListingUpdatePosition)

                        if (adapterFollowers.getItems().size == 0){
                            binding.noItemsTV.text = getString(R.string.COMMON_NO_FOLLOWS)
                            binding.noItemsTV.visibility=View.VISIBLE


                        }else{
                            binding.noItemsTV.visibility=View.INVISIBLE
                        }


                    }
                    is NetworkResult.Error -> {
                        toast(it.message.toString(), type = ToastType.ERROR)
                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        }


        /**
         * Find Tab
         * */

        /** Find follows */

        userViewModel.getUsersToFollow.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {

                        binding.progressBar.hide()


                        // sets page data

                        currentPage = result.data!!._metadata.page
                        nextPage = result.data._metadata.nextPage != null

                        noMoreRecipesMessagePresented = nextPage

                        // check if list empty

                        if(result.data.result.isEmpty()){
                            binding.noItemsTV.text=getString(R.string.follower_fragment_no_more_people_to_follow)
                            binding.noItemsTV.visibility=View.VISIBLE
                            adapterFindFollows.removeItems()
                            return@let
                        }else{
                            binding.noItemsTV.visibility=View.GONE

                        }

                        // checks if new search


                        if (currentPage == 1){
                            adapterFindFollows.setItems(result.data.result)
                        }
                        else{
                            adapterFindFollows.addItems(result.data.result)
                        }



                    }
                    is NetworkResult.Error -> {
                        binding.progressBar.hide()
                        toast(result.message.toString(), type = ToastType.ERROR)
                    }
                    is NetworkResult.Loading -> {

                    }
                }
            }
        }

        /** Send Follow requests */

        userViewModel.postUserFollowRequestLiveData.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        if (it.data == 201) {
                            // Followed
                            adapterFindFollows.removeItem(findFollowsListingUpdatePosition)

                        }
                        else {
                            // Request was sent
                            adapterFindFollows.updateItem(findFollowsListingUpdatePosition, true)
                        }

                    }
                    is NetworkResult.Error -> {
                        toast(it.message.toString(), type = ToastType.ERROR)
                    }
                    is NetworkResult.Loading -> {

                    }
                }
            }
        }

        /** Delete follow requests */

        userViewModel.deleteFollowRequestLiveData.observe(viewLifecycleOwner) { response ->
            response.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        adapterFindFollows.updateItem(findFollowsListingUpdatePosition,false)

                    }
                    is NetworkResult.Error -> {
                        toast(it.message.toString(), type = ToastType.ERROR)
                    }
                    is NetworkResult.Loading -> {
                        //todo rui falta progress bar
                        //binding.progressBar.show()

                    }
                }
            }
        }





    }

    /**
     *  Functions
     * */

    private fun updateView(currentTabSelected: String) {

        // Reset 
        binding.progressBar.show()
        binding.followerRV.visibility = View.INVISIBLE
        binding.noItemsTV.visibility = View.INVISIBLE

        if (binding.followerRV.adapter != null)
            (binding.followerRV.adapter as ( BaseAdapter<*, *>)).removeItems()


        selectedTab = currentTabSelected

        currentTab?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))

        when (selectedTab) {
            FOLLOWERS -> {

                // Get Follow Requests
                userViewModel.getFollowRequests(pageSize = 1)
                binding.followerRV.adapter = adapterFollowers
                currentTab = binding.followersBTN

                userViewModel.getFollowers(userId = userId, searchString = searchString)
            }
            FOLLOWS -> {

                binding.requestFollowCV.visibility = View.GONE
                binding.followerRV.adapter = adapterFollowers
                currentTab = binding.followedsBTN

                userViewModel.getFollows(userId = userId, searchString = searchString)
            }
            else -> {

                binding.requestFollowCV.visibility = View.GONE
                binding.followerRV.adapter = adapterFindFollows
                currentTab = binding.findFollowsBTN

                userViewModel.getUsersToFollow(searchString = searchString)
            }
        }

        currentTab!!.setBackgroundResource(R.drawable.selector_tab_button)
    }


    /**
     *  Object
     * */

    companion object {

        private var usersListed: MutableList<User> = mutableListOf()
        private var usersToFollowListed: MutableList<UserToFollow> = mutableListOf()


        // pagination
        private var currentPage:Int = 1
        private var nextPage:Boolean = true

        // Filters
        private var searchTag: String = ""
        private var searchString: String = ""
        private var sortedBy: String = RecipesSortingType.VERIFIED


        object SelectedTab {
            const val FIND = "ALL"
            const val FOLLOWERS = "FOLLOWERS"
            const val FOLLOWS = "FOLLOWS"
        }
    }


}