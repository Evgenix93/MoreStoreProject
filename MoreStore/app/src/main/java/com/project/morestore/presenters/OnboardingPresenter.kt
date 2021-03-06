package com.project.morestore.presenters

import android.content.Context
import android.util.Log
import com.project.morestore.models.ProductBrand


import com.project.morestore.models.Size
import com.project.morestore.mvpviews.OnBoardingMvpView
import com.project.morestore.repositories.AuthRepository


import com.project.morestore.repositories.ProductRepository
import com.project.morestore.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpPresenter
import moxy.presenterScope
import okhttp3.ResponseBody


class OnboardingPresenter(context: Context) : MvpPresenter<OnBoardingMvpView>() {
    private val repository = ProductRepository(context)
    private val authRepository = AuthRepository(context)
    private val userRepository = UserRepository(context)

    private val categoryIdList = mutableListOf<Int>()

    fun getAllSizes() {
        presenterScope.launch {
            viewState.loading()
            val response = repository.getAllSizes()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
                400 -> {
                    val bodyString = getStringFromResponse(response.errorBody()!!)
                    viewState.error(bodyString)
                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("нет интернета")
                else -> viewState.error("ошибка")

            }

        }

    }

    private fun getProperties(propertyId: Long) {
        presenterScope.launch {
            viewState.loading()
            val response = repository.getProperties()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!.filter { it.idCategory == propertyId })
                400 -> {
                    val bodyString = getStringFromResponse(response.errorBody()!!)
                    viewState.error(bodyString)
                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("нет интернета")
                else -> viewState.error("ошибка")

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
                400 -> {
                    val bodyString = getStringFromResponse(response.errorBody()!!)
                    viewState.error(bodyString)
                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("Нет интернета")
                else -> viewState.error("Ошибка")
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

    fun saveOnBoardingViewed() {
        presenterScope.launch {
            repository.saveOnBoardingViewed()
        }
    }

    fun changeToGuestMode() {
        presenterScope.launch {
            authRepository.clearToken()
        }
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
            Log.d("MyDebug", "segments = ${filter.segments}")
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
            Log.d("MyDebug", "luxBrands = $luxBrands")
            val response = userRepository.saveBrandsProperties(luxBrands + middleBrands + massBrands + ecoBrands, propertiesId)
            when (response?.code()) {
                200 -> saveFilter(isMale)
                null -> viewState.error("Ошибка")
            }
        }
    }

    fun saveOnBoardingData(brandsId: List<Long>?, propertiesId: List<Long>){
        presenterScope.launch {
            val response = userRepository.saveBrandsProperties(brandsId, propertiesId)
            when (response?.code()) {
                200 -> viewState.success()
                null -> viewState.error("Ошибка")
            }
        }
    }

    fun loadOnboardingData(){
        viewState.loading()
        presenterScope.launch {
            val response = userRepository.loadBrandsProperties()
            when(response?.code()){
                200 -> {
                    Log.d("MyDebug", "load brandsPropert success")
                    viewState.loaded(response.body()!!)
                }
                null -> viewState.error("Ошибка")
                else -> {
                    viewState.error("Ошибка")
                }
            }
        }
    }

    fun getAllBrands(){
        viewState.loading()
        presenterScope.launch {
            val response = repository.getBrands()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
                400 -> {
                    val bodyString = getStringFromResponse(response.errorBody()!!)
                    viewState.error(bodyString)
                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("Нет интернета")
                else -> viewState.error("Ошибка")
            }


        }
    }

    private suspend fun getAllBrandsInstant(): List<ProductBrand>{


            val response = repository.getBrands()
            when (response?.code()) {
                200 -> return response.body()!!
                400 -> {
                    val bodyString = getStringFromResponse(response.errorBody()!!)
                    viewState.error(bodyString)
                    return emptyList()
                }
                500 -> {
                    viewState.error("500 Internal Server Error")
                    return emptyList()
                }
                null -> {
                    viewState.error("Нет интернета")
                    return emptyList()
                }
                else -> {
                    viewState.error("Ошибка")
                    return emptyList()
                }
            }



    }


    private suspend fun getStringFromResponse(body: ResponseBody): String {
        return withContext(Dispatchers.IO) {
            val str = body.string()
            Log.d("mylog", str)
            str
        }
    }
}