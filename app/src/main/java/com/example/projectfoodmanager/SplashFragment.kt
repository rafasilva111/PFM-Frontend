package com.example.projectfoodmanager

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.presentation.viewmodels.AuthViewModel
import com.example.projectfoodmanager.util.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SplashFragment : Fragment() {

    @Inject
    lateinit var tokenManager: TokenManager

    private val authViewModel by activityViewModels<AuthViewModel>()
    val TAG: String = "SplashFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Handler().postDelayed({
            if(onBoardingFinished()){
                if (tokenManager.getToken()!=null){
                    authViewModel.getUserSession()
                }
                else{
                    findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                }


            }else{
                findNavController().navigate(R.id.action_splashFragment_to_viewPagerFragment)
            }
        },SPLASH_TIME)

        authViewModel.userAuthResponseLiveData



        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindObservers()

    }

    private fun onBoardingFinished():Boolean{
        val sharedPref = requireActivity().getSharedPreferences("onBoarding",Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished",false)
    }

    private fun showValidationErrors(error: String) {
        toast(error)
    }

    private fun bindObservers() {
        authViewModel.userResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let{
                when (it) {
                    is NetworkResult.Success -> {
                        findNavController().navigate(R.id.action_splashFragment_to_app_navigation)
                        toast(getString(R.string.welcome))
                    }
                    is NetworkResult.Error -> {
                        showValidationErrors(it.message.toString())
                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        })
    }

}