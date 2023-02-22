package com.example.projectfoodmanager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.data.util.Resource
import com.example.projectfoodmanager.presentation.viewmodels.AuthViewModel
import com.example.projectfoodmanager.util.SPLASH_TIME
import com.example.projectfoodmanager.util.hide
import com.example.projectfoodmanager.util.show
import com.example.projectfoodmanager.util.toast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SplashFragment : Fragment() {


    val authViewModel: AuthViewModel by viewModels()
    val TAG: String = "SplashFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel.getUserSession()
        observer()
    }

    private fun onBoardingFinished():Boolean{
        val sharedPref = requireActivity().getSharedPreferences("onBoarding",Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished",false)
    }


    fun observer(){
        authViewModel.user.observe(viewLifecycleOwner) { response ->
            when(response){
                is Resource.Loading -> {
                   // Log.i(TAG,"Loading...")
                }
                is Resource.Success -> {
                    Handler().postDelayed({
                        findNavController().navigate(R.id.action_splashFragment_to_app_navigation)
                    }, SPLASH_TIME)

                }
                is Resource.Error -> {
                    Log.i(TAG,"No user previously logged out.")
                    Log.i(TAG,"${response.message}")

                    Handler().postDelayed({
                        if(onBoardingFinished()){
                            findNavController().navigate(R.id.action_splashFragment_to_homeFragment)

                        }else{
                            findNavController().navigate(R.id.action_splashFragment_to_viewPagerFragment)
                        }
                    },SPLASH_TIME)
                }
                else -> {}
            }
        }
    }


}