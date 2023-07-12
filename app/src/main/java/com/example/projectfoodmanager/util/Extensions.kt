package com.example.projectfoodmanager.util

import android.app.Activity
import android.content.res.ColorStateList
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.projectfoodmanager.R


fun View.hide(){
    visibility = View.GONE
}

fun View.show(){
    visibility=View.VISIBLE
}

fun Fragment.toast(msg: String?){
    Toast.makeText(requireContext(),msg,Toast.LENGTH_LONG).show()
}

fun Activity.toast(msg: String?){
    Toast.makeText(baseContext,msg,Toast.LENGTH_LONG).show()
}

fun String.isValidEmail() =
    isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun Toast.showCustomToast(message: String,type: Int, activity: Activity) {

    val layout = activity.layoutInflater.inflate (R.layout.item_toast, activity.findViewById(R.id.toast_container))

    // set the text of the TextView of the message
    val title = layout.findViewById<TextView>(R.id.titleTV)
    val img = layout.findViewById<ImageView>(R.id.typeIV)
    val cardIcon = layout.findViewById<CardView>(R.id.imgCV)
    val description = layout.findViewById<TextView>(R.id.descriptionTV)

    description.text = message


    when (type){
        ToastConstants.SUCCESS -> {
            title.text="Sucess"
            cardIcon.setCardBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(activity.baseContext, R.color.success)))
            title.backgroundTintList= ColorStateList.valueOf(ContextCompat.getColor(activity.baseContext, R.color.success))
            img.setImageResource(R.drawable.ic_sucess)
        }
        ToastConstants.ALERT -> {
            title.text="Alert"
            cardIcon.setCardBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(activity.baseContext, R.color.warning)))
            title.backgroundTintList= ColorStateList.valueOf(ContextCompat.getColor(activity.baseContext, R.color.warning))
            img.setImageResource(R.drawable.ic_warning)
        }
        ToastConstants.INFO -> {
            title.text="Information"
            cardIcon.setCardBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(activity.baseContext, R.color.info)))
            title.backgroundTintList= ColorStateList.valueOf(ContextCompat.getColor(activity.baseContext, R.color.info))
            img.setImageResource(R.drawable.ic_info)
        }
        ToastConstants.ERROR -> {
            title.text="Error"
            cardIcon.setCardBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(activity.baseContext, R.color.error)))
            title.backgroundTintList= ColorStateList.valueOf(ContextCompat.getColor(activity.baseContext, R.color.error))
            img.setImageResource(R.drawable.ic_error)
        }
        ToastConstants.VIP -> {
            title.text="Vip"
            cardIcon.setCardBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(activity.baseContext, R.color.vip)))
            title.backgroundTintList= ColorStateList.valueOf(ContextCompat.getColor(activity.baseContext, R.color.vip))
            img.setImageResource(R.drawable.vip_white)
        }
    }


    // use the application extension function
    this.apply {
        setGravity(Gravity.TOP, 0, 0)
        duration = Toast.LENGTH_LONG
        view = layout
        show()
    }


}