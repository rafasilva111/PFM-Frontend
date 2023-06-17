package com.example.projectfoodmanager.presentation.recipe.details;

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Ingredient


class IngridientsListingAdapter(
    val context: Context,
    private val items: List<Ingredient>
):
    BaseAdapter() {
    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(p0: Int): Any {
        return items[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View
        val vh: ViewHolder
        if (convertView == null) {
            val layoutInflater = LayoutInflater.from(parent?.context)

            view = layoutInflater.inflate(R.layout.item_recipe_ingredients_layout, parent, false)
            vh = ViewHolder(view)
            if (view != null) {
                view.tag = vh
            }
        } else {
            view = convertView
            vh = view.tag as ViewHolder
        }

        vh.tvName.text = items[position].name.capitalize()
        vh.tvQuant.text = items[position].quantity


        return view
    }

}



private class ViewHolder(view: View?) {
    val tvName: TextView = view?.findViewById<TextView>(R.id.nameIngridTV) as TextView
    val tvQuant: TextView = view?.findViewById<TextView>(R.id.quantIngridTV) as TextView
}