package com.example.projectfoodmanager.presentation.auth

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.R
import androidx.lifecycle.Observer
import com.example.projectfoodmanager.databinding.FragmentLoginBinding
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.example.projectfoodmanager.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment : Fragment() {

    val TAG: String = "LoginFragment"
    lateinit var binding: FragmentLoginBinding
    private val authViewModel by activityViewModels<AuthViewModel>()

    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var sharedPreference: SharedPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //activity?.theme?.applyStyle(R.style.Teste22,true)


        binding.progressBar.hide()

        bindObservers()
        binding.loginBtn.setOnClickListener {
            if (validation()) {
                authViewModel.loginUser(binding.emailEt.text.toString().trim(),binding.passEt.text.toString().trim())
            }
        }


        binding.forgotPassLabel.setOnClickListener {

        }
        binding.registerLabel.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    fun validation(): Boolean {
        var isValid = true

        if (binding.emailEt.text.isNullOrEmpty()){
            isValid = false
            binding.emailTL.isErrorEnabled=true
            binding.emailTL.error=getString(R.string.enter_email)
            //toast(getString(R.string.enter_email))
        }else if (!binding.emailEt.text.toString().isValidEmail()){
            isValid = false
            binding.emailTL.isErrorEnabled=true
            binding.emailTL.error=getString(R.string.invalid_email)
            //toast(getString(R.string.invalid_email))

        }else{
            binding.emailTL.isErrorEnabled=false
        }

        if (binding.passEt.text.isNullOrEmpty()){
            isValid = false
            binding.passwordTL.isErrorEnabled=true
            binding.passwordTL.error=getString(R.string.enter_password)
            //toast(getString(R.string.enter_password))
        }else if (binding.passEt.text.toString().length < 8){
            isValid = false
            binding.passwordTL.isErrorEnabled=true
            binding.passwordTL.error=getString(R.string.invalid_password)
            //toast(getString(R.string.invalid_password))
        }else{
            binding.passwordTL.isErrorEnabled=false
        }
        return isValid
    }



    private fun changeVisib_Menu(state : Boolean){
        val menu = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        if(state){
            menu!!.visibility=View.VISIBLE
        }else{
            menu!!.visibility=View.GONE
        }
    }

    private fun showValidationErrors(error: String) {
        Toast(context).showCustomToast(error,requireActivity(),ToastType.ERROR)
       // toast(error)
    }



    private fun setButtonVisibility(visibility: Boolean) {
        if (visibility){
            binding.loginBtn.isVisible = true
            binding.progressBar.isVisible = false
        }
        else{
            binding.loginBtn.isVisible = false
            binding.progressBar.isVisible = true
        }

    }

    private fun bindObservers() {
        authViewModel.userAuthResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let{

                when (it) {
                    is NetworkResult.Success -> {
                        tokenManager.saveToken(it.data!!.token)
                        authViewModel.getUserSession()
                    }
                    is NetworkResult.Error -> {
                        binding.loginBtn.isVisible = true
                        binding.progressBar.isVisible = false
                        showValidationErrors(it.message.toString())
                        setButtonVisibility(visibility = true)
                    }
                    is NetworkResult.Loading -> {
                        setButtonVisibility(visibility = false)
                    }
                }
            }
        })

        authViewModel.userResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let{

                when (it) {
                    is NetworkResult.Success -> {

                        /*Handler().postDelayed({
                            setButtonVisibility(visibility = true)
                                findNavController().navigate(R.id.action_loginFragment_to_home_navigation)

                        }, LOGIN_TIME)*/

                        setButtonVisibility(visibility = true)
                        findNavController().navigate(R.id.action_loginFragment_to_home_navigation)
                    }
                    is NetworkResult.Error -> {
                        binding.loginBtn.isVisible = true
                        binding.progressBar.isVisible = false
                        showValidationErrors(it.message.toString())
                        setButtonVisibility(visibility = true)

                    }
                    is NetworkResult.Loading -> {

                    }
                }
            }
        })
    }


    override fun onResume() {

        // decaprecated after api 30
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.insetsController?.let { controller ->
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                controller.hide(WindowInsets.Type.systemBars())
            }
        } else {
            @Suppress("DEPRECATION")
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        requireActivity().window.statusBarColor =  requireContext().getColor(R.color.main_color)
        changeVisib_Menu(false)

        super.onResume()
    }

    override fun onPause() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val window = requireActivity().window
            val controller = window.insetsController
            if (controller != null) {
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                window.setDecorFitsSystemWindows(false)
            }
        } else {
            @Suppress("DEPRECATION")
            requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
        requireActivity().window.statusBarColor =  requireContext().getColor(R.color.background_1)

        super.onPause()
    }
}