package com.example.projectfoodmanager.presentation.goals

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.example.projectfoodmanager.databinding.FragmentGoalsBinding
import com.example.projectfoodmanager.presentation.viewmodels.AuthViewModel

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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (isOnline(requireContext())){
            //está online

            //check for user bio data
            authViewModel.getUserSession{
                if(it != null){
                    if (it.height != "" && it.weight != "" && it.sex != "" && it.idade != ""){

                    }
                    else{
                        // call bio data form
                    }
                }
            }




        }
        else{
            //está offline
            val popUpShow = PopUpFragment()
            popUpShow.show((activity as AppCompatActivity).supportFragmentManager,"showUpFrgament")
            binding.offlineText.visibility = View.VISIBLE
        }
        super.onViewCreated(view, savedInstanceState)
    }



    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if ( connectivityManager != null) {
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
        }
        return false
    }
}