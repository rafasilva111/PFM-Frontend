package com.example.projectfoodmanager.presentation.recipe.details;

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Preparation


class PreparationListingAdapter(
    val context: Context,
    private val items: List<Preparation>
    ):
    BaseAdapter() {
    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(p0: Int): Any {
        return items[p0].toString()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var view: View? = convertView
        val vh: ViewHolderPreparation
        if (view == null) {
            var layoutInflater = LayoutInflater.from(parent?.context)

            view = layoutInflater.inflate(R.layout.item_recipe_preparation_layout_new, parent, false)
            vh = ViewHolderPreparation(view)
            if (view != null) {
                view.tag = vh
            }
            vh.tvStep_number.text = items[position].step.toString()
            vh.tvTitle.text = items[position].step.toString() + " Passo"
            vh.tvDiscription.text = items[position].description

            if (position == count-1)
                vh.vLine.isVisible=false

            return view

        }else{
            return view
        }



    }

}



private class ViewHolderPreparation(view: View?) {
    val tvDiscription: TextView = view?.findViewById<TextView>(R.id.discriptionTV) as TextView
    val tvStep_number: TextView = view?.findViewById<TextView>(R.id.stepTV) as TextView
    val tvTitle: TextView = view?.findViewById<TextView>(R.id.numberStepTv) as TextView
    val vLine: View = view?.findViewById<View>(R.id.lineV) as View
}


