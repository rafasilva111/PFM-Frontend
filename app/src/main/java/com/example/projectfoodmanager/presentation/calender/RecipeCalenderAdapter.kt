package com.example.projectfoodmanager.presentation.calender

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.presentation.calender.utils.CalenderUtils
import java.time.LocalDate


class RecipeCalenderAdapter(
    private var days: ArrayList<LocalDate?>,
    private val onItemClicked: (String) -> Unit,
) :RecyclerView.Adapter<RecipeCalenderAdapter.RecipeCalenderViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeCalenderViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view: View = inflater.inflate(R.layout.calendar_cell, parent, false)


            return RecipeCalenderViewHolder(view,days)
        }


        override fun onBindViewHolder(holder: RecipeCalenderViewHolder, position: Int) {

            val date = days[position]
            if (date == null)
                holder.dayOfMonth.text = ""
            else{
                holder.dayOfMonth.text =  date.dayOfMonth.toString()
                if (date == CalenderUtils.currentDate)
                    holder.parentView.setBackgroundColor(Color.GRAY)
                else
                    holder.parentView.setBackgroundColor(Color.TRANSPARENT)
            }

        }

        override fun getItemCount(): Int {
            return days.size
        }

        fun updateList(daysInMonthArray: ArrayList<LocalDate?>) {
            days = daysInMonthArray
            notifyDataSetChanged()
        }

        inner class RecipeCalenderViewHolder constructor(itemView: View, days: ArrayList<LocalDate?>) :
            RecyclerView.ViewHolder(itemView) {
            val dayOfMonth: TextView
            val parentView: View

            init {
                dayOfMonth = itemView.findViewById(R.id.cellDayText)
                parentView = itemView.findViewById(R.id.parentView)

                itemView.setOnClickListener {
                    if (dayOfMonth.text.isNotBlank() ){
                        CalenderUtils.currentDate = days[bindingAdapterPosition]!!
                        onItemClicked.invoke( dayOfMonth.text as String) }
                    notifyDataSetChanged()
                }

            }

        }

    }

