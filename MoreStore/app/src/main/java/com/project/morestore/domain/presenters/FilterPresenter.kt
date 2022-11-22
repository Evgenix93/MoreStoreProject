package com.project.morestore.domain.presenters

import com.project.morestore.data.models.*
import com.project.morestore.data.repositories.ProductRepository
import com.project.morestore.data.repositories.UserRepository
import com.project.morestore.presentation.mvpviews.FilterView
import com.project.morestore.util.errorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpPresenter
import moxy.presenterScope

class FilterPresenter(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository
): MvpPresenter<FilterView> (){
    private var searchJob: Job? = null
    private var searchJob3: Job? = null

    fun clearSizes(){
        val filter = userRepository.getFilter()
        filter.chosenTopSizesWomen = emptyList()
        filter.chosenBottomSizesWomen = emptyList()
        filter.chosenShoosSizesWomen = emptyList()
        filter.chosenTopSizesMen = emptyList()
        filter.chosenBottomSizesMen = emptyList()
        filter.chosenShoosSizesMen = emptyList()
        filter.chosenTopSizesKids = emptyList()
        filter.chosenBottomSizesKids = emptyList()
        filter.chosenShoosSizesKids = emptyList()
        userRepository.updateFilter(filter)
    }

    fun saveCategories(productCategories: List<ProductCategory>) {
        val filter = userRepository.getFilter()
        filter.categories = productCategories
        userRepository.updateFilter(filter)
    }

    fun getProductCategories() {
        presenterScope.launch {
            val response = productRepository.getProductCategories()
            when (response?.code()) {
                200 -> {
                    val filter = userRepository.getFilter()
                    filter.chosenForWho.forEachIndexed { index, isChecked ->
                        if (isChecked)
                            when (index) {
                                0 -> {
                                    viewState.loaded(response.body()!!.filterNot {
                                        it.id == 8 || it.id == 21 || it.id == 22
                                    })
                                }
                                1 -> {
                                    viewState.loaded(response.body()!!.filterNot {
                                        it.id == 4 || it.id == 6 || it.id == 7 || it.id == 10 ||
                                                it.id == 18 || it.id == 21 || it.id == 22
                                    })
                                }
                                2 -> {
                                    viewState.loaded(response.body()!!.filterNot {
                                        it.id == 4 || it.id == 6 || it.id == 7 || it.id == 10 ||
                                                it.id == 18
                                    })
                                }
                            }
                    }
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun getFilter() {
        viewState.loaded(userRepository.getFilter())
    }

    fun saveColors(colors: List<Property>) {
        val filter = userRepository.getFilter()
        filter.colors = colors
        userRepository.updateFilter(filter)
    }

    private fun getProperties(propertyId: Long) {
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.getProperties()
            when (response?.code()) {
                200 -> {
                    viewState.loaded(response.body()!!.filter { it.idCategory == propertyId })
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun getColors() {
        getProperties(12)
    }

    fun loadConditions() {
        val conditions = userRepository.loadConditions()
        if (conditions.isNotEmpty()) {
            viewState.loaded(conditions)
        }
    }

    fun saveConditions(conditions: List<Boolean>) {
        userRepository.saveConditions(conditions)
    }

    fun saveForWho(forWho: List<Boolean>) {
        val filter = userRepository.getFilter()
        filter.chosenForWho = forWho
        userRepository.updateFilter(filter)
    }

    fun getAllSizesWomen(){
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.getProperties()
            when (response?.code()) {
                200 -> {
                    val topSizes = response.body()!!.filter { it.idCategory == 4L }
                    val bottomSizes = response.body()!!.filter { it.idCategory == 5L }
                    val shoesSizes = response.body()!!.filter { it.idCategory == 6L }
                    viewState.loaded(topSizes)
                    viewState.loaded(bottomSizes)
                    viewState.loaded(shoesSizes)
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun getAllSizesMen(){
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.getProperties()
            when (response?.code()) {
                200 -> {
                    val topSizes = response.body()!!.filter { it.idCategory == 1L }
                    val bottomSizes = response.body()!!.filter { it.idCategory == 2L }
                    val shoesSizes = response.body()!!.filter { it.idCategory == 3L }
                    viewState.loaded(topSizes)
                    viewState.loaded(bottomSizes)
                    viewState.loaded(shoesSizes)
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun getAllSizesKids(){
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.getProperties()
            when (response?.code()) {
                200 -> {
                    val topSizes = response.body()!!.filter { it.idCategory == 7L }
                    val bottomSizes = response.body()!!.filter { it.idCategory == 8L }
                    val shoesSizes = response.body()!!.filter { it.idCategory == 9L }
                    viewState.loaded(topSizes)
                    viewState.loaded(bottomSizes)
                    viewState.loaded(shoesSizes)
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun saveTopSizes(sizes: List<SizeLine>) {
        val filter = userRepository.getFilter().apply {
            chosenTopSizesWomen =
                if (sizes.size >= chosenTopSizesWomen.size) sizes else sizes + if (chosenTopSizesWomen.isNotEmpty()) listOf(
                    chosenTopSizesWomen.last()
                ) else listOf(SizeLine(0, "", "", "", "", "", false, idCategory = -1))
        }
        userRepository.updateFilter(filter)

    }

    fun saveBottomSizes(sizes: List<SizeLine>) {
        val filter = userRepository.getFilter().apply {
            chosenBottomSizesWomen =
                if (sizes.size >= chosenBottomSizesWomen.size) sizes else sizes + if (chosenBottomSizesWomen.isNotEmpty()) listOf(
                    chosenBottomSizesWomen.last()
                ) else listOf(
                    SizeLine(0, "", "", "", "", "", false, idCategory = -1)
                )

        }
        userRepository.updateFilter(filter)
    }

    fun saveShoosSizes(sizes: List<SizeLine>) {
        val filter = userRepository.getFilter().apply {
            chosenShoosSizesWomen =
                if (sizes.size >= chosenShoosSizesWomen.size) sizes else sizes + if (chosenShoosSizesWomen.isNotEmpty()) listOf(
                    chosenShoosSizesWomen.last()
                ) else listOf(SizeLine(0, "", "", "", "", "", false, idCategory = -1))
        }
        userRepository.updateFilter(filter)
    }

    fun saveTopSizesMen(sizes: List<SizeLine>) {
        val filter = userRepository.getFilter().apply {
            chosenTopSizesMen =
                if (sizes.size >= chosenTopSizesMen.size) sizes else sizes + if (chosenTopSizesMen.isNotEmpty()) listOf(
                    chosenTopSizesMen.last()
                ) else listOf(SizeLine(0, "", "", "", "", "", false, idCategory = -1))

        }
        userRepository.updateFilter(filter)
    }

    fun saveBottomSizesMen(sizes: List<SizeLine>) {
        val filter = userRepository.getFilter().apply {
            chosenBottomSizesMen =
                if (sizes.size >= chosenBottomSizesMen.size) sizes else sizes + if (chosenBottomSizesMen.isNotEmpty()) listOf(
                    chosenBottomSizesMen.last()
                ) else listOf(SizeLine(0, "", "", "", "", "", false, -1))
        }
        userRepository.updateFilter(filter)
    }

    fun saveShoosSizesMen(sizes: List<SizeLine>) {
        val filter = userRepository.getFilter().apply {
            chosenShoosSizesMen =
                if (sizes.size >= chosenShoosSizesMen.size) sizes else sizes + if (chosenShoosSizesMen.isNotEmpty()) listOf(
                    chosenShoosSizesMen.last()
                ) else listOf(SizeLine(0, "", "", "", "", "", false, idCategory = -1))
        }
        userRepository.updateFilter(filter)
    }

    fun saveTopSizesKids(sizes: List<SizeLine>) {
        val filter = userRepository.getFilter().apply { chosenTopSizesKids = sizes }
        userRepository.updateFilter(filter)
    }

    fun saveBottomSizesKids(sizes: List<SizeLine>) {
        val filter = userRepository.getFilter().apply { chosenBottomSizesKids = sizes }
        userRepository.updateFilter(filter)
    }

    fun saveShoosSizesKids(sizes: List<SizeLine>) {
        val filter = userRepository.getFilter().apply { chosenShoosSizesKids = sizes }
        userRepository.updateFilter(filter)
    }

    fun collectMaterialSearchFlow(flow: Flow<String>, materials: List<Property>) {
        searchJob3 = flow
            .debounce(3000)
            .mapLatest { query ->
                withContext(Dispatchers.IO) {
                    materials.filter { it.name.contains(query, true) }
                }

            }
            .onEach { result ->
                viewState.loaded(result)

            }.launchIn(presenterScope)
    }

    fun getMaterials() {
        getProperties(13)
    }

    fun saveMaterials(materials: List<MaterialLine>) {
        val filter = userRepository.getFilter().apply { chosenMaterials = materials }
        userRepository.updateFilter(filter)
    }

    fun saveStatuses(statuses: List<Boolean>) {
        val filter = userRepository.getFilter()
        filter.chosenProductStatus = statuses
        userRepository.updateFilter(filter)
    }

    fun getCurrentUserAddress() {
        val currentAddress = userRepository.getCurrentUserAddress()
        if (currentAddress != null)
            viewState.loaded(currentAddress)
    }

    fun getAllCities() {
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.getCities()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun collectRegionSearchFlow(flow: Flow<String>, regions: List<Region>) {
        searchJob = flow
            .debounce(800)
            .mapLatest { query ->
                withContext(Dispatchers.IO) {
                    regions.filter { it.name.contains(query, true) }
                }

            }
            .onEach { result ->
                viewState.loaded(result)

            }.launchIn(presenterScope)
    }

    fun getShoosSizesWomen() {
        getProperties(6)
    }

    fun getShoosSizesMen() {
        getProperties(3)
    }

    fun getTopSizesWomen() {
        getProperties(4)
    }

    fun getTopSizesMen() {
        getProperties(1)
    }

    fun getBottomSizesWomen() {
        getProperties(5)
    }

    fun getBottomSizesMen() {
        getProperties(2)
    }

    fun loadStyles() {
        val styles = userRepository.getFilter().chosenStyles
        if (styles.isNotEmpty()) {
            viewState.loaded(styles)
        }
    }

    fun saveStyles(styles: List<Boolean>) {
        val filter = userRepository.getFilter()
        val propertyStyles = styles.mapIndexedNotNull { index, isChecked ->

            when (index) {
                0 -> Property(143, "Вечерний", null, null, isChecked)
                1 -> Property(108, "Деловой", null, null, isChecked)
                2 -> Property(109, "Повседневный", null, null, isChecked)
                3 -> Property(110, "Спортивный", null, null, isChecked)
                else -> null
            }

        }
        filter.chosenStyles = propertyStyles
        userRepository.updateFilter(filter)
    }

}