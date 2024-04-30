package com.example.projectfoodmanager



import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.projectfoodmanager.databinding.ActivityMainBinding
import com.example.projectfoodmanager.util.Helper.Companion.MENU_VISIBILITY
import com.example.projectfoodmanager.util.NetworkConnectivity
import com.example.projectfoodmanager.viewmodels.UserViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var bottomNav: BottomNavigationView
    lateinit var navController: NavController
    val TAG: String = "MainActivity"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val networkConnectivityObserver = NetworkConnectivity(applicationContext)
        networkConnectivityObserver.observe(this){
            if (it){
                println("Connected")
                internetConnection = true
            }
            else{
                println("Not connected")
                internetConnection = false
            }
        }




        //window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startUI()
    }



    private fun startUI() {
        bottomNav = findViewById(R.id.bottomNavigationView)
        navController = (supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment).navController
        bottomNav.setupWithNavController(navController)

    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onResume() {

        MENU_VISIBILITY = this.findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility == View.VISIBLE

        super.onResume()
    }

    companion object{
        var internetConnection = true
    }
}