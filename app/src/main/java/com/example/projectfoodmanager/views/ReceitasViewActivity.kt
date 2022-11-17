package com.example.projectfoodmanager.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.projectfoodmanager.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReceitasViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receitas_view)
    }
}