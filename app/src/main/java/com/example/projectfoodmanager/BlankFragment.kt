package com.example.projectfoodmanager
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
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.notifications.NotificationData
import com.example.projectfoodmanager.data.model.modelResponse.notifications.PushNotification
import com.example.projectfoodmanager.databinding.FragmentBlankBinding
import com.example.projectfoodmanager.databinding.FragmentGoalsBinding
import com.example.projectfoodmanager.di.RetrofitInstance
import com.example.projectfoodmanager.util.LOGIN_TIME
import com.example.projectfoodmanager.util.NetworkResult
import com.example.projectfoodmanager.util.toast
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.example.projectfoodmanager.viewmodels.NotificationViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class BlankFragment : Fragment() {
    lateinit var binding: FragmentBlankBinding
    val authViewModel: AuthViewModel by viewModels()
    private val notificationViewModel: NotificationViewModel by viewModels()
    val TAG: String = "ProfileFragment"

    val topic = "/topics/myTopic"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View? {
        binding = FragmentBlankBinding.inflate(layoutInflater)

        binding.btnBlankId.setOnClickListener {



            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                Log.d(TAG, "push: $token")

                notificationViewModel
                    .sendNotification(
                        PushNotification(
                            NotificationData("FCM Notification","this notification from android"),
                            token
                        )
                    )
            }

        }


        notificationViewModel.connectionError.observe(viewLifecycleOwner){
            when(it){
                "sending"-> {
                    toast("sending notification")
                }
                "sent"-> {
                    toast("sending notification")
                }
                "error while sending"-> {
                    toast("sending notification")
                }
            }
        }

        notificationViewModel.response.observe(viewLifecycleOwner){
            if (it.isNotEmpty())
                Log.d(TAG, "Notification in Kotlin: $it ")
        }


        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}