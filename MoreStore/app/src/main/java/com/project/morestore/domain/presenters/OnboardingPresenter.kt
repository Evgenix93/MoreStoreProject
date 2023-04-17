package com.project.morestore.domain.presenters

import android.util.Log
import com.project.morestore.data.models.ProductBrand


import com.project.morestore.data.models.Size
import com.project.morestore.presentation.mvpviews.OnBoardingMvpView


import com.project.morestore.data.repositories.ProductRepository
import com.project.morestore.data.repositories.UserRepository
import com.project.morestore.util.errorMessage
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject


class OnboardingPresenter @Inject constructor(
    private val repository: ProductRepository,
    private val userRepository: UserRepository
) : MvpPresenter<OnBoardingMvpView>() {

    private val categoryIdList = mutableListOf<Int>()

    private fun getProperties(propertyId: Long) {
        presenterScope.launch {
            viewState.loading()
            val response = repository.getProperties()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!.filter { it.idCategory == propertyId })
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun getTopSizes() {
        getProperties(4)
    }

    fun getBottomSizes() {
        getProperties(5)
    }

    fun getShoosSizes() {
        getProperties(6)
    }

    fun getTopSizesMen() {
        getProperties(1)
    }

    fun getBottomSizesMen() {
        getProperties(2)
    }

    fun getShoosSizesMen() {
        getProperties(3)
    }

    fun getTopSizesKids() {
        getProperties(7)
    }

    fun getBottomSizesKids() {
        getProperties(8)
    }

    fun getShoosSizesKids() {
        getProperties(9)
    }

    fun saveSizes(
        topSizes: List<Size>,
        bottomSizes: List<Size>,
        shoesSizes: List<Size>,
        isMale: Boolean
    ) {
        presenterScope.launch {
            viewState.loading()
            repository.saveSizes(topSizes, bottomSizes, shoesSizes, isMale)
            viewState.success()
        }
    }


    fun getCategories() {
        presenterScope.launch {
            val response = repository.getCategories()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun saveCategories(segmentsChecked: List<Boolean>) {
        repository.safeCategories(segmentsChecked)
        viewState.success()
    }

    fun addRemoveCategoryId(id: Int, isChecked: Boolean) {
        if (isChecked)
            categoryIdList.add(id)
        else
            categoryIdList.remove(id)
    }

    fun saveFilter(isMale: Boolean) {
        presenterScope.launch {
            val filter = userRepository.getFilter()
            if (isMale)
                filter.chosenForWho = listOf(false, true, false)
            else
                filter.chosenForWho = listOf(true, false, false)
            userRepository.updateFilter(filter)

            if (userRepository.saveFilter())
                viewState.success()
            else
                viewState.error("Ошибка")
        }
    }

    fun saveOnboardingData(isMale: Boolean) {
        viewState.loading()
        presenterScope.launch {
            val filter = userRepository.getFilter()
            val forWho = if (isMale)
                listOf(false, true, false)
            else
                listOf(true, false, false)
            val propertiesId = if (isMale.not()) {
                filter.chosenTopSizesWomen.filter{it.isSelected}.map { it.id.toLong() }
            } else {
                filter.chosenTopSizesMen.filter{it.isSelected}.map { it.id.toLong() }
            } + if (isMale.not()) {
                filter.chosenBottomSizesWomen.filter{it.isSelected}.map { it.id.toLong() }
            } else {
                filter.chosenBottomSizesMen.filter{it.isSelected}.map { it.id.toLong() }
            } + if (isMale.not()) {
                filter.chosenShoosSizesWomen.filter{it.isSelected}.map { it.id.toLong() }
            } else {
                filter.chosenShoosSizesMen.filter{it.isSelected}.map { it.id.toLong() }
            } + forWho.mapIndexedNotNull { index, checked ->
                if (checked) {
                    when (index) {
                        0 -> 140L
                        1 -> 141L
                        2 -> 142L
                        else -> null
                    }
                } else
                    null
            }

            val brands = getAllBrandsInstant()
            val luxBrands = mutableListOf<Long>()
            val middleBrands = mutableListOf<Long>()
            val massBrands = mutableListOf<Long>()
            val ecoBrands = mutableListOf<Long>()
            filter.segments.forEachIndexed { index, selected ->
                 if(selected && index == 0) {
                    Log.d("MyDebug", "segments isNotEmpty")
                   luxBrands.addAll(brands.filter { it.idCategory == (index + 1).toLong() }.map { it.id })
                }
                 if(selected && index == 1)
                   middleBrands.addAll(brands.filter { it.idCategory == (index + 1).toLong() }.map { it.id })

                 if(selected && index == 2)
                    massBrands.addAll(brands.filter { it.idCategory == (index + 1).toLong() }.map { it.id })

                 if(selected && index == 3)
                   ecoBrands.addAll( brands.filter { it.idCategory == (index + 1).toLong() }.map { it.id })

            }

            val response = userRepository.saveBrandsProperties(luxBrands + middleBrands + massBrands + ecoBrands, propertiesId)
            when (response?.code()) {
                200 -> saveFilter(isMale)
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun saveOnBoardingData(brandsId: List<Long>?, propertiesId: List<Long>){
        presenterScope.launch {
            val response = userRepository.saveBrandsProperties(brandsId, propertiesId)
            when (response?.code()) {
                200 -> viewState.success()
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun loadOnboardingData(){
        viewState.loading()
        presenterScope.launch {
            val response = userRepository.loadBrandsProperties()
            when(response?.code()){
                200 -> {
                    viewState.loaded(response.body()!!)
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun getAllBrands(){
        viewState.loading()
        presenterScope.launch {
            val response = repository.getBrands()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    private suspend fun getAllBrandsInstant(): List<ProductBrand>{

            val response = repository.getBrands()
            when (response?.code()) {
                200 -> return response.body()!!
                else -> {
                    viewState.error(errorMessage(response))
                    return emptyList()
                }
            }
    }
}