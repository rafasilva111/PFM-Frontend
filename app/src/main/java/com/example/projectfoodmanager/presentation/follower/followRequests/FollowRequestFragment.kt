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
import com.example.projectfoodmanager.util.NetworkResult
import com.example.projectfoodmanager.util.SharedPreference
import com.example.projectfoodmanager.util.ToastType
import com.example.projectfoodmanager.util.toast
import com.example.projectfoodmanager.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FollowRequestFragment : Fragment() {

    // binding
    lateinit var binding: FragmentFollowRequestBinding

    // viewModels
    private val userViewModel by activityViewModels<UserViewModel>()

    // constants
    private lateinit var currentUser: User
    private var itemPosition: Int = -1


    @Inject
    lateinit var sharedPreference: SharedPreference

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

    private fun setUI() {

        /**
         * General
         */

        currentUser = sharedPreference.getUserSession()

        userViewModel.getFollowRequests()

        /**
         * Navigation
         */

        binding.backRegIB.setOnClickListener {
            findNavController().navigateUp()
        }


    }


    private fun bindObservers() {

        userViewModel.getFollowRequestsLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {

                        adapter.updateList(it.data!!.result)

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
                        if (adapter.getList().isEmpty())
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


    override fun onStart() {
        setUI()
        super.onStart()
    }
}