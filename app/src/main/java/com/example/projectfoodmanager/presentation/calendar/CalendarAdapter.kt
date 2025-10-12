package com.example.projectfoodmanager.presentation.calendar

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.presentation.calendar.utils.CalendarUtils.Companion.selectedDate
import com.example.projectfoodmanager.presentation.calendar.utils.CalendarUtils.Companion.todayDate
import java.time.LocalDate


class CalendarAdapter(
    private var days: ArrayList<LocalDate?>,
    private val onItemClicked: (LocalDate) -> Unit,
) :
    RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private var selected: View? = null
    private var currentDatePainted: View? = null
    private var _days: ArrayList<LocalDate?> = this.days
    //private var currentSelected: TextView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.calendar_cell, parent, false)
        return CalendarViewHolder(parent.context,view)
    }

    private fun colorTodaysDate(itemView: View, context: Context) {
        val dayOfMonth = itemView.findViewById<TextView>(R.id.cellDayText)
        dayOfMonth.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                R.color.light_gray
            )
        )

        dayOfMonth.setTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
        )

    }

    private fun colorSelectedDay(itemView: View, context: Context) {
        val dayOfMonth = itemView.findViewById<TextView>(R.id.cellDayText)
        dayOfMonth.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                R.color.main_color
            )
        )

        dayOfMonth.setTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
        )
    }

    private fun colorUnselectedDay(itemView: View, context: Context) {
        val dayOfMonth = itemView.findViewById<TextView>(R.id.cellDayText)
        dayOfMonth.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                R.color.transparent
            )
        )

        dayOfMonth.setTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    context,
                    R.color.black
                )
            )
        )
    }

    override fun getItemCount(): Int {
        return _days.size
    }

    fun updateList(daysInMonthArray: ArrayList<LocalDate?>) {
        _days = daysInMonthArray
        notifyDataSetChanged()
    }

    inner class CalendarViewHolder constructor(context: Context, itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val dayOfMonth: TextView
        val parentView: View

        init {

            dayOfMonth = itemView.findViewById(R.id.cellDayText)
            parentView = itemView.findViewById(R.id.parentView)


            itemView.setOnClickListener {

                if (_days[position] != null && dayOfMonth.text.isNotBlank()) {

                    onItemClicked.invoke(_days[position]!!)


                    // paint red if deselecting from current day
                    if (currentDatePainted == selected)
                        colorTodaysDate(currentDatePainted!!, context)
                    else
                        colorUnselectedDay(selected!!, context)


                    selected = itemView
                    colorSelectedDay(selected!!, context)

                    try{
                        selectedDate = _days[position]!!
                    }catch (e: Exception){
                        println()
                    }



                }
            }


        }

    }


    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {



        val date = _days[position]
        if (date == null){
            holder.dayOfMonth.text = ""
            holder.itemView.visibility = View.INVISIBLE
        }
        else{

            holder.dayOfMonth.text =  date.dayOfMonth.toString()
            when {
                selectedDate == date && todayDate == date -> {
                    // paints selected day and current day

                    selected = holder.itemView
                    currentDatePainted = selected
                    colorSelectedDay(selected!!, holder.parentView.context)
                }
                selectedDate == date -> {
                    // paints selected day

                    selected = holder.itemView
                    colorSelectedDay(selected!!, holder.parentView.context)
                }
                todayDate == date -> {
                    // paints current day

                    currentDatePainted = holder.itemView
                    colorTodaysDate(currentDatePainted!!, holder.parentView.context)
                }
                else -> {
                    // paints default day

                    colorUnselectedDay(holder.itemView, holder.parentView.context)
                }
            }

        }


    }




}