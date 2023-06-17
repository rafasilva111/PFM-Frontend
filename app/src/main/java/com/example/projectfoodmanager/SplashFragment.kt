package com.example.projectfoodmanager

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.example.projectfoodmanager.util.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@Suppress("DEPRECATION")
@AndroidEntryPoint
class SplashFragment : Fragment() {

    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var sharedPreference: SharedPreference

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
                        if (it.data != null) {
                            sharedPreference.saveUserSession(it.data)
                            findNavController().navigate(R.id.action_splashFragment_to_app_navigation)
                            toast(getString(R.string.welcome))
                        }
                        else{
                            Log.d(TAG, "userResponseLiveData Observer: Something went wrong")
                        }
                    }
                    is NetworkResult.Error -> {
                        findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                        tokenManager.deleteToken()
                    }
                    is NetworkResult.Loading -> {
                    }
                }
            }
        })
    }

    override fun onResume() {
        requireActivity().window.decorView.systemUiVisibility = 0
        requireActivity().window.statusBarColor =  requireContext().getColor(R.color.main_color)
        super.onResume()
    }

    override fun onPause() {
        val mainActivity = activity as? MainActivity
        requireActivity().window.decorView.systemUiVisibility = 8192
        requireActivity().window.statusBarColor =  requireContext().getColor(R.color.background_1)
        super.onPause()
    }
}