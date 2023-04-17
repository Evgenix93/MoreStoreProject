package com.project.morestore.presentation.adapters

import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.project.morestore.R
import com.project.morestore.data.models.Region
import com.project.morestore.util.createView

abstract class CitiesAdapter :RecyclerView.Adapter<CitiesAdapter.CityHolder>() {
    open var items = arrayOf<Region>()
        set(value){
            field = value
            notifyDataSetChanged()
        }

    //region Override
    override fun onBindViewHolder(holder: CityHolder, index: Int) = holder.bind(items[index])

    override fun getItemCount() = items.size
    //endregion Override

    abstract class CityHolder(
        parent :ViewGroup
    ) : RecyclerView.ViewHolder(parent.createView(R.layout.item_city)){
        protected val checkBox :MaterialCheckBox = itemView.findViewById(R.id.check)
        protected lateinit var city :Region

        abstract fun bind(city :Region)
    }
}

class SingleCityAdapter(val callback :(Region)->Unit) :CitiesAdapter(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ClickCityHolder(parent)

    inner class ClickCityHolder(parent: ViewGroup) :CityHolder(parent){
        init {
            checkBox.buttonDrawable = null
            checkBox.setOnClickListener { callback(city) }
        }

        override fun bind(city: Region) {
            this.city = city
            checkBox.text = city.name
        }
    }
}

class MultiplyCityAdapter :CitiesAdapter(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SelectCityHolder(parent)

    val selected get() = items.filter { it.isChecked }

    private fun checkAll(check :Boolean){
        items.map { it.isChecked = check }
        notifyDataSetChanged()
    }

    private fun checkSingle(check :Boolean){
        items[0].isChecked = items.drop(1).all { it.isChecked }
        notifyItemChanged(0)
    }

    inner class SelectCityHolder(parent: ViewGroup) :CityHolder(parent){
        init {
            checkBox.setOnClickListener {
                it as CompoundButton
                city.isChecked = it.isChecked
                if(city.id == 0L) checkAll(city.isChecked)
                else checkSingle(city.isChecked)
            }
        }

        override fun bind(city: Region) {
            this.city = city
            checkBox.text = city.name
            checkBox.isChecked = city.isChecked
        }
    }
}