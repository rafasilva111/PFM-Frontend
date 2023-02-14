package com.example.projectfoodmanager.ui.recipe

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.projectfoodmanager.R

class CreateRecepiFragment : Fragment() {

    companion object {
        fun newInstance() = CreateRecepiFragment()
    }

    private lateinit var viewModel: CreateRecepiViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_recepi, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CreateRecepiViewModel::class.java)
        // TODO: Use the ViewModel
    }

}