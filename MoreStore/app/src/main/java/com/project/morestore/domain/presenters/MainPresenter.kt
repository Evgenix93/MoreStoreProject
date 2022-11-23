package com.project.morestore.domain.presenters

import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import com.project.morestore.R
import com.project.morestore.data.models.*
import com.project.morestore.presentation.mvpviews.MainMvpView
import com.project.morestore.data.repositories.AuthRepository
import com.project.morestore.data.repositories.ChatRepository
import com.project.morestore.data.repositories.ProductRepository
import com.project.morestore.data.repositories.UserRepository
import com.project.morestore.data.repositories.*
import com.project.morestore.util.errorMessage
import com.project.morestore.util.ProductStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainPresenter  @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRepository: AuthRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository,
    private val cartRepository: CartRepository
) : MvpPresenter<MainMvpView>() {

    private var searchJob: Job? = null


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



    fun removeProductFromCart(
        productId: Long,
        userId: Long? = null,
    ) {
        presenterScope.launch {
            viewState.loading()
            val response = cartRepository.removeCartItem(
                productId = productId,
                userId = userId
            )

            when (response?.code()) {
                200 -> {
                    viewState.loaded("")
                }
                else -> viewState.error(errorMessage(response))
            }
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


    fun changeCurrentUserAddress(address: Address){
        userRepository.changeCurrentUserAddress(address)
    }
    fun getCurrentUserAddress(){
        viewState.loaded(userRepository.getCurrentUserAddress() ?: Address("", -1))
    }



    fun getFilter() {
        viewState.loaded(userRepository.getFilter())
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

                            viewState.loadedSuggestions(suggestionsList, suggestionObjects)
                        }

                    }
                    result ?: viewState.error("нет интернета")
                }.launchIn(presenterScope)

    }

    fun cancelSearchJob() {
        searchJob?.cancel()
    }


    fun updateProductCategories(productCategories: List<ProductCategory>) {
        var chosenForWho = userRepository.getFilter().chosenForWho
        if(productCategories.firstOrNull()?.id == 21){
            chosenForWho = listOf(false,false,true)
        }else{
            if(chosenForWho.last())
                chosenForWho = listOf(true,false,false)
        }
        val filter = Filter()
        filter.chosenForWho = chosenForWho
        filter.categories = listOf(ProductCategory(0, "Stub", false)) + productCategories
        userRepository.updateFilter(filter)
        viewState.success()
    }

    fun loadFilter() {
        presenterScope.launch {
            userRepository.loadFilter()
            viewState.loaded(userRepository.getFilter())
        }
    }

    fun updateBrand(brand: ProductBrand) {
        val chosenForWho = userRepository.getFilter().chosenForWho
        val filter = Filter()
        filter.chosenForWho = chosenForWho
        filter.brands = listOf(ProductBrand(0, "Stub", 0, null, null), brand.apply { isChecked = true })
        userRepository.updateFilter(filter)
        viewState.success()
    }


    fun getToken() = authRepository.getToken()

    fun getUserData() {
        presenterScope.launch {
            viewState.loading()
            if(getToken().isEmpty()) {
                getProducts(isFiltered = true)
                getBanners(1)
                return@launch
            }
            val fireBaseToken = getFcmToken()
            fireBaseToken ?: return@launch
            val response = authRepository.getUserData(fireBaseToken)
            when (response?.code()) {
                200 -> {

                    viewState.loaded(response.body()!!)
                    authRepository.setupUserId(response.body()!!.id)
                }
                404 -> {
                    viewState.error(context.getString(R.string.not_logged_in))
                    viewState.loginFailed()
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }


    fun loadOnboardingData() {
        presenterScope.launch {
            val response = userRepository.loadBrandsProperties()
            when (response?.code()) {
                200 -> {
                    viewState.loaded(response.body()!!)
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun getUserId() {
        viewState.loaded(authRepository.getUserId())
    }


    fun tokenCheck() {
        viewState.loaded(authRepository.isTokenEmpty())
    }

    fun getCityByCoordinates(coordinates: String) {
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.getCityByCoordinates(coordinates)
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
            }
        }
    }

    private suspend fun getFcmToken() : String? {
        return suspendCoroutine {
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token ->
                    it.resume(token)
                }
                .addOnFailureListener { exception ->
                    viewState.error(exception.message.orEmpty())
                    it.resume(null)
                }
        }
    }

    fun showUnreadMessages(){
        presenterScope.launch {
            val userId = authRepository.getUserId()
            val response = chatRepository.getDialogs()
            if(response?.code() != 200)
                return@launch
            val dialogs = response.body()
            dialogs ?: return@launch
            val unreadDialog = dialogs.find { it.dialog.lastMessage?.is_read == 0 && it.dialog.lastMessage.idSender != userId }
            viewState.loaded(unreadDialog != null)
        }
    }



    fun getBanners(sex: Int){
        presenterScope.launch {
            val response = productRepository.getBanners()
            response?.let {
                if(it.body()!!.isEmpty()) return@launch
                when(sex){
                    1 -> viewState.loaded(it.body()!!.filter { banner -> banner.sex == "1"  })
                    2 -> viewState.loaded(it.body()!!.filter {banner -> banner.sex == "2" || banner.sex == "1"   })
                    3 -> viewState.loaded(it.body()!!.filter { banner -> banner.sex == "3" || banner.sex == "1" })
                }
            }
        }
    }


}