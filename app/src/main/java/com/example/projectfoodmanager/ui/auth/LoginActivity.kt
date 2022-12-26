package com.example.projectfoodmanager.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.ui.auth.AuthViewModel
import com.example.projectfoodmanager.util.MetadataConstants
import com.example.projectfoodmanager.util.UiState
import com.example.projectfoodmanager.util.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel.getMetadata{
            var metadata = it?.get(MetadataConstants.FIRST_TIME_LOGIN) ?: null

            if (metadata != null){
                setContentView(R.layout.activity_login)
            }
            else{
                setContentView(R.layout.first_time_welcoming)
                findViewById<Button>(R.id.btn_continue).setOnClickListener {
                    setContentView(R.layout.activity_login)
                }
                authViewModel.storeMetadata(MetadataConstants.FIRST_TIME_LOGIN,true.toString()){
                }
            }

        }


    }
}

