package com.example.projectfoodmanager


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.projectfoodmanager.databinding.ActivityMainBinding
import com.example.projectfoodmanager.ui.auth.AuthViewModel
import com.example.projectfoodmanager.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var bottomNav: BottomNavigationView
    lateinit var navController: NavController
    val authViewModel: AuthViewModel by viewModels()
    val TAG: String = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        //todo check internet
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authViewModel.getSession { user->
            if (user == null) {
                authViewModel.getMetadata {
                    var metadata = it?.get(MetadataConstants.FIRST_TIME_LOGIN)

                    if (metadata != null) {
                        setContentView(R.layout.activity_login)
                    } else {

                        setContentView(R.layout.first_time_welcoming)
                        findViewById<Button>(R.id.btn_continue).setOnClickListener {
                            setContentView(R.layout.activity_login)
                        }
                        authViewModel.storeMetadata(
                            MetadataConstants.FIRST_TIME_LOGIN,
                            true.toString()
                        ) {
                        }
                    }

                }
            }
            else{
                startUI()
            }
        }

    }


    private fun startUI() {
        bottomNav = findViewById(R.id.bottomNavigationView)
        bottomNav.visibility = View.VISIBLE
        //nav
        navController = findNavController(R.id.nav_host)

        bottomNav.setupWithNavController(navController)

    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount

        if (navController?.currentDestination?.id == R.id.loginFragment){
            bottomNav.visibility = View.GONE
            moveTaskToBack(true)
        }
        if (count == 0) {
            super.onBackPressed()
            //additional code
        } else {
            supportFragmentManager.popBackStack()
        }

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
    fun showHideTextView(visible: Int) {
        binding.bottomNavigationView.visibility = visible
    }
}