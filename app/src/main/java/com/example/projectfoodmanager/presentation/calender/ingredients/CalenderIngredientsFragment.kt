package com.example.projectfoodmanager.presentation.calender.ingredients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.databinding.FragmentCalenderIngredientsBinding
import com.example.projectfoodmanager.presentation.calender.utils.CalenderUtils
import com.example.projectfoodmanager.util.Helper.Companion.formatLocalTimeToServerTime
import com.example.projectfoodmanager.util.NetworkResult
import com.example.projectfoodmanager.viewmodels.CalenderViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters


class CalenderIngredientsFragment : Fragment() {

    private lateinit var toDate: LocalDateTime
    private lateinit var fromDate: LocalDateTime

    // binding
    lateinit var binding: FragmentCalenderIngredientsBinding

    // viewModels
    private val calenderViewModel by activityViewModels<CalenderViewModel>()

    // constants
    private val TAG: String = "NewCalenderEntryFragment"
    private var defaultMonth: Int = -1


    private val calenderIngredientsAdapter by lazy{
        CalenderIngredientsAdapter(
            onItemClicked = { pos, item ->

            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        defaultMonth = arguments?.getInt("month")!!

        return if (this::binding.isInitialized) {
            binding.root
        } else {
            binding = FragmentCalenderIngredientsBinding.inflate(layoutInflater)

            binding.calenderIngridientsLV.layoutManager = LinearLayoutManager(activity)
            binding.calenderIngridientsLV.adapter = calenderIngredientsAdapter

            bindObservers()

            binding.root
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fromDate = LocalDateTime.now().withMonth(defaultMonth).withDayOfMonth(1)
        toDate = fromDate.with(TemporalAdjusters.lastDayOfMonth())

        calenderViewModel.getCalenderIngredients(fromDate, toDate)

        // date buttons

        binding.fromDateValTV.text = fromDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        binding.fromDateCV.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Selecione a data")
                    .setTheme(R.style.Widget_AppTheme_MaterialDatePicker)
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()
            datePicker.dialog?.setCanceledOnTouchOutside(false)

            datePicker.show(parentFragmentManager, "DatePicker");

            datePicker.addOnCancelListener {
                datePicker.dismiss()
            }

            datePicker.addOnPositiveButtonClickListener {

                if(datePicker.headerText.length == 9)
                    binding.fromDateValTV.text= getString(R.string.date_text, "0" + datePicker.headerText)

                fromDate  = LocalDateTime.of(LocalDate.parse(binding.fromDateValTV.text, DateTimeFormatter.ofPattern("dd/MM/yyyy")),LocalTime.MIDNIGHT)
                calenderViewModel.getCalenderIngredients(fromDate, toDate)
            }

        }

        binding.toDateValTV.text = toDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        binding.toDateValTV.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Selecione a data")
                    .setTheme(R.style.Widget_AppTheme_MaterialDatePicker)
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()
            datePicker.dialog?.setCanceledOnTouchOutside(false)

            datePicker.show(parentFragmentManager, "DatePicker");

            datePicker.addOnCancelListener {
                datePicker.dismiss()
            }

            datePicker.addOnPositiveButtonClickListener {

                if(datePicker.headerText.length == 9)
                    binding.toDateValTV.text= getString(R.string.date_text, "0" + datePicker.headerText)

                toDate  = LocalDateTime.of(LocalDate.parse(binding.fromDateValTV.text, DateTimeFormatter.ofPattern("dd/MM/yyyy")),LocalTime.MIDNIGHT)
                calenderViewModel.getCalenderIngredients(fromDate, toDate)
            }

        }




    }

    private fun bindObservers() {

        calenderViewModel.getCalenderIngredientsLiveData.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { result ->
                when (result) {
                    is NetworkResult.Success -> {


                    result.data?.let { it -> calenderIngredientsAdapter.updateList(it.result) }

                    }
                    is NetworkResult.Error -> {

                    }
                    is NetworkResult.Loading -> {
                        // todo rui loading bar
                        //binding.progressBar.isVisible = true
                    }
                }
            }
        }


    }
}