package com.example.projectfoodmanager.ui.recipe;

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.projectfoodmanager.R


class PreparationListingAdapter(
    val context: Context,
    private val items: List<String>
    ):
    BaseAdapter() {
    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(p0: Int): Any {
        return p0
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {

        var convertView2 = convertView
        val vh: ViewHolderPreparation
        //if (convertView2 == null) {
            var layoutInflater = LayoutInflater.from(context)

            convertView2 = LayoutInflater.from(context).inflate(R.layout.item_recipe_preparation_layout, parent, false)
            vh = ViewHolderPreparation(convertView2)
            //if (view != null) {
            //    view.tag = vh
            //}
        //} else {
         //   view = convertView
         //   vh = view.tag as ViewHolderPreparation
        //}

        //vh.tvTitle.text = items[position].toString()
        vh.tvNumber.text = position.toString()
        vh.tvInfo.text = items[position].toString()

        return convertView2
    }

}



private class ViewHolderPreparation(view: View?) {
    val tvNumber: TextView = view?.findViewById<TextView>(R.id.TV_number) as TextView
    val tvInfo: TextView = view?.findViewById<TextView>(R.id.TV_Info) as TextView
}


