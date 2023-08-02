package com.example.projectfoodmanager.presentation.calender

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.databinding.FragmentWeeklyBinding
import com.example.projectfoodmanager.presentation.calender.utils.CalenderUtils
import com.example.projectfoodmanager.presentation.calender.utils.CalenderUtils.Companion.daysInWeekArray
import com.example.projectfoodmanager.presentation.calender.utils.CalenderUtils.Companion.currentDate
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class WeeklyFragment : Fragment() {

    lateinit var binding: FragmentWeeklyBinding

    private val adapter by lazy {
        CalendarAdapter(
            daysInWeekArray(
                currentDate
            ),
            onItemClicked = {text ->

            }
        )
    }


    private val recipeCalenderAdapter by lazy{
        RecipeCalenderAdapter(
            daysInWeekArray(
                currentDate
            ),
            onItemClicked = {text ->

            }
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeeklyBinding.inflate(layoutInflater)

        setWeeklyView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.previousBtn.setOnClickListener{
            currentDate = currentDate.minusWeeks(1)
            updateMonthView()
        }

        binding.nextBtn.setOnClickListener{
            currentDate = currentDate.plusWeeks(1)
            updateMonthView()
        }


    }

    private fun setWeeklyView() {
        currentDate = LocalDate.now()
        binding.monthYearTV.text = CalenderUtils.formatDateMonthYear(currentDate)

        val layoutManager: RecyclerView.LayoutManager =
            GridLayoutManager(activity?.applicationContext, 7)
        binding.calendarRecyclerView.layoutManager = layoutManager
        binding.calendarRecyclerView.adapter = adapter
        //setEventAdapter()
    }


    private fun updateMonthView() {

        binding.monthYearTV.text = CalenderUtils.formatDateMonthYear(currentDate)
        adapter.updateList(
                daysInWeekArray(
                currentDate
            )
        )
    }

    override fun onResume() {
        super.onResume()
        //setEventAdapter()
    }

    private fun setEventAdapter() {
        currentDate = LocalDate.now()
        binding.monthYearTV.text = CalenderUtils.formatDateMonthYear(currentDate)

        val layoutManager: RecyclerView.LayoutManager =LinearLayoutManager(activity?.applicationContext)
        binding.recipesRV.layoutManager = layoutManager
        binding.recipesRV.adapter = recipeCalenderAdapter
    }
}