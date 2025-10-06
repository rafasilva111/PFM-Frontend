package com.example.projectfoodmanager.data.model

import com.example.projectfoodmanager.R
import java.util.ArrayList

class Avatar(var id: Int, var imgId: Int, var reserved: Boolean) {

    private var name: String

    init {
        name = if (reserved) "avatar_vip$id.png" else "avatar$id.png"
    }

    fun getName(): String = name

    companion object {
        val avatarArrayList: ArrayList<Avatar> = ArrayList()
        private val avatarMap: HashMap<String, Avatar> = HashMap()

        init {
            // NORMAL
            for (i in 1..10) {
                val avatar = Avatar(i, getNormalResId(i), false)
                avatarArrayList.add(avatar)
                avatarMap[avatar.getName()] = avatar
            }

            // VIP
            for (i in 1..12) {
                val avatar = Avatar(i, getVipResId(i), true)
                avatarArrayList.add(avatar)
                avatarMap[avatar.getName()] = avatar
            }
        }

        private fun getNormalResId(i: Int): Int {
            return when (i) {
                1 -> R.drawable.avatar1
                2 -> R.drawable.avatar2
                3 -> R.drawable.avatar3
                4 -> R.drawable.avatar4
                5 -> R.drawable.avatar5
                6 -> R.drawable.avatar6
                7 -> R.drawable.avatar7
                8 -> R.drawable.avatar8
                9 -> R.drawable.avatar9
                10 -> R.drawable.avatar10
                else -> throw IllegalArgumentException("Invalid normal avatar id")
            }
        }

        private fun getVipResId(i: Int): Int {
            return when (i) {
                1 -> R.drawable.avatar_vip1
                2 -> R.drawable.avatar_vip2
                3 -> R.drawable.avatar_vip3
                4 -> R.drawable.avatar_vip4
                5 -> R.drawable.avatar_vip5
                6 -> R.drawable.avatar_vip6
                7 -> R.drawable.avatar_vip7
                8 -> R.drawable.avatar_vip8
                9 -> R.drawable.avatar_vip9
                10 -> R.drawable.avatar_vip10
                11 -> R.drawable.avatar_vip11
                12 -> R.drawable.avatar_vip12
                else -> throw IllegalArgumentException("Invalid VIP avatar id")
            }
        }

        fun getAvatarByName(name: String): Avatar? {
            return avatarMap[name]
        }
    }
}