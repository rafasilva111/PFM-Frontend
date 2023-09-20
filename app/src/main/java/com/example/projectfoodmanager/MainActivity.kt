package com.example.projectfoodmanager



import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.projectfoodmanager.databinding.ActivityMainBinding
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
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

        FirebaseMessaging.getInstance().subscribeToTopic("/topics/myTopic")
        //window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startUI()
    }



    private fun startUI() {
        bottomNav = findViewById(R.id.bottomNavigationView)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        navController = navHostFragment.navController
        bottomNav.setupWithNavController(navController)

    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


    override fun onResume() {
        super.onResume()
        window.decorView.systemUiVisibility = 8192
        window.navigationBarColor = this.getColor(R.color.main_color)
        window.statusBarColor =  this.getColor(R.color.background_1)
    }
}