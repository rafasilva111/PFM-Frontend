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
import com.example.projectfoodmanager.presentation.calender.utils.CalenderUtils.Companion.selectedDate
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.LocalTime

@AndroidEntryPoint
class WeeklyFragment : Fragment() {

    lateinit var binding: FragmentWeeklyBinding

    private val adapter by lazy {
        CalendarAdapter(
            daysInWeekArray(
                selectedDate
            ),
            onItemClicked = {text ->

            }
        )
    }

    private val recipeAdapter by lazy{
        val teste = arrayListOf<EventTrash>()
        teste.add(EventTrash("curti", LocalTime.now(), LocalDate.now()))
        TrashEventAdapter(
            teste
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
            selectedDate = selectedDate.plusWeeks(1)
            updateMonthView()
        }

        binding.nextBtn.setOnClickListener{
            selectedDate = selectedDate.plusWeeks(1)
            updateMonthView()
        }

        binding.addRecipeBtn.setOnClickListener{
            findNavController().navigate(R.id.action_weeklyFragment_to_eventEditTrashFragment)
        }

        val manager = LinearLayoutManager(activity)
        binding.recipesRV.layoutManager = manager

        val teste = arrayListOf<EventTrash>()
        teste.add(EventTrash("curti", LocalTime.now(), LocalDate.now()))

        recipeAdapter.updateList(teste)
    }

    private fun setWeeklyView() {
        selectedDate = LocalDate.now()
        binding.monthYearTV.text = CalenderUtils.formatDateMonthYear(selectedDate)

        val layoutManager: RecyclerView.LayoutManager =
            GridLayoutManager(activity?.applicationContext, 7)
        binding.calendarRecyclerView.layoutManager = layoutManager
        binding.calendarRecyclerView.adapter = adapter
        setEventAdapter()
    }


    private fun updateMonthView() {

        binding.monthYearTV.text = CalenderUtils.formatDateMonthYear(selectedDate)
        adapter.updateList(
                daysInWeekArray(
                selectedDate
            )
        )
    }

    override fun onResume() {
        super.onResume()
        setEventAdapter()
    }

    private fun setEventAdapter() {
        binding.recipesRV.adapter = recipeAdapter
        val teste = EventTrash.eventsForDate(selectedDate)
        teste.add(EventTrash("curti", LocalTime.now(), LocalDate.now()))
        recipeAdapter.updateList(EventTrash.eventsForDate(selectedDate))
    }
}