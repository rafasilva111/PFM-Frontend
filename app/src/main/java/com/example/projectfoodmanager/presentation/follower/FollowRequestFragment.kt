package com.example.projectfoodmanager.presentation.follower

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.user.User
import com.example.projectfoodmanager.databinding.FragmentFollowRequestBinding
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.example.projectfoodmanager.viewmodels.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FollowRequestFragment : Fragment() {

    // binding
    lateinit var binding: FragmentFollowRequestBinding

    // viewModels
    private val authViewModel by activityViewModels<AuthViewModel>()
    private val notificationViewModel by activityViewModels<NotificationViewModel>()

    // constants
    private lateinit var currentUser: User
    private var itemPosition: Int = -1


    @Inject
    lateinit var sharedPreference: SharedPreference

    private val adapter by lazy {
        FollowerListingAdaptar(

            FollowType.NOT_FOLLOWER,
            onItemClicked = { userID ->
                findNavController().navigate(R.id.action_followerFragment_to_profileBottomSheetDialog,Bundle().apply {
                    putInt("userID",userID)
                })
            },
            onActionBTNClicked = { userId ->
                authViewModel.postAcceptFollowRequest(userId)
            },
            onRemoveBTNClicked = { _, userId ->
                authViewModel.deleteFollowRequest(FollowType.NOT_FOLLOWER,userId)

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

        authViewModel.postUserAcceptFollowRequestLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        toast("Confirmação com sucesso")
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

        authViewModel.deleteUserFollowRequestLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
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

    override fun onResume() {
        val window = requireActivity().window

        //BACKGROUND in NAVIGATION BAR
        window.statusBarColor = requireContext().getColor(R.color.background_1)
        window.navigationBarColor = requireContext().getColor(R.color.background_1)

        //TextColor in NAVIGATION BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance( WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
            window.insetsController?.setSystemBarsAppearance( WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS, WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS)
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = 0
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        super.onResume()
    }
}