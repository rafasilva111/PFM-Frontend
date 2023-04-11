package com.example.projectfoodmanager.presentation.recipe.details;

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.projectfoodmanager.R


class PreparationListingAdapter(
    val context: Context,
    private val items: HashMap<String,String>
    ):
    BaseAdapter() {
    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(p0: Int): Any {
        return items.get(p0.toString()).toString()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View
        val vh: ViewHolderPreparation
        if (convertView == null) {
            var layoutInflater = LayoutInflater.from(context)

            view = layoutInflater.inflate(R.layout.item_recipe_preparation_layout_new, parent, false)
            vh = ViewHolderPreparation(view)
            if (view != null) {
                view.tag = vh
            }
        } else {
            view = convertView
            vh = view.tag as ViewHolderPreparation
        }

        vh.tvNumber.text = (position + 1).toString()
        vh.tvTitle.text = items.get((position+1).toString()).toString()

        return view
    }

}



private class ViewHolderPreparation(view: View?) {
    val tvTitle: TextView = view?.findViewById<TextView>(R.id.TV_Info) as TextView
    val tvNumber: TextView = view?.findViewById<TextView>(R.id.TV_number) as TextView
}


