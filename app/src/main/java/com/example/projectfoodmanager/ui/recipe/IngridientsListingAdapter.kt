package com.example.projectfoodmanager.ui.recipe;

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.projectfoodmanager.R


class IngridientsListingAdapter     /* here we must override the constructor for ArrayAdapter
      * the only variable we care about now is ArrayList<Item> objects,
      * because it is the list of objects we want to display.
      */(
    context: Context?, textViewResourceId: Int, // declaring our ArrayList of items
    private val objects: ArrayList<String>
) :
    ArrayAdapter<String?>(context!!, textViewResourceId, objects as ArrayList<String?>) {
    /*
	 * we are overriding the getView method here - this is what defines how each
	 * list item will look.
	 */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // assign the view we are converting to a local variable
        var v : View? = convertView

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = inflater.inflate(R.layout.item_recipe_ingredients_layout, null)


        }

        var str : String = objects.get(position)

        if(str != ""){
            val description : TextView = v!!.findViewById(R.id.TV_IngredINFO)
            description.text = str.toString()
        }

        return v!!
    }
}

/*
class IngridientsListingAdapter(private val context : Activity,private val  arrayList: ArrayList<String>) :
    ArrayAdapter<String>(context,R.layout.item_recipe_ingredients_layout, arrayList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val inflater : LayoutInflater = LayoutInflater.from(context)
        val view : View = inflater.inflate(R.layout.item_recipe_ingredients_layout,null)

        //val number : TextView  = view.findViewById(R.id.TV_number)
        val description : TextView = view.findViewById(R.id.TV_Info)


        description.text = arrayList[position]

        return view
    }
}*/
