package com.example.projectfoodmanager.presentation.follower.followRequests

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.FragmentFollowRequestBinding
import com.example.projectfoodmanager.util.network.NetworkResult
import com.example.projectfoodmanager.util.sharedpreferences.SharedPreference
import com.example.projectfoodmanager.util.ToastType
import com.example.projectfoodmanager.util.hide
import com.example.projectfoodmanager.util.listeners.ImageLoadingListener
import com.example.projectfoodmanager.util.toast
import com.example.projectfoodmanager.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FollowRequestFragment : Fragment(), ImageLoadingListener {

    /** Binding */
    lateinit var binding: FragmentFollowRequestBinding

    /** ViewModels */
    private val userViewModel by activityViewModels<UserViewModel>()

    /** Constants */

    lateinit var manager: LinearLayoutManager

    private lateinit var currentUser: User
    private var itemPosition: Int = -1

    /** Injections */
    @Inject
    lateinit var sharedPreference: SharedPreference

    /** Interfaces */
    override fun onImageLoaded() {
        requireActivity().runOnUiThread {
            if (binding.followerRV.visibility != View.VISIBLE) {
                adapter.imagesLoaded++

                val firstVisibleItemPosition = manager.findFirstVisibleItemPosition()
                val lastVisibleItemPosition = manager.findLastVisibleItemPosition()
                val visibleItemCount = lastVisibleItemPosition - firstVisibleItemPosition + 1

                // If all visible images are loaded, hide the progress bar
                if (adapter.imagesLoaded >= visibleItemCount) {
                    binding.progressBar.hide()
                    binding.followerRV.visibility = View.VISIBLE
                }
            }
        }
    }

    /** Adapters */
    private val adapter by lazy {
        FollowRequestListingAdapter(
            onItemClicked = { userID ->
                findNavController().navigate(R.id.action_followRequestFragment_to_profileFragment,Bundle().apply {
                    putInt("userId",userID)
                })
            },
            onActionBTNClicked = { position,userId ->
                itemPosition = position
                userViewModel.postAcceptFollowRequest(userId)
            }
        )
    }

    /**
     *  Android LifeCycle
     * */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        if (!this::binding.isInitialized) {
            binding = FragmentFollowRequestBinding.inflate(layoutInflater)

            binding.followerRV.layoutManager = LinearLayoutManager(activity)
            binding.followerRV.adapter = adapter

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
         * General
         */

        currentUser = sharedPreference.getUserSession()

        userViewModel.getFollowRequests()

        binding.header.titleTV.text = getString(R.string.COMMON_FOLLOW_REQUESTS)

        /**
         * Navigation
         */

        binding.header.backIB.setOnClickListener {
            findNavController().navigateUp()
        }


    }

    private fun bindObservers() {

        userViewModel.getFollowRequestsLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {

                        adapter.setItems(it.data!!.result)

                        if (it.data.result.size != 0){
                            binding.noFollowRequestTV.visibility=View.INVISIBLE
                        }else{
                            binding.noFollowRequestTV.visibility=View.VISIBLE
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

        userViewModel.postUserAcceptFollowRequestLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        adapter.removeItem(itemPosition)

                        toast("Confirmação com sucesso")
                        if (adapter.itemCount == 0)
                            findNavController().navigateUp()

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

        userViewModel.deleteFollowRequestLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        toast("Confirmação removida com sucesso")
                        adapter.removeItem(itemPosition)
                        //Get FollowRequests
                        //authViewModel.getFollowRequests()
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



}