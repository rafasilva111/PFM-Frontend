package com.example.projectfoodmanager.presentation.calender.ingredients

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.databinding.FragmentCalenderIngredientsBinding
import com.example.projectfoodmanager.databinding.ItemCalenderIngredientEntryBinding


class CalenderIngredientsAdapter(context: Context, private val items: List<String>) : ArrayAdapter<String>(context, 0, items) {
    private val inflater = LayoutInflater.from(context)

    private class ViewHolder(val binding: ItemCalenderIngredientEntryBinding)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder = convertView?.tag as? ViewHolder
            ?: ViewHolder(ItemCalenderIngredientEntryBinding.inflate(inflater, parent, false)).apply {
                binding.root.tag = this
            }

        val item = items[position]
        viewHolder.binding.nameTV.text = item
        viewHolder.binding.quantityTV.text = item

        return viewHolder.binding.root
    }
}