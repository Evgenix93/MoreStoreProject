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
import com.project.morestore.adapters.SizeCardsAdapter
import com.project.morestore.databinding.FragmentFilterSizesKidsBinding
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

class FilterKidsSizesFragment : MvpAppCompatFragment(R.layout.fragment_filter_sizes_kids),
    UserMvpView {
    private val binding: FragmentFilterSizesKidsBinding by viewBinding()
    private var topSizeCardAdapter: SizeCardsAdapter by autoCleared()
    private var bottomSizeCardAdapter: SizeCardsAdapter by autoCleared()
    private var shoesSizeCardAdapter: SizeCardsAdapter by autoCleared()
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }
    private var isForKids = false
    private var isForWomen = true
    private var isForMen = false
    private var isSizesLoaded = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initLists()
        loadFilter()
        setClickListeners()
    }

    private fun setClickListeners(){
        binding.showResultsBtn.setOnClickListener {
            findNavController().navigate(R.id.catalogFragment)
        }
    }


    private fun initToolbar() {
        binding.toolbar.titleTextView.text = "Размер"
        binding.toolbar.actionTextView.text = "Сбросить"
        binding.toolbar.actionTextView.setOnClickListener {
            topSizeCardAdapter.cleanChosenSizes()
            bottomSizeCardAdapter.cleanChosenSizes()
            shoesSizeCardAdapter.cleanChosenSizes()
        }
        binding.toolbar.imageView2.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initLists() {
        topSizeCardAdapter = SizeCardsAdapter(false)
        bottomSizeCardAdapter = SizeCardsAdapter(false)
        shoesSizeCardAdapter = SizeCardsAdapter(false)
        with(binding.topSizeCardsList) {
            adapter = topSizeCardAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)

        }
        with(binding.bottomSizeCardsList) {
            adapter = bottomSizeCardAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)

        }
        with(binding.shoesSizeCardsList) {
            adapter = shoesSizeCardAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)

        }
        //presenter.getAllSizes()


    }

    private fun showLoading(loading: Boolean) {
        binding.loader.isVisible = loading
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

        return SizeLine(size.id, int, w, itRuFr, us, uk, size.chosen ?: false, idCategory = -1)


    }

    private fun convertShoeSizeToSizeLine(size: Size): SizeLine {
        return SizeLine(
            size.id,
            size.name,
            size.w.orEmpty(),
            size.fr.orEmpty(),
            size.us.orEmpty(),
            size.uk.orEmpty(),
            size.chosen ?: false,
            idCategory = size.id_category ?: -1
        )
    }
    /*
    val int = size.name
    var w = ""
    var itRuFr = ""
    var us = ""
    var uk = ""
    when (size.name) {
        "35" -> {

        w = "",
                itRuFr = "36",
        us = "5",
        uk = "2"
    }
        ,

            "71" -> {
            w = "35.5",

            w = ""
            itRuFr = "36.5"
            us = "5.5"
            uk = "2.5"
        }

        ,



            "36" -> {
            w = ""
            itRuFr = "37"
            us = "6"
            uk = "3"


        },
        {


            "36.6" ->
                w =  73,
                itRuFr =  "36.5",
            us = 6.5,
            uk = 3.5


            }
        },

    "id": 75,
    "name": "37.5",
    "ico": "FR'38.5';US'7.5';UK'4.5'",
    "id_category": 6

    "37.5" ->
    w = ""
    itRuFr = "38.5"
    us = "7.5"
    uk = "4.5"




           "37" ->
            w
            itRuFr = "38"
            us = "4"
            uk = "4"


        "38" -> {
            w = ""
            itRuFr = "38.5"
            us = "7.5"
            uk = "4.5"
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


    return SizeLine(size.id, int, w, itRuFr, us, uk, size.chosen ?: false)


}*/

    private fun loadFilter() {
        presenter.getFilter()
    }

    private fun bindFilter(filter: Filter) {
        isForKids = filter.chosenForWho[2]
        isForWomen = filter.chosenForWho[0]
        isForMen = filter.chosenForWho[1]

        if (!isSizesLoaded)
            getSizes()

        Log.d("mytest", filter.chosenTopSizes.size.toString())

        when {
            isForWomen -> {
                if (topSizeCardAdapter.getSizes().size == filter.chosenTopSizes.size || topSizeCardAdapter.getSizes().size + 1 == filter.chosenTopSizes.size) {
                    topSizeCardAdapter.updateList(
                        filter.chosenTopSizes.toMutableList().apply {
                            if (filter.chosenTopSizes.size == topSizeCardAdapter.getSizes().size + 1) removeLast() else {
                            }
                        }.toList()
                            .map { Size(it.id, it.int, 1, it.isSelected, it.itRuFr, it.us, it.uk) })
                }
                if (bottomSizeCardAdapter.getSizes().size == filter.chosenBottomSizes.size || bottomSizeCardAdapter.getSizes().size + 1 == filter.chosenBottomSizes.size) {
                    bottomSizeCardAdapter.updateList(
                        filter.chosenBottomSizes.toMutableList().apply {
                            if (filter.chosenBottomSizes.size == bottomSizeCardAdapter.getSizes().size + 1) removeLast() else {
                            }
                        }.toList()
                            .map { Size(it.id, it.int, 2, it.isSelected) })
                }
                if (shoesSizeCardAdapter.getSizes().size == filter.chosenShoosSizes.size || shoesSizeCardAdapter.getSizes().size + 1 == filter.chosenShoosSizes.size) {

                    shoesSizeCardAdapter.updateList(
                        filter.chosenShoosSizes.toMutableList().apply {
                            if (filter.chosenShoosSizes.size == shoesSizeCardAdapter.getSizes().size + 1) removeLast() else {
                            }
                        }.toList()
                            .map { Size(it.id, it.int, 3, it.isSelected) })
                }
            }
            isForMen -> {
                if (topSizeCardAdapter.getSizes().size == filter.chosenTopSizesMen.size || topSizeCardAdapter.getSizes().size + 1 == filter.chosenTopSizesMen.size) {

                    topSizeCardAdapter.updateList(
                        filter.chosenTopSizesMen.toMutableList().apply {
                            if (filter.chosenTopSizesMen.size == topSizeCardAdapter.getSizes().size + 1) removeLast() else {
                            }
                        }.toList()
                            .map { Size(it.id, it.int, 1, it.isSelected) })
                }

                if (bottomSizeCardAdapter.getSizes().size == filter.chosenBottomSizesMen.size || bottomSizeCardAdapter.getSizes().size + 1 == filter.chosenBottomSizesMen.size) {
                    bottomSizeCardAdapter.updateList(
                        filter.chosenBottomSizesMen.toMutableList().apply {
                            if (filter.chosenBottomSizesMen.size == bottomSizeCardAdapter.getSizes().size + 1) removeLast() else {
                            }
                        }.toList()
                            .map { Size(it.id, it.int, 2, it.isSelected) })
                }
                if (shoesSizeCardAdapter.getSizes().size == filter.chosenShoosSizesMen.size || shoesSizeCardAdapter.getSizes().size + 1 == filter.chosenShoosSizesMen.size) {

                    shoesSizeCardAdapter.updateList(
                        filter.chosenShoosSizesMen.toMutableList().apply {
                            if (filter.chosenShoosSizesMen.size == shoesSizeCardAdapter.getSizes().size + 1) removeLast() else {
                            }
                        }.toList()
                            .map { Size(it.id, it.int, 3, it.isSelected) })
                }
            }

            isForKids -> {
                if (topSizeCardAdapter.getSizes().size == filter.chosenTopSizesKids.size || topSizeCardAdapter.getSizes().size + 1 == filter.chosenTopSizesKids.size) {

                    topSizeCardAdapter.updateList(
                        filter.chosenTopSizesKids.toMutableList().apply {
                            if (filter.chosenTopSizesKids.size == topSizeCardAdapter.getSizes().size + 1) removeLast() else {
                            }
                        }.toList()
                            .map { Size(it.id, it.int, 1, it.isSelected) })
                }
                if (bottomSizeCardAdapter.getSizes().size == filter.chosenBottomSizesKids.size || bottomSizeCardAdapter.getSizes().size + 1 == filter.chosenBottomSizesKids.size) {

                    bottomSizeCardAdapter.updateList(
                        filter.chosenBottomSizesKids.toMutableList().apply {
                            if (filter.chosenBottomSizesKids.size == bottomSizeCardAdapter.getSizes().size + 1) removeLast() else {
                            }
                        }.toList()
                            .map { Size(it.id, it.int, 2, it.isSelected) })
                }
                if (shoesSizeCardAdapter.getSizes().size == filter.chosenShoosSizesKids.size || shoesSizeCardAdapter.getSizes().size + 1 == filter.chosenShoosSizesKids.size) {

                    shoesSizeCardAdapter.updateList(
                        filter.chosenShoosSizesKids.toMutableList().apply {
                            if (filter.chosenShoosSizesKids.size == shoesSizeCardAdapter.getSizes().size + 1) removeLast() else {
                            }
                        }.toList()
                            .map { Size(it.id, it.int, 3, it.isSelected) })
                }
            }
        }
    }

    private fun getSizes() {
        when {
            isForWomen -> {
                presenter.getTopSizesWomen()
                presenter.getBottomSizesWomen()
                presenter.getShoosSizesWomen()
            }
            isForMen -> {
                presenter.getTopSizesMen()
                presenter.getBottomSizesMen()
                presenter.getShoosSizesMen()
            }
            isForKids -> {
                presenter.getTopSizesKids()
                presenter.getBottomSizesKids()
                presenter.getShoosSizesKids()
            }
        }
    }

    private fun saveSizes() {
        when {
            isForWomen -> {
                presenter.saveTopSizes(
                    topSizeCardAdapter.getSizes().map { convertSizeToSizeLine(it) })
                presenter.saveBottomSizes(bottomSizeCardAdapter.getSizes()
                    .map { convertSizeToSizeLine(it) })
                presenter.saveShoosSizes(shoesSizeCardAdapter.getSizes()
                    .map { convertShoeSizeToSizeLine(it) })
            }

            isForMen -> {
                presenter.saveTopSizesMen(
                    topSizeCardAdapter.getSizes().map { convertSizeToSizeLine(it) })
                presenter.saveBottomSizesMen(bottomSizeCardAdapter.getSizes()
                    .map { convertSizeToSizeLine(it) })
                presenter.saveShoosSizesMen(shoesSizeCardAdapter.getSizes()
                    .map { convertShoeSizeToSizeLine(it) })
            }

            isForKids -> {
                presenter.saveTopSizesKids(
                    topSizeCardAdapter.getSizes().map { convertSizeToSizeLine(it) })
                presenter.saveBottomSizesKids(bottomSizeCardAdapter.getSizes()
                    .map { convertSizeToSizeLine(it) })
                presenter.saveShoosSizesKids(shoesSizeCardAdapter.getSizes()
                    .map { convertShoeSizeToSizeLine(it) })
            }
        }


    }

    override fun onStop() {
        super.onStop()
        saveSizes()
        //FilterState.filter.chosenTopSizes = topSizeCardAdapter.getSizes().map { convertSizeToSizeLine(it) } + if(FilterState.filter.chosenTopSizes.isNotEmpty()) listOf(FilterState.filter.chosenTopSizes.last()) else listOf(
        //   SizeLine(0, "Другое", "", "", "", "", false)
        // )
        // FilterState.filter.chosenBottomSizes = bottomSizeCardAdapter.getSizes().map { convertSizeToSizeLine(it) } + if(FilterState.filter.chosenBottomSizes.isNotEmpty()) listOf(FilterState.filter.chosenBottomSizes.last()) else listOf(SizeLine(0, "Другое", "", "", "", "", false))
        // FilterState.filter.chosenShoosSizes = shoesSizeCardAdapter.getSizes().map { convertShoeSizeToSizeLine(it) } + if(FilterState.filter.chosenShoosSizes.isNotEmpty()) listOf(FilterState.filter.chosenShoosSizes.last()) else listOf(SizeLine(0, "Другое", "", "", "", "", false))

    }

    override fun success(result: Any) {

    }

    override fun error(message: String) {
        showLoading(false)

    }

    override fun loading() {
        showLoading(true)

    }

    override fun loaded(result: Any) {
        showLoading(false)
        if (result is List<*>) {
            val sizes =
                ((result as List<Property>).map {
                    val list = it.ico?.split(';').orEmpty()
                    it
                })
            //Size(it.id.toInt(), it.name, list[0].removePrefix("W").removeSurrounding("'"), list[1].removePrefix("IT/RU/FR").removeSurrounding("'"), list[2].removePrefix("US").removeSurrounding("'"), list[3].removePrefix("UK").removeSurrounding("'"), false)
            //Size(it.id.toInt(), it.name, it.idCategory.toInt(), false, list[0].removePrefix("FR").removeSurrounding("'"), list[1].removePrefix("US").removeSurrounding("'"), list[2].removePrefix("UK").removeSurrounding("'")) })
            //val bottomSizes = (result).filter { it.id_category == 2 }.sortedBy { it.toInt() }
            //val shoosSizes = (result).filter { it.id_category == 3 }.sortedBy { it.toInt() }
            // if (topSizes.size + 1 == FilterState.filter.chosenTopSizes.size) {
            //   topSizeCardAdapter.updateList(FilterState.filter.chosenTopSizes.map {
            //     Size(
            //       it.id,
            //     it.int,
            //   1,
            // it.isSelected
            // )
            // }.toMutableList().apply { removeLast() })
            //} else {
            if (sizes[0].idCategory?.toInt() == 1 || sizes[0].idCategory?.toInt() == 4 || sizes[0].idCategory?.toInt() == 7)
                topSizeCardAdapter.updateList(sizes.map {
                    val list = it.ico?.split(';').orEmpty()
                    Size(
                        it.id.toInt(),
                        it.name,
                        it.idCategory?.toInt(),
                        false,
                        w = if(list.isNotEmpty()) list[0].removePrefix("W").removeSurrounding("'") else "",
                        fr = if(list.isNotEmpty()) list[1].removePrefix("IT/RU/FR").removeSurrounding("'") else "",
                        us = if(list.isNotEmpty()) list[2].removePrefix("US").removeSurrounding("'")else "",
                        uk = if(list.isNotEmpty()) list[3].removePrefix("UK").removeSurrounding("'") else ""
                    )

                })
            // })
            //}
            //if (bottomSizes.size + 1 == FilterState.filter.chosenBottomSizes.size) {
            //  Log.d("mylog3", FilterState.filter.chosenBottomSizes.size.toString())
            //val list = FilterState.filter.chosenBottomSizes.map {
            //  Size(
            //    it.id,
            //  it.int,
            // 2,
            // it.isSelected
            // )
            //}
            // bottomSizeCardAdapter.updateList(list.toMutableList().apply { removeLast() })
            // Log.d("mylog3", FilterState.filter.chosenBottomSizes.toString())

            //} else {
            //  Log.d("mylog3", FilterState.filter.chosenBottomSizes.size.toString())
            if(sizes[0].idCategory?.toInt() == 2 || sizes[0].idCategory?.toInt() == 5 || sizes[0].idCategory?.toInt() == 8)
            bottomSizeCardAdapter.updateList(sizes.map {
                val list = it.ico?.split(';').orEmpty()
                Size(
                    it.id.toInt(),
                    it.name,
                    it.idCategory?.toInt(),
                    false,
                    w = if(list.isNotEmpty()) list[0].removePrefix("W").removeSurrounding("'") else "",
                    fr = if(list.isNotEmpty()) list[1].removePrefix("IT/RU/FR").removeSurrounding("'") else "",
                    us = if(list.isNotEmpty()) list[2].removePrefix("US").removeSurrounding("'")else "",
                    uk = if(list.isNotEmpty()) list[3].removePrefix("UK").removeSurrounding("'")else ""
                )
            })
            //}

            //  if (shoosSizes.size + 1 == FilterState.filter.chosenShoosSizes.size) {
            //    shoesSizeCardAdapter.updateList(FilterState.filter.chosenShoosSizes.map {
            //      Size(
            //        it.id,
            //      it.int,
            //    3,
            //  it.isSelected
            // )
            // }.toMutableList().apply { removeLast() })
            // } else {
            if (sizes[0].idCategory?.toInt() == 3 || sizes[0].idCategory?.toInt() == 6 || sizes[0].idCategory?.toInt() == 9)
                shoesSizeCardAdapter.updateList(sizes.map {
                    val list = it.ico?.split(';').orEmpty()
                    Size(
                        it.id.toInt(),
                        it.name,
                        it.idCategory?.toInt(),
                        false,
                        fr = if(list.isNotEmpty()) list[0].removePrefix("FR").removeSurrounding("'") else "",
                        us = if(list.isNotEmpty()) list[1].removePrefix("US").removeSurrounding("'") else "" ,
                        uk = if(list.isNotEmpty()) list[2].removePrefix("UK").removeSurrounding("'") else ""
                    )

                })
            //}
            isSizesLoaded = true
            loadFilter()
        }

        if (result is Filter) {
            bindFilter(result)

        }

    }

    override fun successNewCode() {

    }
}