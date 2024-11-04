package com.example.projectfoodmanager



import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.projectfoodmanager.databinding.ActivityMainBinding
import com.example.projectfoodmanager.util.Helper.Companion.MENU_VISIBILITY
import com.example.projectfoodmanager.util.network.NetworkConnectivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var navController: NavController
    private val TAG: String = "MainActivity"

    // Variable to track the last selected item
    private var lastSelectedItemId: Int = 0



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

        // Handle item reselect events
        bottomNav.setOnItemReselectedListener { item ->
            // Prevent reelecting the same item
            if (item.itemId == lastSelectedItemId) {
                return@setOnItemReselectedListener
            }
        }

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