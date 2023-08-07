package com.example.projectfoodmanager.presentation.follower

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.FragmentFollowerBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.util.Helper.Companion.formatNameToNameUpper
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FollowerFragment : Fragment() {



    // binding
    lateinit var binding: FragmentFollowerBinding
    private val authViewModel by activityViewModels<AuthViewModel>()
    private var userId: Int = -1
    private var userName: String? = null
    private var followType: Int = -1
    private lateinit var currentUser: User

    @Inject
    lateinit var sharedPreference: SharedPreference

    private val adapter by lazy {
        FollowerListingAdaptar(
            followType
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        arguments?.let {
            userId = it.getInt("userID")
            userName = it.getString("userName")
            followType = it.getInt("followType")
        }

        // Inflate the layout for this fragment
        if (!this::binding.isInitialized) {
            binding = FragmentFollowerBinding.inflate(layoutInflater)

            binding.followerRV.layoutManager = LinearLayoutManager(activity)
            binding.followerRV.adapter = adapter

            bindObservers()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // user data

        currentUser = sharedPreference.getUserSession()

        binding.nameProfileTV.text= formatNameToNameUpper(userName!!)

        eventClick()

        //authViewModel.getFollowers(id_user = userId)
        binding.backRegIB.setOnClickListener {
            findNavController().navigateUp()
        }


        binding.followersBTN.setOnClickListener {
            followType=FollowType.FOLLOWERS
            eventClick()

        }

        binding.followedsBTN.setOnClickListener {
            followType=FollowType.FOLLOWEDS
            eventClick()
        }

        binding.requestFollowCV.setOnClickListener {

            findNavController().navigate(R.id.action_followerFragment_to_followRequestFragment)
        }

    }

    private fun eventClick() {

        if (followType==FollowType.FOLLOWERS){
            if(currentUser.id==userId) {
                binding.requestFollowCV.visibility = View.VISIBLE
            }else{
                binding.requestFollowCV.visibility=View.GONE
            }
            binding.followersBTN.setBackgroundResource(R.drawable.selector_tab_button)
            binding.followedsBTN.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
            authViewModel.getFollowers(id_user = userId)
        }else{
            binding.requestFollowCV.visibility=View.GONE
            binding.followedsBTN.setBackgroundResource(R.drawable.selector_tab_button)
            binding.followersBTN.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
            authViewModel.getFolloweds(id_user = userId)
        }

    }

    private fun bindObservers() {
        authViewModel.getUserFollowersLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {

                        adapter.updateList(it.data!!.result)

                        if (it.data.result.size != 0){
                            binding.emptyFollowTV.visibility=View.INVISIBLE
                        }else{
                            binding.emptyFollowTV.visibility=View.VISIBLE
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

        authViewModel.getUserFollowedsLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {

                        adapter.updateList(it.data!!.result)

                        if (it.data.result.size != 0){
                            binding.emptyFollowTV.visibility=View.INVISIBLE
                        }else{
                            binding.emptyFollowTV.visibility=View.VISIBLE
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
    }


}