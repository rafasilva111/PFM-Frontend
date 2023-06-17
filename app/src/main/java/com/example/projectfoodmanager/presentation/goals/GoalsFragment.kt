package com.example.projectfoodmanager.presentation.goals

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.databinding.FragmentGoalsBinding
import com.example.projectfoodmanager.util.LOGIN_TIME
import com.example.projectfoodmanager.util.NetworkResult
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GoalsFragment : Fragment() {
    lateinit var binding: FragmentGoalsBinding
    val authViewModel: AuthViewModel by viewModels()
    val TAG: String = "ProfileFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View? {
        binding = FragmentGoalsBinding.inflate(layoutInflater)
        changeVisibilityMenu(state = true)


        bindObservers()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (isOnline(requireContext())){
            //está online


            //check for user bio data
            authViewModel.getUserSession()




        }
        else{
            setOfflineText(state = true)
            //está offline
            val popUpShow = PopUpFragment()
            popUpShow.show((activity as AppCompatActivity).supportFragmentManager,"showUpFrgament")
            //binding.offlineText.visibility = View.VISIBLE
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setOfflineText(state: Boolean) {
        if (state)
            binding.offlineText.visibility = View.VISIBLE
        else
            binding.offlineText.visibility = View.GONE
    }




    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }
    private fun changeVisibilityMenu(state : Boolean){
        val menu = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        if(state){
            menu!!.visibility=View.VISIBLE
        }else{
            menu!!.visibility=View.GONE
        }
    }

    private fun bindObservers() {


        authViewModel.userResponseLiveData.observe(viewLifecycleOwner, Observer { userSessionResponse ->
            userSessionResponse.getContentIfNotHandled()?.let{

                when (it) {
                    is NetworkResult.Success -> {
                        if (!(it.data!!.weight != 0.0 && it.data.height != 0.0 && it.data.activity_level != 1.0)){
                            val dialogBinding : View = layoutInflater.inflate(R.layout.dialog_goals_biodata_confirmation_from_user, null);

                            val myDialog = Dialog(requireContext())
                            myDialog.setContentView(dialogBinding)

                            // create alert dialog
                            myDialog.setCancelable(false)
                            myDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                            val yesBtn = dialogBinding.findViewById<Button>(R.id.btn_conf_Yes)
                            val cancelBtn = dialogBinding.findViewById<Button>(R.id.btn_conf_cancel)

                            yesBtn.setOnClickListener {
                                findNavController().navigate(R.id.action_goalsFragment_to_updateBiodata)
                                myDialog.dismiss()
                            }

                            cancelBtn.setOnClickListener {
                                findNavController().navigateUp()
                                myDialog.dismiss()

                            }

                            myDialog.show()
                        }
                    }
                    is NetworkResult.Error -> {

                    }
                    is NetworkResult.Loading -> {
                        // show loading bar

                    }
                }
            }
        })
    }
}