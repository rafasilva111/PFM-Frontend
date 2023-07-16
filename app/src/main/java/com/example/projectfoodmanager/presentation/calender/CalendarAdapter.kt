package com.example.projectfoodmanager.presentation.calender

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.presentation.calender.utils.CalenderUtils.Companion.currentDate
import java.time.LocalDate


class CalendarAdapter(
    private var days: ArrayList<LocalDate?>,
    private val onItemClicked: (String) -> Unit,
) :
    RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    private var unselected: Boolean = true

    //private var currentSelected: TextView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.calendar_cell, parent, false)
        val layoutParams = view.layoutParams

 /*       if (days.size> 15)
            layoutParams.height = (parent.height * 0.166666666).toInt()
        else
            layoutParams.height = parent.height*/
        return CalendarViewHolder(parent.context,view,days)
    }


    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {

        val date = days[position]
        if (date == null)
            holder.dayOfMonth.text = ""
        else{
            holder.dayOfMonth.text =  date.dayOfMonth.toString()
            if (date == currentDate && currentDate == LocalDate.now()) {
                //holder.parentView.setBackgroundColor(Color.GRAY)
                colorPresentDay(holder)

            }else {
                colorDefaultDay(holder)
            }
        }


    }

    private fun colorDefaultDay(holder: CalendarViewHolder) {
        holder.dayOfMonth.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                holder.parentView.context,
                R.color.transparent
            )
        )

        holder.dayOfMonth.setTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    holder.parentView.context,
                    R.color.black
                )
            )
        )
    }

    private fun colorPresentDay(holder: CalendarViewHolder) {
        holder.dayOfMonth.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(
                holder.parentView.context,
                R.color.main_color
            )
        )

        holder.dayOfMonth.setTextColor(
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    holder.parentView.context,
                    R.color.white
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
                if (dayOfMonth.text.isNotBlank() ){

                    onItemClicked.invoke( dayOfMonth.text as String)
                }
                    notifyDataSetChanged()
            }

        }

    }

}