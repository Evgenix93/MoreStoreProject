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
import com.project.morestore.models.Size
import com.project.morestore.models.SizeLine
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.presenters.UserPresenter
import com.project.morestore.singletones.FilterState
import com.project.morestore.util.autoCleared
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class FilterKidsSizesFragment: MvpAppCompatFragment(R.layout.fragment_filter_sizes_kids), UserMvpView {
    private val binding: FragmentFilterSizesKidsBinding by viewBinding()
    private var topSizeCardAdapter: SizeCardsAdapter by autoCleared()
    private var bottomSizeCardAdapter: SizeCardsAdapter by autoCleared()
    private var shoesSizeCardAdapter: SizeCardsAdapter by autoCleared()
    private val presenter by moxyPresenter { UserPresenter(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initLists()
    }



    private fun initToolbar(){
        binding.toolbar.titleTextView.text = "Размер"
        binding.toolbar.actionTextView.text = "Сбросить"
        binding.toolbar.imageView2.setOnClickListener { findNavController().popBackStack() }
    }

    private fun initLists(){
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
        presenter.getAllSizes()


    }

    private fun showLoading(loading: Boolean){
        binding.loader.isVisible = loading
    }

    private fun convertSizeToSizeLine(size: Size): SizeLine{
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

    private fun convertShoeSizeToSizeLine(size: Size): SizeLine{
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

    override fun onStop() {
        super.onStop()
        FilterState.filter.chosenTopSizes = topSizeCardAdapter.getSizes().map { convertSizeToSizeLine(it) } + if(FilterState.filter.chosenTopSizes.isNotEmpty()) listOf(FilterState.filter.chosenTopSizes.last()) else listOf(
            SizeLine(0, "Другое", "", "", "", "", false)
        )
        FilterState.filter.chosenBottomSizes = bottomSizeCardAdapter.getSizes().map { convertSizeToSizeLine(it) } + if(FilterState.filter.chosenBottomSizes.isNotEmpty()) listOf(FilterState.filter.chosenBottomSizes.last()) else listOf(SizeLine(0, "Другое", "", "", "", "", false))
        FilterState.filter.chosenShoosSizes = shoesSizeCardAdapter.getSizes().map { convertShoeSizeToSizeLine(it) } + if(FilterState.filter.chosenShoosSizes.isNotEmpty()) listOf(FilterState.filter.chosenShoosSizes.last()) else listOf(SizeLine(0, "Другое", "", "", "", "", false))

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
        val topSizes = (result as List<Size>).filter { it.id_category == 1 }.sortedBy { it.toInt() }
        val bottomSizes = (result).filter { it.id_category == 2 }.sortedBy { it.toInt() }
        val shoosSizes = (result).filter { it.id_category == 3 }.sortedBy { it.toInt() }
        if(topSizes.size + 1 == FilterState.filter.chosenTopSizes.size){
            topSizeCardAdapter.updateList(FilterState.filter.chosenTopSizes.map { Size(it.id, it.int, 1, it.isSelected) }.toMutableList().apply { removeLast() })
        }else{
            topSizeCardAdapter.updateList(topSizes)
        }
        if(bottomSizes.size + 1 == FilterState.filter.chosenBottomSizes.size){
            Log.d("mylog3", FilterState.filter.chosenBottomSizes.size.toString())
            val list = FilterState.filter.chosenBottomSizes.map { Size(it.id, it.int, 2, it.isSelected) }
            bottomSizeCardAdapter.updateList(list.toMutableList().apply { removeLast() })
            Log.d("mylog3", FilterState.filter.chosenBottomSizes.toString())

        }else{
            Log.d("mylog3", FilterState.filter.chosenBottomSizes.size.toString())

            bottomSizeCardAdapter.updateList(bottomSizes)
        }

        if(shoosSizes.size + 1 == FilterState.filter.chosenShoosSizes.size){
            shoesSizeCardAdapter.updateList(FilterState.filter.chosenShoosSizes.map { Size(it.id, it.int, 3, it.isSelected) }.toMutableList().apply { removeLast() })
        }else{
            shoesSizeCardAdapter.updateList(shoosSizes)
        }

    }

    override fun successNewCode() {

    }
}