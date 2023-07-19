package com.example.projectfoodmanager.presentation.calender.insertCalenderEntry

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.databinding.FragmentNewCalenderEntryBinding
import com.example.projectfoodmanager.util.NetworkResult
import com.example.projectfoodmanager.util.SharedPreference
import com.example.projectfoodmanager.util.TokenManager
import com.example.projectfoodmanager.util.toast
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NewCalenderEntryFragment : Fragment() {

    @Inject
    lateinit var sharedPreference: SharedPreference
    @Inject
    lateinit var tokenManager: TokenManager

    private val authViewModel: AuthViewModel by viewModels()


    lateinit var binding: FragmentNewCalenderEntryBinding
    private var snapHelper: SnapHelper = PagerSnapHelper()
    lateinit var manager: LinearLayoutManager

    val TAG: String = "NewCalenderEntryFragment"


    private val adapter by lazy {
        FavoritesRecipeCalenderListingAdapter(
            onItemClicked = { _, item ->
                findNavController().navigate(R.id.action_newCalenderEntryFragment_to_receitaDetailFragment,Bundle().apply {
                    putParcelable("Recipe",item)
                })
            }
        )
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bindObservers()

        //todo check for internet connection
        if (this::binding.isInitialized) {
            return binding.root
        } else {
            binding = FragmentNewCalenderEntryBinding.inflate(layoutInflater)
            manager = LinearLayoutManager(activity)
            manager.orientation = LinearLayoutManager.HORIZONTAL
            manager.reverseLayout = false
            binding.favoritesRV.layoutManager = manager
            snapHelper.attachToRecyclerView(binding.favoritesRV)


            //setRecyclerViewScrollListener()

            return binding.root
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //bindObservers()

        binding.favoritesRV.adapter = adapter

        binding.CloseRegIB.setOnClickListener {
            findNavController().navigateUp()
        }

        //valida shared preferences
        try {
            val user = sharedPreference.getUserSession()
            adapter.updateList(user.saved_recipes.toMutableList(), user)

        } catch (e: Exception) {
            Log.d(TAG, "onViewCreated: User had no shared prefences...")
            // se não tiver shared preferences o user não tem sessão válida
            //tera um comportamento diferente offilne
            authViewModel.logoutUser()
        }


        binding.tagCV.setOnClickListener {

        }

        binding.tagCV.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Apply visual feedback when the card is touched (e.g., change background color, apply elevation effect)
                    binding.tagCV.setCardBackgroundColor(Color.parseColor("#E3E3E4"))
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Remove visual feedback when the touch is released or cancelled
                    binding.tagCV.setCardBackgroundColor(Color.WHITE)
                }
            }
            false
        }

        binding.dateCV.setOnClickListener {

        }

        binding.dateCV.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Apply visual feedback when the card is touched (e.g., change background color, apply elevation effect)
                    binding.dateCV.setCardBackgroundColor(Color.parseColor("#E3E3E4"))
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Remove visual feedback when the touch is released or cancelled
                    binding.dateCV.setCardBackgroundColor(Color.WHITE)
                }
            }
            false
        }

        binding.dateCV.setOnClickListener {

        }

        binding.timeCV.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Apply visual feedback when the card is touched (e.g., change background color, apply elevation effect)
                    binding.timeCV.setCardBackgroundColor(Color.parseColor("#E3E3E4"))
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Remove visual feedback when the touch is released or cancelled
                    binding.timeCV.setCardBackgroundColor(Color.WHITE)
                }
            }
            false
        }
    }


    private fun bindObservers() {

        authViewModel.userLogoutResponseLiveData.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {
                        tokenManager.deleteToken()
                        sharedPreference.deleteUserSession()
                        toast(getString(R.string.user_had_no_shared_preferences))
                        findNavController().navigate(R.id.action_profile_to_login)
                    }
                    is NetworkResult.Error -> {
                        Log.d(TAG, "bindObservers: ${it.message}.")
                    }
                    is NetworkResult.Loading -> {
                        //binding.progressBar.isVisible = true
                    }
                }
            }
        }

    }



}