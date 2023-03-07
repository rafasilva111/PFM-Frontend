package com.example.projectfoodmanager.presentation.recipe.comments;

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.Comment


class CommentsListingAdapter(
    val context: Context,
    private val items: MutableList<Comment>
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
        var vh: ViewHolderComments
        if (convertView == null) {
            val layoutInflater = LayoutInflater.from(context)

            view = layoutInflater.inflate(R.layout.item_comment_layout, parent, false)
            vh = ViewHolderComments(view)
            if (view != null) {
                view.tag = vh
            }
        } else {
            view = convertView
        }
        vh = view.tag as ViewHolderComments

        vh.tvName.text = items[position].getAuthor()
        vh.tvData.text = items[position].getData()
        vh.tvMessage.text = items[position].getMessage()

        return view
    }

}



private class ViewHolderComments(view: View?) {
    val tvName: TextView = view?.findViewById<TextView>(R.id.TV_c_Author) as TextView
    val tvData: TextView = view?.findViewById<TextView>(R.id.TV_c_Data) as TextView
    val tvMessage: TextView = view?.findViewById<TextView>(R.id.TV_c_Message) as TextView
}