package com.example.projectfoodmanager.presentation.calender

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.databinding.ItemEventTrashBinding
import com.example.projectfoodmanager.presentation.calender.utils.CalenderUtils
import java.time.LocalDate



class TrashEventAdapter(
    private var recipes: ArrayList<EventTrash>
) :
    RecyclerView.Adapter<TrashEventAdapter.EventViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(R.layout.calendar_cell, parent, false)
        return EventViewHolder(view)
    }


    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {

        val recipe = recipes[position]

        holder.name.text = recipe.name

    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    fun updateList(daysInMonthArray: ArrayList<EventTrash>) {
        recipes = daysInMonthArray
        notifyDataSetChanged()
    }

    inner class EventViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val name: TextView
        init {
            name = itemView.findViewById(R.id.nameEventTV)
            notifyItemChanged(bindingAdapterPosition)

        }

    }

}