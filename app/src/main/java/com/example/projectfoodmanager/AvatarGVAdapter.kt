package com.example.projectfoodmanager

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.example.projectfoodmanager.data.model.Avatar
import java.util.ArrayList

class AvatarGVAdapter(context: Context, avatarArrayList: ArrayList<Avatar>):
    ArrayAdapter<Avatar?>(context,0, avatarArrayList as List<Avatar?>){

    @SuppressLint("UseCompatLoadingForDrawables", "DiscouragedApi")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var listitemview = convertView

        if (listitemview==null){
            listitemview= LayoutInflater.from(context).inflate(R.layout.item_card_avatar,parent,false)
        }

        val avatar: Avatar? = getItem(position)
        val img = listitemview!!.findViewById<ImageView>(R.id.imgItemAvatar)
        val reserve = listitemview.findViewById<ImageView>(R.id.imgVipAvatar)


        // Get the drawable resource by its name
        //val resourceId = context.resources.getIdentifier(avatar?.name, "drawable", context.packageName)

        // Check if the resource ID is valid
        //if (resourceId != 0) {
            // Use the drawable as needed
            //val drawable = context.resources.getDrawable(resourceId, null)
        img.setImageResource(avatar!!.imgId)
       // }

        if (avatar.reserved){
            reserve.visibility= View.VISIBLE
        }else{
            reserve.visibility= View.INVISIBLE

        }

        return listitemview
    }

}