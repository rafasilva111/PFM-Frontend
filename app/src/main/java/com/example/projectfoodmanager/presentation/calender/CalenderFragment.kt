package com.example.projectfoodmanager.presentation.calender

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.databinding.FragmentCalenderBinding
import com.example.projectfoodmanager.presentation.calender.utils.CalenderUtils
import com.example.projectfoodmanager.presentation.calender.utils.CalenderUtils.Companion.daysInMonthArray
import com.example.projectfoodmanager.presentation.calender.utils.CalenderUtils.Companion.formatDateMonthYear
import com.example.projectfoodmanager.presentation.calender.utils.CalenderUtils.Companion.currentDate
import com.example.projectfoodmanager.util.*
import com.example.projectfoodmanager.viewmodels.AuthViewModel
import com.example.projectfoodmanager.viewmodels.CalenderViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class CalenderFragment : Fragment() {
    lateinit var binding: FragmentCalenderBinding
    val authViewModel: AuthViewModel by viewModels()
    private val calenderViewModel by activityViewModels<CalenderViewModel>()

    val TAG: String = "ProfileFragment"

    private val adapterCalMonth by lazy {
        CalendarAdapter(
            daysInMonthArray(
                currentDate
            ),
            onItemClicked = { selectedDate ->
                calenderViewModel.getEntryOnCalender(selectedDate.atStartOfDay())
            }
        )
    }

    private val adapterCalWeekly by lazy {
        CalendarAdapter(
            CalenderUtils.daysInWeekArray(
                currentDate
            ),
            onItemClicked = { _ ->

            }
        )
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,

        ): View {
        binding = FragmentCalenderBinding.inflate(layoutInflater)
        setMonthView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addRegisterIB.setOnClickListener {
            findNavController().navigate(R.id.action_calenderFragment_to_newCalenderEntryFragment)
            changeVisibilityMenu(false)

        }

        binding.monthViewBtn.setOnClickListener {
            setMonthView()
        }


        binding.weeklyViewBtn.setOnClickListener {
            setWeeklyView()
        }


        binding.previousBtn.setOnClickListener {
            currentDate = currentDate.minusMonths(1)
            updateCalenderView()
        }

        binding.nextBtn.setOnClickListener {
            currentDate = currentDate.plusMonths(1)
            updateCalenderView()
        }

        binding.addBasketIB.setOnClickListener {
            findNavController().navigate(R.id.action_calenderFragment_to_calenderIngredientsFragment,Bundle().apply {
                putInt("month",currentDate.monthValue)
            })
        }

        if (Helper.isOnline(view.context)) {
            bindObservers()

            calenderViewModel.getEntryOnCalender(currentDate.atStartOfDay())
        }

    }

    override fun onResume() {
        super.onResume()
        changeVisibilityMenu(true)
    }

    private fun setMonthView() {
        binding.calWeeklyRV.visibility = View.INVISIBLE
        binding.calMonthRV.visibility = View.VISIBLE

        currentDate = LocalDate.now()
        binding.monthYearTV.text = formatDateMonthYear(currentDate)

        val layoutManager: RecyclerView.LayoutManager =
            GridLayoutManager(activity?.applicationContext, 7)
        binding.calMonthRV.layoutManager = layoutManager
        binding.calMonthRV.adapter = adapterCalMonth
    }

/*    private fun updateMonthView() {

        binding.monthYearTV.text = formatDateMonthYear(currentDate)
        adapter.updateList(daysInMonthArray(
            currentDate
        ))
    }*/

    private fun setWeeklyView() {

        binding.calMonthRV.visibility = View.GONE
        binding.calWeeklyRV.visibility = View.VISIBLE

        currentDate = LocalDate.now()
        binding.monthYearTV.text = formatDateMonthYear(currentDate)

        val layoutManager: RecyclerView.LayoutManager =
            GridLayoutManager(activity?.applicationContext, 7)
        binding.calWeeklyRV.layoutManager = layoutManager
        binding.calWeeklyRV.adapter = adapterCalWeekly

        //setEventAdapter()
    }


    private fun updateCalenderView() {

        binding.monthYearTV.text = formatDateMonthYear(currentDate)

        if (binding.calMonthRV.visibility == View.VISIBLE) {
            adapterCalMonth.updateList(
                daysInMonthArray(
                    currentDate
                )
            )
        } else {
            adapterCalWeekly.updateList(
                CalenderUtils.daysInWeekArray(
                    currentDate
                )
            )
        }
    }

    private fun changeVisibilityMenu(state: Boolean) {
        val menu = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        if (state) {
            menu!!.visibility = View.VISIBLE
        } else {
            menu!!.visibility = View.GONE
        }
    }
    private fun bindObservers() {
        calenderViewModel.getEntryOnCalenderLiveData.observe(viewLifecycleOwner) { networkResultEvent ->
            networkResultEvent.getContentIfNotHandled()?.let {
                when (it) {
                    is NetworkResult.Success -> {

                        
                    }
                    is NetworkResult.Error -> {
                        toast(it.message.toString(), type = ToastType.ERROR)
                    }
                    is NetworkResult.Loading -> {
                        //todo rui falta progress bar
                        //binding.progressBar.show()

                    }
                }
            }
        }
    }



}