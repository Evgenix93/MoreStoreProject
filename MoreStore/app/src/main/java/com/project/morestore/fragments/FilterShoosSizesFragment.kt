package com.project.morestore.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.SizeLineAdapter
import com.project.morestore.databinding.FragmentFilterSizesShoesBinding
import com.project.morestore.models.Filter
import com.project.morestore.models.Property
import com.project.morestore.models.Size
import com.project.morestore.models.SizeLine
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import com.project.morestore.singletones.FilterState
import com.project.morestore.util.autoCleared
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class FilterShoosSizesFragment: MvpAppCompatFragment(R.layout.fragment_filter_sizes_shoes), UserMvpView {
    private val binding: FragmentFilterSizesShoesBinding by viewBinding()
    private var sizeAdapter: SizeLineAdapter by autoCleared()
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }
    //private var shoosSizes = listOf<SizeLine>()
    private var isForWomen = true
    private var isSizesLoaded = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initToolbar()
        loadFilter()
        setClickListenrs()
    }

    private fun setClickListenrs(){
        binding.showProductsBtn.setOnClickListener {
            findNavController().navigate(R.id.catalogFragment)
        }
    }

    private fun initList(){
        sizeAdapter = SizeLineAdapter(true)
        with(binding.sizesList) {
            adapter = sizeAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        //presenter.getAllSizes()

       /* val list = listOf(
            SizeLine(
                0,
                "XXS",
                "26-27",
                "42",
                "32",
                "32",
                true
            ),
            SizeLine(
                0,
                "XS",
                "28-29",
                "44",
                "34",
                "34",
                true
            ),
            SizeLine(
                0,
                "S",
                "30-31",
                "46",
                "36",
                "36",
                true
            ),
            SizeLine(
                0,
                "M",
                "32-33",
                "48",
                "38",
                "38",
                true
            ),
            SizeLine(
                0,
                "L",
                "34-35",
                "50",
                "40",
                "40",
                true
            ),
            SizeLine(
                0,
                "XL",
                "36-37",
                "52",
                "42",
                "42",
                true
            ),
            SizeLine(
                0,
                "XXL",
                "38-39",
                "54",
                "44",
                "44",
                true
            ),
            SizeLine(
                0,
                "3XL",
                "40-41",
                "56",
                "46",
                "46",
                true
            ),
            SizeLine(
                0,
                "4XL",
                "42-43",
                "58",
                "48",
                "48",
                true
            ),
            SizeLine(
                0,
                "5XL",
                "44-45",
                "60",
                "50",
                "60",
                true
            ),
            SizeLine(
                0,
                "",
                "",
                "",
                "",
                "",
                true
            )


        )*/

        /* sizeAdapter.updateList(if(FilterState.chosenSizes.isNotEmpty()){
             val allNotSelected = FilterState.chosenSizes.all { !it.isSelected }
             if(allNotSelected){
                 for(size in FilterState.chosenSizes){
                     size.isSelected = true
                 }
             }
             FilterState.chosenSizes
         } else emptyList())*/
    }

    private fun getSizes(){
        if(isForWomen){
            presenter.getShoosSizesWomen()
        }else{
            presenter.getShoosSizesMen()
        }

    }

    private fun initToolbar(){
        binding.toolbar.titleTextView.text = "Размер"
        binding.toolbar.actionTextView.text = "Сбросить"
        binding.toolbar.actionTextView.setOnClickListener {
            sizeAdapter.cleanCheckBoxes()
        }
        binding.toolbar.imageView2.setOnClickListener { findNavController().popBackStack() }
    }

    private fun convertSizeToSizeLine(size: Size): SizeLine{
        val int = size.name
        var w = ""
        var itRuFr = ""
        var us = ""
        var uk = ""
        when (size.name) {
            "38" -> {
                w = ""
                itRuFr = "39"
                us = "8"
                uk = "5"
            }
            "38.5" -> {
                w = ""
                itRuFr = "39.5"
                us = "8.5"
                uk = "5.5"
            }
            "39" -> {
                w = ""
                itRuFr = "40"
                us = "9"
                uk = "6"
            }
            "39.5" -> {
                w = ""
                itRuFr = "40.5"
                us = "9.5"
                uk = "6.5"
            }
            "40" -> {
                w = ""
                itRuFr = "41"
                us = "10"
                uk = "7"
            }
            "40.5" -> {
                w = ""
                itRuFr = "41.5"
                us = "10.5"
                uk = "7.5"
            }
            "41" -> {
                w = ""
                itRuFr = "42"
                us = "11"
                uk = "8"
            }
            "41.5" -> {
                w = ""
                itRuFr = "42.5"
                us = "11.5"
                uk = "8.5"
            }
            "42" -> {
                w = ""
                itRuFr = "43"
                us = "12"
                uk = "9"
            }
            "42.5" -> {
                w = ""
                itRuFr = "43.5"
                us = "12.5"
                uk = "9.5"
            }

            "43" ->  {
                w = ""
                itRuFr = "44"
                us = "13"
                uk = "10"
            }
            else -> {
            }
        }

        return SizeLine(size.id, int, w, itRuFr, us, uk, size.chosen ?: false)


    }

    private fun showLoading(loading: Boolean){
        binding.loader.isVisible = loading
    }

    private fun loadFilter(){
        presenter.getFilter()
    }

    private fun bindFilter(filter: Filter){
        isForWomen = filter.chosenForWho[0]

        if(!isSizesLoaded){
            getSizes()
        }

        if(isForWomen) {
            if (sizeAdapter.getChosenSizes().size == filter.chosenShoosSizes.size || sizeAdapter.getChosenSizes().size == filter.chosenShoosSizes.size + 1) {
                sizeAdapter.updateList(filter.chosenShoosSizes, null)
            }
        }else{
            if (sizeAdapter.getChosenSizes().size == filter.chosenShoosSizesMen.size || sizeAdapter.getChosenSizes().size == filter.chosenShoosSizesMen.size + 1) {
                sizeAdapter.updateList(filter.chosenShoosSizesMen, null)
            }
        }

    }

    private fun saveShoosSizes(){
        if(isForWomen)
        presenter.saveShoosSizes(sizeAdapter.getChosenSizes())
        else presenter.saveShoosSizesMen(sizeAdapter.getChosenSizes())
    }

    override fun onStop() {
        super.onStop()
        //FilterState.filter.chosenShoosSizes = sizeAdapter.getChosenSizes()
        saveShoosSizes()
    }

    override fun success(result: Any) {

    }

    override fun error(message: String) {
        showLoading(false)

    }

    override fun loading() {
        showLoading(true)

    }

    override fun loaded(result: Any){
        showLoading(false)
        if(result is List<*>) {
            val listShoosSizes =
                (result as List<Property>)
                    .map {
                        val list = it.ico?.split(';').orEmpty()
                        SizeLine(it.id.toInt(), it.name, "", list[0].removePrefix("FR").removeSurrounding("'"), list[1].removePrefix("US").removeSurrounding("'"), list[2].removePrefix("UK").removeSurrounding("'"), false)

                    }
                //(result as List<Size>).filter { it.id_category == 3 }.sortedBy { it.toInt() }
                  //  .ma
            //if(FilterState.filter.chosenShoosSizes.size == listShoosSizes.size + 1){
              //  sizeAdapter.updateList(FilterState.filter.chosenShoosSizes, null)//+ listOf(SizeLine(0, "", "", "", "", "", false)))
            //}else {
                sizeAdapter.updateList(listShoosSizes + listOf(SizeLine(0, "", "", "", "", "", false)), null)
            //}
            //shoosSizes = listShoosSizes
            isSizesLoaded = true
            loadFilter()

        }//else{
           // val sizes = result as List<SizeLine>
           // if(shoosSizes.size == sizes.size){
             //   sizeAdapter.updateList(sizes, null)
            //}
        //}

        if(result is Filter){
            bindFilter(result)

        }


    }



    override fun successNewCode() {

    }
}