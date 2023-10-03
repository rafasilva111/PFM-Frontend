package com.example.projectfoodmanager

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.projectfoodmanager.data.model.modelResponse.notifications.NotificationData
import com.example.projectfoodmanager.data.model.modelResponse.notifications.PushNotification
import com.example.projectfoodmanager.databinding.FragmentBlankBinding
import com.example.projectfoodmanager.util.toast
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.example.projectfoodmanager.viewmodels.NotificationViewModel
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint


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

        ): View {
        binding = FragmentBlankBinding.inflate(layoutInflater)

        binding.btnBlankId.setOnClickListener {



            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                Log.d(TAG, "push: $token")

                notificationViewModel
                    .sendNotification(
                        PushNotification(
                            NotificationData("FCM Notification","this notification from android"),
                            "fFBChT9qSYuSKAkhgFJ93y:APA91bGcwrG-jQbc8OmVUYNYQJAm63mi6looFRNy_1Ew1yQD8F8pGFWeFMsMdsuYsldhf4BwOTkp2k8trFpxypxdd-kV4Rufjh6XuR6p-cFxn2R7HHnN__HQd6G3uiHICnWtElazGHYe"
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
        setUI()
    }

    private fun setUI() {

    }


}