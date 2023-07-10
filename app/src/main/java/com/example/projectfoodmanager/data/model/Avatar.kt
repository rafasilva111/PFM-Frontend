package com.example.projectfoodmanager.data.model

import com.example.projectfoodmanager.R
import java.util.ArrayList

class Avatar(var id: Int,var imgId:Int,var reserved: Boolean)  {

    private var name:String

    init {
        if (reserved)
            this.name= "avatar_vip$id.png"
        else
            this.name= "avatar$id.png"
    }

    fun getName(): String {
        return this.name
    }

    companion object {
        val avatarArrayList: ArrayList<Avatar> = ArrayList<Avatar>()

        init {
            //NORMAL
            avatarArrayList.add(Avatar(1, R.drawable.avatar1,false))
            avatarArrayList.add(Avatar(2, R.drawable.avatar2,false))
            avatarArrayList.add(Avatar(3, R.drawable.avatar3,false))
            avatarArrayList.add(Avatar(4, R.drawable.avatar4,false))
            avatarArrayList.add(Avatar(5, R.drawable.avatar5,false))
            avatarArrayList.add(Avatar(6, R.drawable.avatar6,false))
            avatarArrayList.add(Avatar(7, R.drawable.avatar7,false))
            avatarArrayList.add(Avatar(8, R.drawable.avatar8,false))
            avatarArrayList.add(Avatar(9, R.drawable.avatar9,false))
            avatarArrayList.add(Avatar(10, R.drawable.avatar10,false))
            //VIP
            avatarArrayList.add(Avatar(1, R.drawable.avatar_vip1,true))
            avatarArrayList.add(Avatar(2, R.drawable.avatar_vip2,true))
            avatarArrayList.add(Avatar(3, R.drawable.avatar_vip3,true))
            avatarArrayList.add(Avatar(4, R.drawable.avatar_vip4,true))
            avatarArrayList.add(Avatar(5, R.drawable.avatar_vip5,true))
            avatarArrayList.add(Avatar(6, R.drawable.avatar_vip6,true))
            avatarArrayList.add(Avatar(7, R.drawable.avatar_vip7,true))
            avatarArrayList.add(Avatar(8, R.drawable.avatar_vip8,true))
            avatarArrayList.add(Avatar(9, R.drawable.avatar_vip9,true))
            avatarArrayList.add(Avatar(10, R.drawable.avatar_vip10,true))
            avatarArrayList.add(Avatar(11, R.drawable.avatar_vip11,true))
            avatarArrayList.add(Avatar(12, R.drawable.avatar_vip12,true))
        }


        fun getAvatarByName(name:String):Avatar?{
            for (avatar in avatarArrayList)
                if (avatar.name==name)
                    return avatar

            return null
        }

    }


}