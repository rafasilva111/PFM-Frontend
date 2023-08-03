package com.example.projectfoodmanager

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import com.example.projectfoodmanager.util.toast
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*


class TesteFragment : Fragment(){

    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_teste, container, false)
        Locale.setDefault(Locale("pt"));

        view.findViewById<Button>(R.id.DatePicker).setOnClickListener {

/*
            picker = DatePickerDialog(
                view.context,
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_YEAR),
            )


            picker.datePicker.spinnersShown=false


            picker.show()*/
            showDatePickerDialog()

        }

        return view
    }

    private fun showDatePickerDialog() {

        Locale.setDefault(Locale("pt"));
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        calendar.timeInMillis = today
        val lastValidMonth = calendar.timeInMillis

        // Build constraints.
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setEnd(lastValidMonth)

        val currentDate = LocalDate.now()
        val selectedMillis = currentDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

        val mtDatePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Selecione a sua data de nascimento")
            .setTheme(R.style.Widget_AppTheme_MaterialDatePicker)
            .setCalendarConstraints(constraintsBuilder.build()) // Set the calendar constraints
            .setSelection(selectedMillis)
            .build()

        mtDatePicker.addOnPositiveButtonClickListener{ selection ->

            val selectedDate = Date(selection)
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = formatter.format(selectedDate)
            toast(""+formattedDate)
        }

        mtDatePicker.show(parentFragmentManager,"DatePicker")
    }



}