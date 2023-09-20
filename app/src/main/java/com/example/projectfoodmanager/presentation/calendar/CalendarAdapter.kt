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
import com.example.projectfoodmanager.presentation.calendar.utils.CalendarUtils.Companion.currentDate
import com.example.projectfoodmanager.presentation.calendar.utils.CalendarUtils.Companion.selectedDate
import java.time.LocalDate


class CalendarAdapter(
    private var days: ArrayList<LocalDate?>,
    private val onItemClicked: (LocalDate) -> Unit,
) :
    RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private var selected: View? = null
    private var currentDatePainted: View? = null

    //private var currentSelected: TextView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.calendar_cell, parent, false)
        return CalendarViewHolder(parent.context,view,days)
    }


    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {

        val date = days[position]
        val todayDate = LocalDate.now()
        if (date == null)
            holder.dayOfMonth.text = ""
        else{
            holder.dayOfMonth.text =  date.dayOfMonth.toString()

            if (date == currentDate && date == selectedDate){
                selected = holder.itemView
                currentDatePainted = holder.itemView
                colorSelectedDay(currentDatePainted!!, holder.parentView.context)
            }
            else if (date == currentDate && currentDate == todayDate) {
                currentDatePainted = holder.itemView
                colorCurrentDay(currentDatePainted!!, holder.parentView.context)
            }else if (date == selectedDate) {
                selected = holder.itemView
                colorSelectedDay(selected!!, holder.parentView.context)
            }
            else {
                colorUnselectedDay(holder.itemView,holder.parentView.context)
            }
        }


    }



    private fun colorCurrentDay(itemView: View, context: Context) {
        val dayOfMonth = itemView.findViewById<TextView>(R.id.cellDayText)
        dayOfMonth.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                context,
                R.color.gray
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
        return days.size
    }

    fun updateList(daysInMonthArray: ArrayList<LocalDate?>) {
        days = daysInMonthArray
        notifyDataSetChanged()
    }

    inner class CalendarViewHolder constructor(context: Context, itemView: View,days: ArrayList<LocalDate?>) :
        RecyclerView.ViewHolder(itemView) {
        val dayOfMonth: TextView
        val parentView: View

        init {
            dayOfMonth = itemView.findViewById(R.id.cellDayText)
            parentView = itemView.findViewById(R.id.parentView)

            itemView.setOnClickListener {

                if(days[position] != null){
                    if (dayOfMonth.text.isNotBlank() ){
                        onItemClicked.invoke(days[position]!!)
                    }

                    if (currentDatePainted == selected)
                        colorCurrentDay(selected!!,context)
                    else
                        colorUnselectedDay(selected!!, context)


                    selected = itemView
                    selectedDate = days[position]!!

                    colorSelectedDay(selected!!,context)
                }
            }

        }

    }







}