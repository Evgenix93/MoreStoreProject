package com.project.morestore.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.SizeLineAdapter
import com.project.morestore.databinding.FragmentFilterSizesColthesBinding
import com.project.morestore.models.Filter
import com.project.morestore.models.Property
import com.project.morestore.models.Size
import com.project.morestore.models.SizeLine
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import com.project.morestore.singletones.FilterState
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class FilterSizesFragment : MvpAppCompatFragment(R.layout.fragment_filter_sizes_colthes),
    UserMvpView {
    private val binding: FragmentFilterSizesColthesBinding by viewBinding()
    private val sizeAdapter = SizeLineAdapter(false)
    private var topSizeList = listOf<SizeLine>()
    private var bottomSizeList = listOf<SizeLine>()
    private var isForWomen = true
    private var isSizesLoaded = false
    //private var filter = Filter()

    private val presenter by moxyPresenter { UserPresenter(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initToolBar()
        loadFilter()

    }


    private fun initList() {
        with(binding.sizesList) {
            adapter = sizeAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

       // presenter.getAllSizes()

        /*val list = listOf(
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


        )

        /* sizeAdapter.updateList(if(FilterState.chosenSizes.isNotEmpty()){
             val allNotSelected = FilterState.chosenSizes.all { !it.isSelected }
             if(allNotSelected){
                 for(size in FilterState.chosenSizes){
                     size.isSelected = true
                 }
             }
             FilterState.chosenSizes
         } else emptyList())*/*/
    }

    private fun initToolBar() {
        binding.toolbar.titleTextView.text = "Размер"
        binding.toolbar.actionTextView.text = "Сбросить"
        binding.toolbar.actionTextView.setOnClickListener {
            sizeAdapter.cleanCheckBoxes()
        }
        binding.toolbar.imageView2.setOnClickListener { findNavController().popBackStack() }
    }

    private fun convertSizeToSizeLine(size: Size): SizeLine {
        val int = size.name
        var w = ""
        var itRuFr = ""
        var us = ""
        var uk = ""
        when (size.name) {
            "XXS" -> {
                w = "26-27"
                itRuFr = "42"
                us = "32"
                uk = "32"
            }
            "XS" -> {
                w = "28-29"
                itRuFr = "44"
                us = "34"
                uk = "34"
            }
            "S" -> {
                w = "30-31"
                itRuFr = "46"
                us = "36"
                uk = "36"
            }
            "M" -> {
                w = "32-33"
                itRuFr = "48"
                us = "38"
                uk = "38"
            }
            "L" -> {
                w = "34-35"
                itRuFr = "50"
                us = "40"
                uk = "40"
            }
            "XL" -> {
                w = "36-37"
                itRuFr = "52"
                us = "42"
                uk = "42"
            }
            "XXL" -> {
                w = "38-39"
                itRuFr = "54"
                us = "44"
                uk = "44"
            }
            "3XL" -> {
                w = "40-41"
                itRuFr = "56"
                us = "46"
                uk = "46"
            }
            "4XL" -> {
                w = "42-43"
                itRuFr = "58"
                us = "48"
                uk = "48"
            }
            "5XL" -> {
                w = "44-45"
                itRuFr = "60"
                us = "50"
                uk = "60"
            }
            else -> {
            }
        }

        return SizeLine(size.id, int, w, itRuFr, us, uk, size.chosen ?: false)


    }

    private fun showLoading(loading: Boolean){
        binding.loader.isVisible = loading
    }

   // private fun loadFilterSizes(){
       // presenter.loadTopSizes()
   // }

    private fun getSizes(){
        if (isForWomen) {
            presenter.getTopSizesWomen()
            presenter.getBottomSizesWomen()
        }
        else {
            presenter.getTopSizesMen()
            presenter.getBottomSizesMen()
        }
    }

    private fun loadFilter(){
        presenter.getFilter()
    }

    private fun bindFilter(filter: Filter){
        isForWomen = filter.chosenForWho[0]

        if(!isSizesLoaded)
        getSizes()

        Log.d("mytest", filter.chosenTopSizes.size.toString())



        when {
         filter.chosenForWho[0] ->   if (sizeAdapter.getChosenSizes().size == filter.chosenTopSizes.size) {
                sizeAdapter.updateList(filter.chosenTopSizes, null)
            }

            filter.chosenForWho[1] ->  if (sizeAdapter.getChosenSizes().size == filter.chosenTopSizesMen.size) {
                sizeAdapter.updateList(filter.chosenTopSizesMen, null)
            }


        }
    }

    private fun saveSizes(){
        val chosenTopSizes = sizeAdapter.getChosenSizes()
        val chosenBottomSizes = sizeAdapter.getChosenBottomSizes()
        //val chosenBottomSizes = bottomSizeList.apply {
        // forEachIndexed { index, sizeLine ->
        // sizeLine.isSelected = chosenTopSizes[index].isSelected
        //}

        //}

        if(isForWomen) {
            presenter.saveTopSizes(chosenTopSizes)
            presenter.saveBottomSizes(chosenBottomSizes)
        }else{
            presenter.saveTopSizesMen(chosenTopSizes)
            presenter.saveBottomSizesMen(chosenBottomSizes)
        }

    }



    override fun onStop() {
        super.onStop()
        saveSizes()

    }

    override fun success(result: Any) {

    }

    override fun error(message: String) {
        showLoading(false)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }

    override fun loading() {
        showLoading(true)

    }

    override fun loaded(result: Any) {
        showLoading(false)
        if(result is List<*> ) {
            val sizes =
                (result as List<Property>)
                    .map {
                        val list = it.ico?.split(';').orEmpty()
                        SizeLine(it.id.toInt(), it.name, list[0].removePrefix("W").removeSurrounding("'"), list[1].removePrefix("IT/RU/FR").removeSurrounding("'"), list[2].removePrefix("US").removeSurrounding("'"), list[3].removePrefix("UK").removeSurrounding("'"), false)
                    }
            //val listBottomSizes = result.sortedBy { it.toInt() }
              //  .map { convertSizeToSizeLine(it) }
            if(result[0].idCategory.toInt() == 1 || result[0].idCategory.toInt() == 4){
                sizeAdapter.updateList(sizes + listOf(SizeLine(0, "", "", "", "", "", false)), null)
            }else{
                sizeAdapter.updateList( sizeAdapter.getChosenSizes(), sizes + listOf(SizeLine(0, "", "", "", "", "", false)) )
            }

           // sizeAdapter.updateList(listTopSizes + listOf(SizeLine(0, "", "", "", "", "", false)), listBottomSizes + listOf(SizeLine(0, "", "", "", "", "", false)))

            //bottomSizeList = listBottomSizes
            //topSizeList = listTopSizes
            //Log.d("mylog2", topSizeList.size.toString())
            isSizesLoaded = true
            loadFilter()



        }

        if(result is Filter){
            //Log.d("mylog2", "filterSizes")
            //val sizes = result as List<SizeLine>
            //Log.d("mylog2", "${topSizeList.size} topSizes")
           // Log.d("mylog2", sizes.size.toString())


           // if(topSizeList.size + 1 == sizes.size){
               // Log.d("mylog2", "filterSizes")
               // sizeAdapter.updateList(sizes)
            //}

            bindFilter(result)


        }


    }

    override fun successNewCode() {

    }
}