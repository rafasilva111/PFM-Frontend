package com.example.projectfoodmanager.presentation.follower

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FollowerFragment : Fragment() {

    // binding
    lateinit var binding: FragmentFollowerBinding
    private var itemPosition: Int = -1
    private val authViewModel by activityViewModels<AuthViewModel>()
    private var userId: Int = -1
    private var userName: String? = null
    private var followType: Int = -1
    private lateinit var currentUser: User

    @Inject
    lateinit var sharedPreference: SharedPreference
    private val adapter by lazy {
        FollowerListingAdaptar(
            followType,
            onItemClicked = {user_id ->
                val bundle=Bundle()
                if (currentUser.id==user_id){
                    bundle.putInt("userID",-1)
                }else{
                    bundle.putInt("userID",user_id)
                }

                findNavController().navigate(R.id.action_followerFragment_to_profileBottomSheetDialog,bundle)

            },
            onActionBTNClicked = { user_Id ->
                authViewModel.postFollowRequest(userId)
            },
            onRemoveBTNClicked = { postion,user_Id ->

                itemPosition = postion
                val title:String
                val message:String

                if (followType==FollowType.FOLLOWERS) {
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
                    .setPositiveButton("Sim") { dialog, which ->
                        // Remove Follower or Followed
                        authViewModel.deleteFollowRequest(followType,user_Id)

                    }
                    .setNegativeButton("Não") { dialog, which ->
                        // Close Dialog
                        dialog.dismiss()
                    }
                    .show()

            }
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
            if(currentUser.id==userId || userId==-1) {
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

        authViewModel.postUserFollowRequestLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        toast("Pedido enviado com sucesso")
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
                        toast("Removido com sucesso")
                        adapter.removeItem(itemPosition)
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