package com.example.projectfoodmanager.presentation.calender

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.databinding.FragmentCalenderBinding
import com.example.projectfoodmanager.presentation.calender.utils.CalenderUtils.Companion.daysInMonthArray
import com.example.projectfoodmanager.presentation.calender.utils.CalenderUtils.Companion.formatDateMonthYear
import com.example.projectfoodmanager.presentation.calender.utils.CalenderUtils.Companion.selectedDate
import com.example.projectfoodmanager.util.toast
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate


@AndroidEntryPoint
class CalenderFragment : Fragment() {
    lateinit var binding: FragmentCalenderBinding
    val authViewModel: AuthViewModel by viewModels()
    val TAG: String = "ProfileFragment"

    private val adapter by lazy {
            CalendarAdapter(
                daysInMonthArray(
                    selectedDate
            ),
            onItemClicked = {text ->
                toast(text)
            }
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View? {
        binding = FragmentCalenderBinding.inflate(layoutInflater)
        setMonthView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.weeklyViewBtn.setOnClickListener {
            findNavController().navigate(R.id.action_calenderFragment_to_weeklyFragment)
        }

        binding.previousBtn.setOnClickListener{
            selectedDate = selectedDate.minusMonths(1)
            updateMonthView()
        }

        binding.nextBtn.setOnClickListener{
            selectedDate = selectedDate.plusMonths(1)
            updateMonthView()
        }

    }

    private fun setMonthView() {
        selectedDate = LocalDate.now()
        binding.monthYearTV.text = formatDateMonthYear(selectedDate)

        val layoutManager: RecyclerView.LayoutManager =
            GridLayoutManager(activity?.applicationContext, 7)
        binding.calendarRecyclerView.layoutManager = layoutManager
        binding.calendarRecyclerView.adapter = adapter
    }

    private fun updateMonthView() {

        binding.monthYearTV.text = formatDateMonthYear(selectedDate)
        adapter.updateList(daysInMonthArray(
            selectedDate
        ))
    }
}