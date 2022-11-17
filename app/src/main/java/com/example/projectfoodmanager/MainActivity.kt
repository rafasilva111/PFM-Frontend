package com.example.projectfoodmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

import com.example.projectfoodmanager.views.ReceitasViewActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        //TODO

        //check internet


        //send login

        val openRecipes = findViewById<Button>(R.id.button2)
        openRecipes.setOnClickListener {
            startActivity(Intent(this,ReceitasViewActivity::class.java))
        }


    }

}