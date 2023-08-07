package com.example.projectfoodmanager.presentation.follower

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.FragmentFollowRequestBinding
import com.example.projectfoodmanager.databinding.FragmentFollowerBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FollowRequestFragment : Fragment() {
    // binding
    lateinit var binding: FragmentFollowRequestBinding
    private val authViewModel by activityViewModels<AuthViewModel>()
    private lateinit var currentUser: User

    @Inject
    lateinit var sharedPreference: SharedPreference

    private val adapter by lazy {
        FollowerListingAdaptar(
            FollowType.NOT_FOLLOWER
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (!this::binding.isInitialized) {
            binding = FragmentFollowRequestBinding.inflate(layoutInflater)

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

        //Get FollowRequests
        authViewModel.getFollowRequests()

        binding.backRegIB.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun bindObservers() {

        authViewModel.getUserFollowRequestsLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
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
    }


}