package com.project.morestore.presenters

import android.content.Context
import android.util.Log
import com.project.morestore.models.BrandsPropertiesData


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

    fun getProperties(propertyId: Long) {
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

    fun safeCategories(segmentsChecked: List<Boolean>) {
        /* presenterScope.launch {
            if (repository.safeCategories(categoryIdList))
                viewState.success()
             else
                 viewState.error("Ошибка")
         }*/
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
        authRepository.clearToken()
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
                filter.chosenTopSizes.map { it.id.toLong() }
            } else {
                filter.chosenTopSizesMen.map { it.id.toLong() }
            } + if (isMale.not()) {
                filter.chosenBottomSizes.map { it.id.toLong() }
            } else {
                filter.chosenBottomSizesMen.map { it.id.toLong() }
            } + if (isMale.not()) {
                filter.chosenShoosSizes.map { it.id.toLong() }
            } else {
                filter.chosenShoosSizesMen.map { it.id.toLong() }
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

            val response = userRepository.saveBrandsProperties(
                BrandsPropertiesData(
                    "https://morestore.app-rest.ru/api/v1",
                    null,
                    propertiesId
                )
            )
            when (response?.code()) {
                200 -> saveFilter(isMale)
                null -> viewState.error("Ошибка")
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