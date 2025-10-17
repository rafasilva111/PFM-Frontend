import android.content.res.ColorStateList
import androidx.viewbinding.ViewBinding
import com.example.projectfoodmanager.R
import com.example.projectfoodmanager.data.model.modelResponse.calender.CalenderEntry
import com.example.projectfoodmanager.databinding.ItemCalenderEntryBinding
import com.example.projectfoodmanager.util.BaseAdapter
import com.example.projectfoodmanager.util.CALENDAR_MEALS_TAG
import com.example.projectfoodmanager.util.Helper.Companion.formatServerTimeToTimeString
import com.example.projectfoodmanager.util.Helper.Companion.loadRecipeImage
import com.example.projectfoodmanager.util.listeners.ImageLoadingListener

class CalendarEntryAdapter(
    val onItemClicked: (Int, CalenderEntry) -> Unit,
    val onDoneClicked: (Boolean, CalenderEntry) -> Unit,
    private val imageLoadingListener: ImageLoadingListener
) : BaseAdapter<CalenderEntry, ItemCalenderEntryBinding>(
    ItemCalenderEntryBinding::inflate
) {



    override fun bind(binding: ItemCalenderEntryBinding, item: CalenderEntry, position: Int) {

        /**
         * Loading Images
         */


        // Load Recipe img
        loadRecipeImage(binding.recipeIV, item.recipe.imgSource) {
            imageLoadingListener.onImageLoaded()
        }


        /**
         * Details
         */


        binding.nameRecipeTV.text = item.recipe.title
        binding.tagTV.text = item.tag

        val tags = binding.root.context.resources.getStringArray(R.array.tagEntryCalender_items).toList()

        for (tag in tags){
            if(item.tag.lowercase() == tag.lowercase())
                binding.tagTV.text = tag
        }
        binding.tagTV.backgroundTintList= getColorTag(item.tag,binding)
        binding.itemLayout.setOnClickListener { onItemClicked.invoke(position, item) }


        binding.timeTV.text = formatServerTimeToTimeString(item.realizationDate)


        binding.checkDoneCB.isChecked = item.checkedDone

        binding.checkDoneCB.setOnClickListener {

            onDoneClicked.invoke(binding.checkDoneCB.isChecked,item)

        }

    }

    private fun getColorTag(tag: String, binding: ViewBinding): ColorStateList {

        var color: ColorStateList? = null

        when(tag){
            CALENDAR_MEALS_TAG.PEQUENO_ALMOCO -> color = binding.root.context.resources.getColorStateList(R.color.catg_peq_almoco, null)
            CALENDAR_MEALS_TAG.LANCHE_DA_MANHA -> color = binding.root.context.resources.getColorStateList(R.color.catg_lanche_manha, null)
            CALENDAR_MEALS_TAG.ALMOCO -> color = binding.root.context.resources.getColorStateList(R.color.catg_almoco, null)
            CALENDAR_MEALS_TAG.LANCHE_DA_TARDE -> color = binding.root.context.resources.getColorStateList(R.color.catg_lanche_tarde, null)
            CALENDAR_MEALS_TAG.JANTAR -> color = binding.root.context.resources.getColorStateList(R.color.catg_jantar, null)
            CALENDAR_MEALS_TAG.CEIA -> color = binding.root.context.resources.getColorStateList(R.color.catg_ceia, null)
        }

        return color!!
    }


}