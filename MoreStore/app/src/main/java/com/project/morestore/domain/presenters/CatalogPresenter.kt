package com.project.morestore.domain.presenters

import com.project.morestore.data.models.*
import com.project.morestore.data.repositories.AuthRepository
import com.project.morestore.data.repositories.ProductRepository
import com.project.morestore.data.repositories.UserRepository
import com.project.morestore.presentation.mvpviews.CatalogMvpView
import com.project.morestore.presentation.mvpviews.MainFragmentMvpView
import com.project.morestore.util.ProductStatus
import com.project.morestore.util.errorMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope

class CatalogPresenter(
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository,
    private val authRepository: AuthRepository
    ): MvpPresenter<CatalogMvpView>() {

    private var searchJob: Job? = null

    fun getCurrentUserAddress(){
        viewState.loaded(userRepository.getCurrentUserAddress() ?: Address("", -1))
    }

    fun getProducts(
        queryStr: String? = null,
        productId: Long? = null,
        isFiltered: Boolean,
        productCategories: List<ProductCategory>? = null,
        forWho: List<Boolean>? = null,
        isGuest: Boolean = false,
        status: Int? = 1,
        useDefaultFilter: Boolean = false
    ) {
        presenterScope.launch {
            viewState.loading()
            val response = if (productCategories != null || forWho != null) {
                val filter = Filter(
                    chosenForWho = forWho.orEmpty(),
                    categories = productCategories.orEmpty(),
                    chosenProductStatus = listOf()
                )
                productRepository.getProducts(
                    query = queryStr,
                    filter = filter,
                    productId = productId,
                    limit = 500,
                    isGuest = isGuest,
                    status = status
                )
            } else {
                productRepository.getProducts(
                    query = queryStr,
                    filter = if (isFiltered){
                        if(!useDefaultFilter)
                            userRepository.getFilter()
                        else {
                            val currentForWho = userRepository.getFilter().chosenForWho
                            val filter = Filter()
                            filter.chosenForWho = currentForWho
                            filter
                        }
                    } else null,
                    productId = productId,
                    limit = 500,
                    isGuest = isGuest,
                    status = status
                )
            }
            when (response?.code()) {
                200 -> {
                    val filter = userRepository.getFilter()
                    val currentUserId = authRepository.getUserId()
                    response.body()?.forEach {
                        val productStatus = when (it.statusUser?.order?.status) {
                            0 -> if (it.statusUser.order.idUser == currentUserId && it.statusUser.buy?.status != 2) ProductStatus.APPROVED.value
                            else if (it.idUser == currentUserId && it.statusUser.buy?.status != 2) ProductStatus.APPROVED.value
                            else if (it.statusUser.buy?.status != 2) ProductStatus.BOOKED.value else it.status
                            1 -> ProductStatus.SOLD.value
                            null -> it.status
                            else -> it.status
                        }
                        it.status = productStatus
                    }
                    if(isFiltered) {
                        val products = response.body()!!.toMutableList()
                        filter.chosenProductStatus.forEachIndexed { index, chosen ->
                            if (chosen.not())
                                when (index) {
                                    0 -> products.removeAll(
                                        response.body()!!.filter { it.status == 1 })
                                    1 -> products.removeAll(
                                        response.body()!!
                                            .filter { it.status == 6 || it.status == 7 })
                                    2 -> products.removeAll(
                                        response.body()!!.filter { it.status == 8 })
                                }
                        }
                        if (productId == null) viewState.loaded(products.toList()) else viewState.loaded(
                            products.toList().first()
                        )
                    }else{
                        if (productId == null) viewState.loaded(response.body()!!) else viewState.loaded(
                            response.body()!!.first()
                        )
                    }
                }
                404 -> {
                    viewState.loaded(emptyList<Product>())


                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun changeUserCity(city: String? = null, region: Region? = null) {
        presenterScope.launch {
            viewState.loading()
            val filter = userRepository.getFilter()
            city ?: run {
                filter.currentLocation = null
                filter.isCurrentLocationChosen = true
                userRepository.updateFilter(filter)
                viewState.loaded(Unit)
                return@launch
            }
            region?.let {
                filter.currentLocation = region
                filter.isCurrentLocationFirstLoaded = false
                filter.isCurrentLocationChosen = true
                userRepository.updateFilter(filter)
                viewState.loaded(Unit)
                return@launch
            }
            val cities = productRepository.getCities()?.body()
            cities ?: run {
                viewState.error("Ошибка")
                return@launch
            }
            val foundCity = cities.find { it.name == city }
            foundCity ?: run {
                viewState.error("Ошибка")
                return@launch
            }
            filter.currentLocation = foundCity
            filter.isCurrentLocationFirstLoaded = false
            filter.isCurrentLocationChosen = true
            userRepository.updateFilter(filter)
            viewState.loaded(Unit)
        }
    }

    fun getSuggestionProducts(suggestionModels: SuggestionModels) {
        presenterScope.launch {
            viewState.loading()
            val filter = userRepository.getFilter()
            val category = suggestionModels.list.find { it.category != null }
            val brand = suggestionModels.list.find { it.brand != null }
            val product = suggestionModels.list.find { it.product == true }

            category?.let {
                val forWho: Int =
                    if (category.category == 6.toLong()
                        || category.category == 7.toLong()
                        || category.category == 4.toLong()
                        || category.category == 10.toLong()
                        || category.category == 18.toLong()
                    )
                        0
                    else if (category.category == 8.toLong())
                        1
                    else if (category.category == 21.toLong()
                        || category.category == 22.toLong()
                    )
                        2
                    else filter.chosenForWho.indexOf(true)
                filter.chosenForWho = listOf(forWho == 0, forWho == 1, forWho == 2)
                filter.categories.forEach { it.isChecked = false }
                val filterCategory =
                    filter.categories.find { it.id == category.category?.toInt() }
                        ?.apply { isChecked = true }

                filterCategory ?: run {
                    val response = productRepository.getProductCategories()
                    if (response?.code() == 200) {
                        if (filter.categories.isNotEmpty())
                            filter.categories = filter.categories + listOf(
                                response.body()?.find { it.id == category.category?.toInt() }!!
                                    .apply { isChecked = true })
                        else
                            filter.categories = response.body()!!.apply {
                                find { it.id == category.category?.toInt() }?.apply {
                                    isChecked = true
                                }
                            }

                    }
                }
            }
            brand?.let {
                filter.brands.forEach { it.isChecked = false }
                val filterBrand =
                    filter.brands.find { it.id == brand.brand }
                        ?.apply { isChecked = true }

                filterBrand ?: run {
                    val response = productRepository.getBrands()
                    if (response?.code() == 200) {
                        if (filter.brands.isNotEmpty())
                            filter.brands = filter.brands + listOf(
                                response.body()?.find { it.id == brand.brand }!!
                                    .apply { isChecked = true })
                        else
                            filter.brands = response.body()!!
                                .apply { find { it.id == brand.brand }?.apply { isChecked = true } }
                    }
                }
            }

            val queryStr = product?.text ?: ""
            userRepository.updateFilter(filter)
            getProducts(queryStr, isFiltered = true)
        }
    }

    fun collectSearchFlow(flow: Flow<String>) {
        searchJob = flow
            .debounce(3000)
            .mapLatest { query ->
                val queryStr = query.replace(" и ", " ", true)
                productRepository.getSearchSuggestions(queryStr)
            }
            .onEach { result ->
                result?.let {
                    if (result.code() == 200) {
                        val categoryList =
                            result.body()?.filter { it.category != null }.orEmpty().toSet()
                        val brandList = result.body()?.filter { it.brand != null }.orEmpty().toSet()
                        val productList =
                            result.body()?.filter { it.product == true }.orEmpty().toSet()

                        val suggestionsList = mutableListOf<String>()
                        val suggestionObjects = mutableListOf<SuggestionModels>()

                        if (categoryList.isNotEmpty())
                            for (category in categoryList) {
                                if (brandList.isNotEmpty())
                                    for (brand in brandList) {
                                        if (productList.isNotEmpty())
                                            for (product in productList) {
                                                suggestionsList.add("${category.text} ${brand.text} ${product.text}")
                                                suggestionObjects.add(
                                                    SuggestionModels(
                                                        listOf(
                                                            category,
                                                            brand,
                                                            product
                                                        )
                                                    )
                                                )
                                            } else {
                                            suggestionsList.add("${category.text} ${brand.text}")
                                            suggestionObjects.add(
                                                SuggestionModels(
                                                    listOf(
                                                        category,
                                                        brand
                                                    )
                                                )
                                            )
                                        }
                                    } else if (productList.isNotEmpty())
                                    for (product in productList) {
                                        suggestionsList.add("${category.text} ${product.text}")
                                        suggestionObjects.add(
                                            SuggestionModels(
                                                listOf(
                                                    category,
                                                    product
                                                )
                                            )
                                        )
                                    } else {
                                    suggestionsList.add("${category.text}")
                                    suggestionObjects.add(SuggestionModels(listOf(category)))
                                }
                            } else if (brandList.isNotEmpty())
                            for (brand in brandList) {
                                if (productList.isNotEmpty())
                                    for (product in productList) {
                                        suggestionsList.add("${brand.text} ${product.text}")
                                        suggestionObjects.add(
                                            SuggestionModels(
                                                listOf(
                                                    brand,
                                                    product
                                                )
                                            )
                                        )
                                    } else {
                                    suggestionsList.add("${brand.text}")
                                    suggestionObjects.add(SuggestionModels(listOf(brand)))
                                }
                            } else for (product in productList) {
                            suggestionsList.add("${product.text}")
                            suggestionObjects.add(SuggestionModels(listOf(product)))
                        }

                        (viewState as MainFragmentMvpView).loadedSuggestions(suggestionsList, suggestionObjects)
                    }

                }
                result ?: viewState.error("нет интернета")
            }.launchIn(presenterScope)
    }

    fun cancelSearchJob() {
        searchJob?.cancel()
    }

    fun getFilter() {
        viewState.loaded(userRepository.getFilter())
    }
}