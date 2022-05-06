package com.project.morestore.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
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
        binding.toolbar.arrowBackImageView.setOnClickListener { findNavController().popBackStack() }
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



    }

    private fun showLoading(loading: Boolean) {
        binding.loader.isVisible = loading
    }

    private fun convertSizeToSizeLine(size: Size): SizeLine {
        return SizeLine(size.id, size.name, size.w, size.fr, size.us, size.uk, size.chosen ?: false, idCategory = size.id_category ?: -1)


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

    private fun loadFilter() {
        presenter.getFilter()
    }

    private fun bindFilter(filter: Filter) {
        isForKids = filter.chosenForWho[2]
        isForWomen = filter.chosenForWho[0]
        isForMen = filter.chosenForWho[1]

        if (!isSizesLoaded)
            getSizes()

        when {
            isForWomen -> {
                if (topSizeCardAdapter.getSizes().size == filter.chosenTopSizesWomen.size || topSizeCardAdapter.getSizes().size + 1 == filter.chosenTopSizesWomen.size) {
                    topSizeCardAdapter.updateList(
                        filter.chosenTopSizesWomen.toMutableList().apply {
                            if (filter.chosenTopSizesWomen.size == topSizeCardAdapter.getSizes().size + 1) removeLast() else {
                            }
                        }.toList()
                            .map { Size(it.id, it.int.orEmpty(), 4, it.isSelected, it.itRuFr, it.us, it.uk) })
                }
                if (bottomSizeCardAdapter.getSizes().size == filter.chosenBottomSizesWomen.size || bottomSizeCardAdapter.getSizes().size + 1 == filter.chosenBottomSizesWomen.size) {
                    bottomSizeCardAdapter.updateList(
                        filter.chosenBottomSizesWomen.toMutableList().apply {
                            if (filter.chosenBottomSizesWomen.size == bottomSizeCardAdapter.getSizes().size + 1) removeLast() else {
                            }
                        }.toList()
                            .map { Size(it.id, it.int.orEmpty(), 5, it.isSelected) })
                }
                if (shoesSizeCardAdapter.getSizes().size == filter.chosenShoosSizesWomen.size || shoesSizeCardAdapter.getSizes().size + 1 == filter.chosenShoosSizesWomen.size) {

                    shoesSizeCardAdapter.updateList(
                        filter.chosenShoosSizesWomen.toMutableList().apply {
                            if (filter.chosenShoosSizesWomen.size == shoesSizeCardAdapter.getSizes().size + 1) removeLast() else {
                            }
                        }.toList()
                            .map { Size(it.id, it.int.orEmpty(), 6, it.isSelected) })
                }
            }
            isForMen -> {
                if (topSizeCardAdapter.getSizes().size == filter.chosenTopSizesMen.size || topSizeCardAdapter.getSizes().size + 1 == filter.chosenTopSizesMen.size) {

                    topSizeCardAdapter.updateList(
                        filter.chosenTopSizesMen.toMutableList().apply {
                            if (filter.chosenTopSizesMen.size == topSizeCardAdapter.getSizes().size + 1) removeLast() else {
                            }
                        }.toList()
                            .map { Size(it.id, it.int.orEmpty(), 1, it.isSelected) })
                }

                if (bottomSizeCardAdapter.getSizes().size == filter.chosenBottomSizesMen.size || bottomSizeCardAdapter.getSizes().size + 1 == filter.chosenBottomSizesMen.size) {
                    bottomSizeCardAdapter.updateList(
                        filter.chosenBottomSizesMen.toMutableList().apply {
                            if (filter.chosenBottomSizesMen.size == bottomSizeCardAdapter.getSizes().size + 1) removeLast() else {
                            }
                        }.toList()
                            .map { Size(it.id, it.int.orEmpty(), 2, it.isSelected) })
                }
                if (shoesSizeCardAdapter.getSizes().size == filter.chosenShoosSizesMen.size || shoesSizeCardAdapter.getSizes().size + 1 == filter.chosenShoosSizesMen.size) {

                    shoesSizeCardAdapter.updateList(
                        filter.chosenShoosSizesMen.toMutableList().apply {
                            if (filter.chosenShoosSizesMen.size == shoesSizeCardAdapter.getSizes().size + 1) removeLast() else {
                            }
                        }.toList()
                            .map { Size(it.id, it.int.orEmpty(), 3, it.isSelected) })
                }
            }

            isForKids -> {
                if (topSizeCardAdapter.getSizes().size == filter.chosenTopSizesKids.size || topSizeCardAdapter.getSizes().size + 1 == filter.chosenTopSizesKids.size) {

                    topSizeCardAdapter.updateList(
                        filter.chosenTopSizesKids.toMutableList().apply {
                            if (filter.chosenTopSizesKids.size == topSizeCardAdapter.getSizes().size + 1) removeLast() else {
                            }
                        }.toList()
                            .map { Size(it.id, it.int.orEmpty(), 7, it.isSelected) })
                }
                if (bottomSizeCardAdapter.getSizes().size == filter.chosenBottomSizesKids.size || bottomSizeCardAdapter.getSizes().size + 1 == filter.chosenBottomSizesKids.size) {

                    bottomSizeCardAdapter.updateList(
                        filter.chosenBottomSizesKids.toMutableList().apply {
                            if (filter.chosenBottomSizesKids.size == bottomSizeCardAdapter.getSizes().size + 1) removeLast() else {
                            }
                        }.toList()
                            .map { Size(it.id, it.int.orEmpty(), 8, it.isSelected) })
                }
                if (shoesSizeCardAdapter.getSizes().size == filter.chosenShoosSizesKids.size || shoesSizeCardAdapter.getSizes().size + 1 == filter.chosenShoosSizesKids.size) {

                    shoesSizeCardAdapter.updateList(
                        filter.chosenShoosSizesKids.toMutableList().apply {
                            if (filter.chosenShoosSizesKids.size == shoesSizeCardAdapter.getSizes().size + 1) removeLast() else {
                            }
                        }.toList()
                            .map { Size(it.id, it.int.orEmpty(), 9, it.isSelected) })
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