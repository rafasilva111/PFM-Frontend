package com.example.projectfoodmanager.presentation.calender.ingredients

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.projectfoodmanager.databinding.FragmentCalenderIngredientsBinding
import com.example.projectfoodmanager.viewmodels.CalenderViewModel
import java.time.LocalDateTime


class CalenderIngredientsFragment : Fragment() {

    lateinit var binding: FragmentCalenderIngredientsBinding

    private val calenderViewModel by activityViewModels<CalenderViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCalenderIngredientsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fromDate = LocalDateTime.now()
        val toDate = fromDate.plusDays(15)
        //calenderViewModel.getCalenderIngredients(fromDate,toDate)

        //val itemsAdapter = context?.let { CalenderIngredientsAdapter(it, items) }

        //binding.ingridientsLV.adapter = itemsAdapter
    }
}