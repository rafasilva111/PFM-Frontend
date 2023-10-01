package com.example.projectfoodmanager.presentation.recipe.create.steps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectfoodmanager.data.model.modelResponse.recipe.Preparation
import com.example.projectfoodmanager.databinding.FragmentStepPreparationBinding
import java.util.*


class StepPreparationFragment : Fragment() {

    // binding
    lateinit var binding: FragmentStepPreparationBinding

    private var position: Int = -1
    private var isUpdate: Boolean = false
    private var itemToUpdated: Preparation? = null
    private val items = mutableListOf<Preparation>()

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (!this::binding.isInitialized) {
            binding = FragmentStepPreparationBinding.inflate(layoutInflater)
        }
        return binding.root
    }

    private val adapter by lazy{
        PreparationAdapter(
            items,

            onItemClicked =  { pos,item ->

            },
            onUpdateClicked = { pos,item ->
                isUpdate=true
                itemToUpdated = item
                position = pos

                binding.preparationET.setText(item.description)

            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUI()

        setEvents()

    }

    private fun setEvents() {
        binding.addIngredientBTN.setOnClickListener {
            //TODO: Ver o com o rafa
            if(isUpdate){
                //itemToUpdated!!.description = binding.preparationET.text.toString()
                adapter.updateItem(position, itemToUpdated!!)
                binding.preparationET.setText("")
                binding.preparationET.clearFocus()
                isUpdate=false
            }else{
                adapter.addItem(Preparation(binding.preparationET.text.toString(),0))
                binding.preparationET.setText("")
                binding.preparationET.clearFocus()
            }
            updateNSteps()
        }

        val simpleCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or
                    ItemTouchHelper.DOWN or
                    ItemTouchHelper.START or
                    ItemTouchHelper.END,0
        ){
            // override the necessary methods here
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
            ): Boolean {

                // Implement move logic here
                val fromPosition = viewHolder.bindingAdapterPosition
                val toPosition = target.bindingAdapterPosition

                Collections.swap(adapter.getList(),fromPosition, toPosition)

                adapter.notifyItemMoved(fromPosition,toPosition)
                return false;
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Implement swipe logic here
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)

                // The item has stopped moving
                adapter.updateList(adapter.getList())
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleCallback)

        itemTouchHelper.attachToRecyclerView(binding.preparationRV)

        binding.backIB.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setUI() {
        val manager = LinearLayoutManager(activity)
        binding.preparationRV.layoutManager = manager
        binding.preparationRV.adapter = adapter

        updateNSteps()
    }

    private fun updateNSteps() {
        binding.nStepsTV.text = adapter.itemCount.toString() + " Steps"
    }


    /*private fun getRecipeRequest(): RecipeRequest {

        val sex: String = when (binding.sexEt.text.toString()) {
            "Masculino" -> SexConstants.M
            "Feminino" -> SexConstants.F
            else -> SexConstants.NA
        }

        var img = ""
        if (selectedAvatar != null){
            img = selectedAvatar!!
        }else if (fileName!=null){
            img= fileName!!
        }

        return UserRequest(
            name =  binding.firstNameEt.text.toString() + " "+ binding.lastNameEt.text.toString(),
            email = binding.emailEt.text.toString(),
            birth_date = binding.dateEt.text.toString(),
            password = binding.passEt.text.toString(),
            sex = sex,
            img_source=img
        )
    }*/

}