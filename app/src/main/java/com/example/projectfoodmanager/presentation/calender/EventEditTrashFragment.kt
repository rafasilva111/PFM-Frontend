package com.example.projectfoodmanager.presentation.calender

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.projectfoodmanager.databinding.FragmentEventTrashBinding
import com.example.projectfoodmanager.presentation.calender.utils.CalenderUtils.Companion.daysInMonthArray
import com.example.projectfoodmanager.presentation.calender.utils.CalenderUtils.Companion.formatDate
import com.example.projectfoodmanager.presentation.calender.utils.CalenderUtils.Companion.formatTime
import com.example.projectfoodmanager.presentation.calender.utils.CalenderUtils.Companion.currentDate
import com.example.projectfoodmanager.util.toast
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime


@AndroidEntryPoint
class EventEditTrashFragment : Fragment() {
    lateinit var binding: FragmentEventTrashBinding
    val TAG: String = "ProfileFragment"
    lateinit var time: LocalTime

    private val adapter by lazy {
            CalendarAdapter(
                daysInMonthArray(
                    currentDate
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
        // todo isto não faz sentido, é só para testar
        binding = FragmentEventTrashBinding.inflate(layoutInflater)
        time = LocalTime.now()
        binding.eventTimeTV.text = "Time: "+formatTime(time)
        binding.eventDateTv.text = "Date: "+formatDate(currentDate)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addRecipeBtn.setOnClickListener {
            val eventName = binding.nameET.text.toString()
            val eventTrash =  EventTrash(eventName,time, currentDate)
            EventTrash.eventsList.add(eventTrash)
            findNavController().navigateUp()
        }
    }

}