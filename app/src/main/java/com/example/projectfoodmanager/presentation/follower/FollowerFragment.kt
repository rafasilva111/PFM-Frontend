package com.example.projectfoodmanager.presentation.follower

import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.follows.UserToFollow
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.FragmentFollowerBinding
import com.example.projectfoodmanager.presentation.follower.FollowerFragment.Companion.SelectedTab.ALL
import com.example.projectfoodmanager.presentation.follower.FollowerFragment.Companion.SelectedTab.FOLLOWERS
import com.example.projectfoodmanager.presentation.follower.FollowerFragment.Companion.SelectedTab.FOLLOWS
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.formatNameToNameUpper
import com.example.projectfoodmanager.util.Helper.Companion.loadUserImage
import com.example.projectfoodmanager.util.network.NetworkResult
import com.example.projectfoodmanager.util.sharedpreferences.SharedPreference
import com.example.projectfoodmanager.viewmodels.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FollowerFragment : Fragment() {



    /** Binding */
    lateinit var binding: FragmentFollowerBinding

    /** ViewModels */
    private val userViewModel by activityViewModels<UserViewModel>()

    /** Constants */


    private var selectedTab: String = FOLLOWERS
    private var currentTab: View? = null


    private var itemPosition: Int = -1

    private var userToFollowList: MutableList<UserToFollow> = mutableListOf()
    private var followList: MutableList<User> = mutableListOf()

    private var userId: Int = -1
    private var userName: String? = null
    private lateinit var currentUser: User


    // pagination and search
    private var currentPage:Int = 1
    private var nextPage:Boolean = true
    private var newSearch: Boolean = false
    private var stringToSearch: String = ""
    private var noMoreRecipesMessagePresented = false
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    lateinit var manager: LinearLayoutManager

    // to update items on FindFollowsListingAdapter
    private var followersListingUpdatePosition = -1
    // to update items on FindFollowsListingAdapter
    private var findFollowsListingUpdatePosition = -1

    /** Injections */

    @Inject
    lateinit var sharedPreference: SharedPreference

    /** Adapters */

    private val adapterFollowers by lazy {
        FollowerListingAdapter(
            onItemClicked = {user_id ->
                val bundle=Bundle()
                if (currentUser.id==user_id){
                    bundle.putInt("userId",-1)
                }else{
                    bundle.putInt("userId",user_id)
                }

                findNavController().navigate(R.id.action_followerFragment_to_profileFragment,bundle)

            },
            onActionBTNClicked = { position, user_Id ->
                userViewModel.postFollowRequest(user_Id)
                followersListingUpdatePosition = position
            },
            onRemoveBTNClicked = { position,user_Id ->

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
                            userViewModel.deleteFollower(user_Id)
                        }
                        else{
                            userViewModel.deleteFollow(user_Id)
                        }

                        followersListingUpdatePosition = position

                    }
                    .setNegativeButton("Não") { dialog, _ ->
                        // Close Dialog
                        dialog.dismiss()
                    }
                    .show()

            }
        )
    }

    private val adapterFindFollows by lazy {
        FindFollowsListingAdapter(
            onItemClicked = {user_id ->
                val bundle=Bundle()
                if (currentUser.id==user_id){
                    bundle.putInt("userId",-1)
                }else{
                    bundle.putInt("userId",user_id)
                }

                findNavController().navigate(R.id.action_followerFragment_to_profileFragment,bundle)

            },
            onActionBTNClicked = { position, user_Id ->
                userViewModel.postFollowRequest(user_Id)
                findFollowsListingUpdatePosition = position

            },
            onRemoveFollowRequestBTN = { position,user_Id ->
                MaterialAlertDialogBuilder(requireContext())
                    .setIcon(R.drawable.ic_follower_remove)
                    .setTitle(getString(R.string.fragment_follower_remove_follow_request_title))
                    .setMessage(getString(R.string.fragment_follower_remove_follow_request_message))
                    .setPositiveButton("Sim") { _, _ ->
                        // Remove Follow request
                        userViewModel.deleteFollowRequest(user_Id)
                        findFollowsListingUpdatePosition = position

                    }
                    .setNegativeButton("Não") { dialog, _ ->
                        // Close Dialog
                        dialog.dismiss()
                    }
                    .show()

            }
        )
    }

    /**
     *  Android LifeCycle
     * */

    override fun onCreate(savedInstanceState: Bundle?) {

        arguments?.let {
            userId = it.getInt("userId")
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
        Helper.changeMenuVisibility(false, activity)
        Helper.changeTheme(false, activity, requireContext())

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
                    // importante se não não funciona
                    currentPage = 1
                    newSearch = true

                    // debouncer
                    val handler = Handler()
                    handler.postDelayed({
                        if (stringToSearch == text) {
                            // verifica se tag está a ser usada se não pesquisa a string nas tags da receita
                            userViewModel.getUsersToFollow(searchString =stringToSearch)
                        }
                    }, 400)

                    stringToSearch = text

                } // se já fez pesquisa e text vazio ( stringToSearch != null) e limpou o texto
                else if (stringToSearch != "" && text == "") {
                    stringToSearch = ""
                    userViewModel.getUsersToFollow()
                } else {
                    stringToSearch = ""
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
            updateView(ALL)
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

        userViewModel.getUserFollowersLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {

                        // atualiza adapter list
                        adapterFollowers.updateList(it.data!!.result, FOLLOWS)
                        // sets the adapter
                        binding.followerRV.adapter = adapterFollowers

                        if (it.data.result.size == 0){
                            binding.emptyFollowTV.text = getString(R.string.no_followers)
                            binding.emptyFollowTV.visibility=View.VISIBLE


                        }else{
                            binding.emptyFollowTV.visibility=View.INVISIBLE
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

        /** Follow requests */

        userViewModel.getFollowRequestsLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {


                        if(it.data!!.result.isEmpty()) {
                            binding.requestFollowCV.visibility = View.GONE
                        }else{
                            // set number
                            binding.nRequestBadgeTV.text = it.data._metadata.totalItems.toString()
                            // set image
                            loadUserImage(binding.imgFirstReqIV,it.data.result[0].imgSource)

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

        userViewModel.deleteFollowerLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        adapterFollowers.removeItem(followersListingUpdatePosition)

                        if (adapterFollowers.getList().size == 0){
                            binding.emptyFollowTV.text = getString(R.string.no_followers)
                            binding.emptyFollowTV.visibility=View.VISIBLE


                        }else{
                            binding.emptyFollowTV.visibility=View.INVISIBLE
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

        userViewModel.getUserFollowsLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        // atualiza adapter list todo
                        adapterFollowers.updateList(it.data!!.result, "")

                        // sets the adapter
                        binding.followerRV.adapter = adapterFollowers

                        if (it.data.result.size == 0){
                            binding.emptyFollowTV.text = getString(R.string.no_follows)
                            binding.emptyFollowTV.visibility=View.VISIBLE


                        }else{
                            binding.emptyFollowTV.visibility=View.INVISIBLE



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

        userViewModel.deleteFollowLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        adapterFollowers.removeItem(followersListingUpdatePosition)
                        if (adapterFollowers.getList().size == 0){
                            binding.emptyFollowTV.text = getString(R.string.no_followers)
                            binding.emptyFollowTV.visibility=View.VISIBLE


                        }else{
                            binding.emptyFollowTV.visibility=View.INVISIBLE
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

        userViewModel.deleteFollowLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        adapterFollowers.removeItem(followersListingUpdatePosition)

                        if (adapterFollowers.getList().size == 0){
                            binding.emptyFollowTV.text = getString(R.string.no_follows)
                            binding.emptyFollowTV.visibility=View.VISIBLE


                        }else{
                            binding.emptyFollowTV.visibility=View.INVISIBLE
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
         * Find Tab
         * */

        /** Find follows */

        userViewModel.getUsersToFollow.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {

                        // sets page data

                        currentPage = it.data!!._metadata.page
                        nextPage = it.data._metadata.nextPage != null

                        // sets the adapter
                        binding.followerRV.adapter = adapterFindFollows

                        // check if list empty

                        if (it.data.result.isEmpty()){
                            binding.emptyFollowTV.text = getString(R.string.follower_fragment_no_more_people_to_follow)
                            binding.emptyFollowTV.visibility=View.VISIBLE
                            adapterFindFollows.updateList(it.data.result)
                            return@let
                        }else{
                            binding.emptyFollowTV.visibility=View.GONE
                        }


                        // checks if new search

                        if (currentPage == 1){
                            userToFollowList = it.data.result
                            noMoreRecipesMessagePresented = false
                        }
                        else{
                            userToFollowList += it.data.result
                        }

                        adapterFindFollows.updateList(userToFollowList)




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

        /** Send Follow requests */

        userViewModel.postUserFollowRequestLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
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
                        //todo rui falta progress bar
                        //binding.progressBar.show()

                    }
                }
            }
        }

        /** Delete follow requests */

        userViewModel.deleteFollowRequestLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
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

        selectedTab = currentTabSelected
        when (selectedTab) {
            FOLLOWERS -> {
                binding.SVsearch.visibility = View.GONE

                /**
                 * Search filter
                 */

                userViewModel.getFollowRequests(pageSize = 1)


                currentTab?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
                currentTab = binding.followersBTN
                currentTab!!.setBackgroundResource(R.drawable.selector_tab_button)

                userViewModel.getFollowers(id_user = userId)
            }
            FOLLOWS -> {

                binding.requestFollowCV.visibility = View.GONE
                binding.SVsearch.visibility = View.GONE

                currentTab?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
                currentTab = binding.followedsBTN
                currentTab!!.setBackgroundResource(R.drawable.selector_tab_button)
                userViewModel.getFollows(id_user = userId)
            }
            else -> {

                binding.requestFollowCV.visibility = View.GONE

                binding.SVsearch.visibility = View.VISIBLE


                currentTab?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
                currentTab = binding.findFollowsBTN
                currentTab!!.setBackgroundResource(R.drawable.selector_tab_button)
                userViewModel.getUsersToFollow()
            }
        }

    }

    /**
     *  Object
     * */

    companion object {

        object SelectedTab {
            const val ALL = "ALL"
            const val FOLLOWERS = "FOLLOWERS"
            const val FOLLOWS = "FOLLOWS"
        }
    }

}