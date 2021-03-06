package com.project.morestore.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.adapters.SizeCardsAdapter
import com.project.morestore.databinding.FragmentOnboarding2Binding
import com.project.morestore.models.*
import com.project.morestore.mvpviews.OnBoardingMvpView
import com.project.morestore.presenters.OnboardingPresenter
import com.project.morestore.util.autoCleared
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class Onboarding2Fragment : MvpAppCompatFragment(R.layout.fragment_onboarding2), OnBoardingMvpView {
    private val binding: FragmentOnboarding2Binding by viewBinding()
    private val args: Onboarding2FragmentArgs by navArgs()
    private val presenter by moxyPresenter { OnboardingPresenter(requireContext()) }
    private var topSizeCardAdapter: SizeCardsAdapter by autoCleared()
    private var bottomSizeCardAdapter: SizeCardsAdapter by autoCleared()
    private var shoesSizeCardAdapter: SizeCardsAdapter by autoCleared()
    private lateinit var brandsPropertiesDataWrapper: BrandsPropertiesDataWrapper
    private var forWhoIds = listOf<Long>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLists()
        initContinueButton()
        setClickListeners()
        loadOnboardingData()
    }

    private fun initContinueButton() {
        if (args.fromProfile)
            binding.continueBtn.text = "Сохранить"
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

    private fun setClickListeners() {
        binding.continueBtn.setOnClickListener {
            if (args.fromProfile) {
                val propertyIds = topSizeCardAdapter.getSizes().filter { it.chosen == true }
                    .map { it.id.toLong() } + bottomSizeCardAdapter.getSizes().filter { it.chosen == true }
                    .map { it.id.toLong() } + shoesSizeCardAdapter.getSizes().filter { it.chosen == true }
                    .map { it.id.toLong() } +  forWhoIds
                val brandsIds = brandsPropertiesDataWrapper.data.brand?.split(
                    ';'
                )?.mapNotNull { it.toLongOrNull() }.orEmpty()
                presenter.saveOnBoardingData(brandsIds, propertyIds)
            } else
                presenter.saveSizes(
                    topSizeCardAdapter.getSizes(),
                    bottomSizeCardAdapter.getSizes(),
                    shoesSizeCardAdapter.getSizes(),
                    args.isMale
                )
        }
        binding.backIcon.setOnClickListener { findNavController().popBackStack() }
    }

    private fun showLoading(loading: Boolean) {
        binding.continueBtn.isEnabled = !loading
        binding.loader.isVisible = loading
    }

    private fun getAllSizes(forWhoIds: List<Long>) {
        when(forWhoIds[0]) {
            140L -> { presenter.getTopSizes()
                presenter.getBottomSizes()
                presenter.getShoosSizes()
            }
            141L -> {
                presenter.getTopSizesMen()
                presenter.getBottomSizesMen()
                presenter.getShoosSizesMen()
            }
            142L  -> {
                Log.d("MyKids", "isKids")
                presenter.getTopSizesKids()
                presenter.getBottomSizesKids()
                presenter.getShoosSizesKids()
            }
        }
    }

    private fun loadOnboardingData() {
        if (args.fromProfile)
            presenter.loadOnboardingData()
        else
            getAllSizes(if(args.isMale)listOf(141L) else listOf(140L))
    }

    override fun loading() {
        showLoading(true)

    }

    override fun loaded(result: List<Any>) {
        showLoading(false)
        if (result[0] is Property) {
            val categoryId = (result[0] as Property).idCategory?.toInt()
            if (categoryId == 4 || categoryId == 1)
                topSizeCardAdapter.updateList((result as List<Property>).map {
                    val list = it.ico?.split(';').orEmpty()
                    val sizeLine = SizeLine(
                        it.id.toInt(),
                        it.name,
                        list[0].removePrefix("W").removeSurrounding("'"),
                        list[1].removePrefix("IT/RU/FR").removeSurrounding("'"),
                        list[2].removePrefix("US").removeSurrounding("'"),
                        list[3].removePrefix("UK").removeSurrounding("'"),
                        false,
                        idCategory = it.idCategory?.toInt()!!
                    )
                    Size(
                        it.id.toInt(),
                        it.name,
                        it.idCategory.toInt(),
                        if (args.fromProfile)
                            brandsPropertiesDataWrapper.data.property?.split(';')
                                ?.mapNotNull { string -> string.toLongOrNull() }
                                ?.any { id -> id == it.id }
                        else
                            false,
                        w = sizeLine.w,
                        fr = sizeLine.itRuFr,
                        uk = sizeLine.uk,
                        us = sizeLine.us
                    )
                })

            if (categoryId == 5 || categoryId == 2)
                bottomSizeCardAdapter.updateList((result as List<Property>).map {
                    val list = it.ico?.split(';').orEmpty()
                    val sizeLine = SizeLine(
                        it.id.toInt(),
                        it.name,
                        list[0].removePrefix("W").removeSurrounding("'"),
                        list[1].removePrefix("IT/RU/FR").removeSurrounding("'"),
                        list[2].removePrefix("US").removeSurrounding("'"),
                        list[3].removePrefix("UK").removeSurrounding("'"),
                        false,
                        idCategory = it.idCategory?.toInt()!!
                    )
                    Size(
                        it.id.toInt(),
                        it.name,
                        it.idCategory.toInt(),
                        if (args.fromProfile)
                            brandsPropertiesDataWrapper.data.property?.split(';')
                                ?.mapNotNull { string -> string.toLongOrNull() }
                                ?.any { id -> id == it.id }
                        else
                            false,
                        w = sizeLine.w,
                        fr = sizeLine.itRuFr,
                        uk = sizeLine.uk,
                        us = sizeLine.us
                    )
                })

            if (categoryId == 6 || categoryId == 3)
                shoesSizeCardAdapter.updateList((result as List<Property>).map {
                    val list = it.ico?.split(';').orEmpty()
                    val sizeLine = SizeLine(
                        it.id.toInt(),
                        it.name,
                        null,
                        // list[0].removePrefix("W").removeSurrounding("'"),
                        list[0].removePrefix("FR").removeSurrounding("'"),
                        list[1].removePrefix("US").removeSurrounding("'"),
                        list[2].removePrefix("UK").removeSurrounding("'"),
                        false,
                        idCategory = it.idCategory?.toInt()!!
                    )
                    Size(
                        it.id.toInt(),
                        it.name,
                        it.idCategory.toInt(),
                        if (args.fromProfile)
                            brandsPropertiesDataWrapper.data.property?.split(';')
                                ?.mapNotNull { string -> string.toLongOrNull() }
                                ?.any { id -> id == it.id }
                        else false,
                        w = sizeLine.w,
                        fr = sizeLine.itRuFr,
                        uk = sizeLine.uk,
                        us = sizeLine.us
                    )
                })
            if(categoryId == 7)
                topSizeCardAdapter.updateList((result as List<Property>).map {
                    val list = it.ico?.split(';').orEmpty()
                    Size(
                        it.id.toInt(),
                        it.name,
                        it.idCategory?.toInt(),
                        if (args.fromProfile)
                            brandsPropertiesDataWrapper.data.property?.split(';')
                                ?.mapNotNull { string -> string.toLongOrNull() }
                                ?.any { id -> id == it.id }
                        else
                            false,
                        w = if(list.isNotEmpty()) list[0].removePrefix("W").removeSurrounding("'") else "",
                        fr = if(list.isNotEmpty()) list[1].removePrefix("IT/RU/FR").removeSurrounding("'") else "",
                        us = if(list.isNotEmpty()) list[2].removePrefix("US").removeSurrounding("'")else "",
                        uk = if(list.isNotEmpty()) list[3].removePrefix("UK").removeSurrounding("'") else ""
                    )

                })
            if(categoryId == 8)
                bottomSizeCardAdapter.updateList((result as List<Property>).map {
                    val list = it.ico?.split(';').orEmpty()
                    Size(
                        it.id.toInt(),
                        it.name,
                        it.idCategory?.toInt(),
                        if (args.fromProfile)
                            brandsPropertiesDataWrapper.data.property?.split(';')
                                ?.mapNotNull { string -> string.toLongOrNull() }
                                ?.any { id -> id == it.id }
                        else
                            false,
                        w = if(list.isNotEmpty()) list[0].removePrefix("W").removeSurrounding("'") else "",
                        fr = if(list.isNotEmpty()) list[1].removePrefix("IT/RU/FR").removeSurrounding("'") else "",
                        us = if(list.isNotEmpty()) list[2].removePrefix("US").removeSurrounding("'")else "",
                        uk = if(list.isNotEmpty()) list[3].removePrefix("UK").removeSurrounding("'")else ""
                    )
                })
            if(categoryId == 9)
                shoesSizeCardAdapter.updateList((result as List<Property>).map {
                    val list = it.ico?.split(';').orEmpty()
                    Size(
                        it.id.toInt(),
                        it.name,
                        it.idCategory?.toInt(),
                        if (args.fromProfile)
                            brandsPropertiesDataWrapper.data.property?.split(';')
                                ?.mapNotNull { string -> string.toLongOrNull() }
                                ?.any { id -> id == it.id }
                        else
                            false,
                        fr = if(list.isNotEmpty()) list[0].removePrefix("FR").removeSurrounding("'") else "",
                        us = if(list.isNotEmpty()) list[1].removePrefix("US").removeSurrounding("'") else "" ,
                        uk = if(list.isNotEmpty()) list[2].removePrefix("UK").removeSurrounding("'") else ""
                    )

                })
        } else {
            brandsPropertiesDataWrapper = (result as List<BrandsPropertiesDataWrapper>).last()
             forWhoIds = brandsPropertiesDataWrapper.data.property?.split(';')
                ?.mapNotNull { it.toLongOrNull() }?.filter {
                it == 140L || it == 141L || it == 142L
            }.orEmpty()
            getAllSizes(forWhoIds)
        }
    }

    override fun error(message: String) {
        showLoading(false)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun success() {
        showLoading(false)
        if (args.fromProfile)
            findNavController().popBackStack()
        else
            findNavController().navigate(
                Onboarding2FragmentDirections.actionOnboarding2FragmentToOnboarding3Fragment(
                    args.isMale
                )
            )
    }
}