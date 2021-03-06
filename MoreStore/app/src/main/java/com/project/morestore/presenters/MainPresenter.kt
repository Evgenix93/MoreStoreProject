package com.project.morestore.presenters

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.firebase.messaging.FirebaseMessaging
import com.project.morestore.models.*
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.repositories.AuthRepository
import com.project.morestore.repositories.ChatRepository
import com.project.morestore.repositories.ProductRepository
import com.project.morestore.repositories.UserRepository
import com.project.morestore.singletones.Token
import kotlinx.coroutines.*
import com.project.morestore.repositories.*
import com.project.morestore.singletones.Network
import com.project.morestore.util.SortingType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import moxy.MvpPresenter
import moxy.presenterScope
import okhttp3.ResponseBody
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainPresenter(context: Context) : MvpPresenter<MainMvpView>() {
    private val authRepository = AuthRepository(context)
    private val productRepository = ProductRepository(context)
    private val ordersRepository = OrdersRepository(context)
    private val userRepository = UserRepository(context)
    private val chatRepository = ChatRepository(context)
    private val addressesRepository = AddressesRepository.apply { init(Network.addresses) }
    private var searchJob: Job? = null
    private var searchJob2: Job? = null
    private val ITEMS_PER_PAGE = 50


    fun addProductToWishList(id: Long) {
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.addProductToWishList(BrandWishList(listOf(id)))
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
                400 -> viewState.error(getStringFromResponse(response.errorBody()!!))
                404 -> {
                    viewState.loaded(emptyList<Product>())
                    viewState.error(getStringFromResponse(response.errorBody()!!))

                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("?????? ??????????????????")

            }


        }
    }

    fun getProductWishList() {
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.getProductWishList()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
                400 -> viewState.error(getStringFromResponse(response.errorBody()!!))
                404 -> {
                    viewState.loaded(emptyList<Product>())
                    viewState.error(getStringFromResponse(response.errorBody()!!))

                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("?????? ??????????????????")

            }
        }
    }

    fun checkToken() {
        presenterScope.launch {
            val token = authRepository.loadToken()
            val tokenSaveTime = authRepository.loadTokenSaveTime()
            val tokenExpiresTime = authRepository.loadTokenExpires()

            if (token == null || tokenSaveTime == null || tokenExpiresTime == null) {
                Log.d("error", token.orEmpty())
                Log.d("error", tokenSaveTime.toString())
                Log.d("error", tokenExpiresTime.toString())


                viewState.loaded(false)
                return@launch
            }

            val diffTime = System.currentTimeMillis() - tokenSaveTime
            val isExpired = (diffTime / 1000) / 60 > tokenExpiresTime
            if (isExpired) {
                viewState.loaded(false)
            } else {
                authRepository.setupToken(token)
                viewState.loaded(true)
            }
        }
    }


    fun getProducts(
            queryStr: String? = null,
            productId: Long? = null,
            isFiltered: Boolean,
            productCategories: List<ProductCategory>? = null,
            forWho: List<Boolean>? = null,
            isGuest: Boolean = false,
            status: Int? = 1
    ) {
        Log.d("mylog", "getProducts")
        presenterScope.launch {
            viewState.loading()
            val response =/*val flow =*/  if (productCategories != null || forWho != null) {
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
            /*Pager(
                config = PagingConfig(pageSize = ITEMS_PER_PAGE, enablePlaceholders = false),
                pagingSourceFactory = {
                    productRepository.productPagingSource(
                        query = queryStr,
                        filter = filter,
                        productId = productId,
                        isGuest = isGuest

                    )
                }
            ).flow.cachedIn(presenterScope)*/
            } else {
                productRepository.getProducts(
                    query = queryStr,
                    filter = if (isFiltered) userRepository.getFilter() else null,
                    productId = productId,
                    limit = 500,
                    isGuest = isGuest,
                    status = status
                )
            }
            /*Pager(
                config = PagingConfig(pageSize = ITEMS_PER_PAGE, enablePlaceholders = false),
                pagingSourceFactory = {
                    productRepository.productPagingSource(
                        query = queryStr,
                        filter = if (isFiltered) userRepository.getFilter() else null,
                        productId = productId,
                        isGuest = isGuest

                    )
                }
            ).flow.cachedIn(presenterScope)*/
            //flow.collectLatest {
              //  viewState.loaded(it)

                when (response?.code()) {
                    200 -> {
                        val filter = userRepository.getFilter()
                        val currentUserId = authRepository.getUserId()
                        response.body()?.forEach {
                            val status = when (it.statusUser?.order?.status) {
                                0 -> if (it.statusUser.order.idUser == currentUserId && it.statusUser.buy?.status != 2) 6
                                else if (it.idUser == currentUserId && it.statusUser.buy?.status != 2) 6
                                else if (it.statusUser.buy?.status != 2) 7 else 1
                                1 -> 8
                                else -> if ((it.statusUser?.buy?.status == 0 || it.statusUser?.buy?.status == 1) &&
                                    (it.idUser == currentUserId || it.statusUser.buy.idUser == currentUserId)
                                ) 6
                                else if (it.statusUser?.buy?.status == 0 || it.statusUser?.buy?.status == 1) 7
                                else 1
                            }
                            it.status = status
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
                    400 -> viewState.error(getStringFromResponse(response.errorBody()!!))
                    404 -> {
                        viewState.loaded(emptyList<Product>())
                        viewState.error(getStringFromResponse(response.errorBody()!!))

                    }
                    500 -> viewState.error("500 Internal Server Error")
                    null -> viewState.error("?????? ??????????????????")

                //}
            }
        }
    }

    fun addProductToCart(
            productId: Long,
            userId: Long? = null,
    ) {
        Log.d("mylog", "addProductToCart")
        presenterScope.launch {
            viewState.loading()
            val response = ordersRepository.addCartItem(
                    productId = productId,
                    userId = userId
            )

            when (response?.code()) {
                200 -> {
                    viewState.loaded("")
                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("?????? ??????????????????")
                400 -> viewState.error("?????????? ?????????????????? ?? ?????????? ????????????????")

            }
        }
    }

    fun removeProductFromCart(
        productId: Long,
        userId: Long? = null,
    ) {
        Log.d("mylog", "removeProduct")
        presenterScope.launch {
            viewState.loading()
            val response = ordersRepository.removeCartItem(
                productId = productId,
                userId = userId
            )

            when (response?.code()) {
                200 -> {
                    viewState.loaded("")
                }
                400 -> viewState.error(response.message())
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("?????? ??????????????????")

            }
        }
    }

    fun getUserProductsWithStatus(status: Int) {
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.getCurrentUserProductsWithStatus(status)
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)

                400 -> viewState.error(getStringFromResponse(response.errorBody()!!))
                404 -> {
                    viewState.loaded(emptyList<Product>())
                    viewState.error(getStringFromResponse(response.errorBody()!!))

                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("?????? ??????????????????")

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

    fun getYouMayLikeProducts(category: Category?) {
        Log.d("MyDebug", "getYouMayLikeProducts")
        Log.d("MyDebug", "category name = ${category?.name}")
        presenterScope.launch {
            viewState.loading()
            val response = if (category == null) productRepository.getYouMayLikeProducts(4, authRepository.getUserId())
            else productRepository.getProducts(limit = 4, filter = Filter(categories = listOf(ProductCategory(category.id, category.name, true)), chosenForWho = listOf()))
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
                400 -> viewState.error(getStringFromResponse(response.errorBody()!!))
                500 -> viewState.error("500 Internal Server Error")
                null -> {
                    viewState.error("?????? ??????????????????")
                }
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
                viewState.error("????????????")
                return@launch
            }
            val foundCity = cities.find { it.name == city }
            foundCity ?: run {
                viewState.error("????????????")
                return@launch
            }
            filter.currentLocation = foundCity
            filter.isCurrentLocationFirstLoaded = false
            filter.isCurrentLocationChosen = true
            userRepository.updateFilter(filter)
            Log.d("mylog", filter.currentLocation?.name.orEmpty())
            viewState.loaded(Unit)
        }


    }

    fun changeCurrentUserAddress(address: Address){
        userRepository.changeCurrentUserAddress(address)
    }
    fun getCurrentUserAddress(){
        viewState.loaded(userRepository.getCurrentUserAddress() ?: Address("", -1))
    }

    fun shareProduct(id: Long) {
        viewState.loaded(productRepository.getShareProductIntent(id))
    }

    fun getFilter() {
        viewState.loaded(userRepository.getFilter())
    }

    fun collectSearchFlow(flow: Flow<String>) {
        searchJob = flow
                .debounce(3000)
                .mapLatest { query ->
                    val queryStr = query.replace(" ?? ", " ", true)
                    productRepository.getSearchSuggestions(queryStr)
                }
                .onEach { result ->
                    result?.let {
                        if (result.code() == 200) {
                            //viewState.loadedSuggestions(result.body()!!)
                            Log.d("mylog", result.body()?.get(0)?.brand.toString())
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
                            Log.d("mylog", suggestionsList.toString())
                        }

                    }
                    result ?: viewState.error("?????? ??????????????????")


                }.launchIn(presenterScope)

    }

    fun cancelSearchJob() {
        searchJob?.cancel()
    }


    private suspend fun getStringFromResponse(body: ResponseBody): String {
        return withContext(Dispatchers.IO) {
            val str = body.string()
            Log.d("mylog", str)
            str
        }


    }

    fun updateProductCategories(productCategories: List<ProductCategory>) {
        val filter = userRepository.getFilter()
        filter.categories = listOf(ProductCategory(0, "Stub", false)) + productCategories
        userRepository.updateFilter(filter)
        viewState.success()
    }

    fun addProductCategory(productCategoryId: Long) {
        val filter = userRepository.getFilter()
        if (filter.categories.all { it.isChecked == false || it.isChecked == null }.not())
            filter.categories.find { it.id == productCategoryId.toInt() }
                    ?.apply { isChecked = true }
        userRepository.updateFilter(filter)

    }

    fun loadFilter() {
        presenterScope.launch {
            userRepository.loadFilter()
            viewState.loaded(userRepository.getFilter())
        }
    }

    fun updateBrand(brand: ProductBrand) {
        val filter = userRepository.getFilter()
                .apply { brands = listOf(ProductBrand(0, "Stub", 0, null, null), brand.apply { isChecked = true }) }
        userRepository.updateFilter(filter)
        viewState.success()
    }

    fun getCategories(forWho: Int) {
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.getProductCategories()
            when (response?.code()) {
                200 -> {
                    when (forWho) {
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

                400 -> viewState.error(getStringFromResponse(response.errorBody()!!))
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("?????? ??????????????????")

            }
        }
    }

    fun getBrands() {
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.getBrands()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
                400 -> {
                    val bodyString = getStringFromResponse(response.errorBody()!!)
                    viewState.error(bodyString)
                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("?????? ??????????????????")
                else -> viewState.error("????????????")

            }

        }
    }

    fun collectBrandSearchFlow(flow: Flow<String>, brands: List<ProductBrand>) {
        searchJob2 = flow
                .debounce(3000)
                .mapLatest { query ->
                    withContext(Dispatchers.IO) {
                        brands.filter { it.name.contains(query, true) }
                    }

                }
                .onEach { result ->
                    viewState.loaded(result)

                }.launchIn(presenterScope)

    }


    private fun getProperties(propertyId: Long) {
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.getProperties()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!.filter { it.idCategory == propertyId })
                400 -> {
                    val bodyString = getStringFromResponse(response.errorBody()!!)
                    viewState.error(bodyString)
                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("?????? ??????????????????")
                else -> viewState.error("????????????")

            }

        }

    }

    fun getSizes(forWho: Int, idCategory: Int) {
        when (forWho) {
            0 -> {
                val isBottomSizes = when (idCategory) {
                    4 -> true
                    7 -> true
                    9 -> true
                    11 -> true
                    else -> false
                }
                if (isBottomSizes) {
                    //  removeProperty(5)
                    getProperties(5)
                } else {
                    //  removeProperty(4)
                    getProperties(4)
                }
            }
            1 -> {
                val isBottomSizes = when (idCategory) {
                    8 -> true
                    9 -> true
                    11 -> true
                    else -> false
                }
                if (isBottomSizes) {
                    //  removeProperty(2)
                    getProperties(2)
                } else {
                    //  removeProperty(1)
                    getProperties(1)
                }
            }
            2 -> {
                val isBottomSizes = when (idCategory) {
                    8 -> true
                    9 -> true
                    11 -> true
                    else -> false
                }
                if (isBottomSizes) {
                    // removeProperty(8)
                    getProperties(8)
                } else {
                    // removeProperty(7)
                    getProperties(7)
                }
            }
        }
    }

    fun getSizesShoos(forWho: Int) {
        when (forWho) {
            0 -> {
                //removeProperty(6)
                getProperties(6)
            }
            1 -> {
                //removeProperty(3)
                getProperties(3)
            }
            2 -> {
                // removeProperty(9)
                getProperties(9)
            }
        }
    }

    fun getColors() {
        getProperties(12)
    }

    fun getMaterials() {
        getProperties(13)
    }

    fun collectMaterialSearchFlow(flow: Flow<String>, materials: List<Property>) {
        searchJob2 = flow
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


    fun getAllCities() {
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.getCities()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
                400 -> {
                    val bodyString = getStringFromResponse(response.errorBody()!!)
                    viewState.error(bodyString)
                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("?????? ??????????????????")
                else -> viewState.error("????????????")

            }
        }
    }

    fun collectRegionSearchFlow(flow: Flow<String>, regions: List<Region>) {
        searchJob = flow
                .debounce(3000)
                .mapLatest { query ->
                    withContext(Dispatchers.IO) {
                        regions.filter { it.name.contains(query, true) }
                    }

                }
                .onEach { result ->
                    viewState.loaded(result)

                }.launchIn(presenterScope)

    }

    fun getToken() = authRepository.getToken()

    fun getUserData() {
        presenterScope.launch {
            viewState.loading()
            Log.d("MyToken", "token = ${getToken()}")
            if(getToken().isEmpty()) {
                getProducts(isFiltered = false)
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
                400 -> {
                    val bodyString = getStringFromResponse(response.errorBody()!!)
                    viewState.error(bodyString)
                }

                404 -> {
                    viewState.error("???????? ???? ????????????????")
                    viewState.loginFailed()
                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("?????? ??????????????????")
                else -> viewState.error("????????????")

            }
        }
    }

    fun createProduct() {
        presenterScope.launch {
            viewState.loading()
            val productAddress = productRepository.loadCreateProductData().address
            val productStatus = productRepository.loadCreateProductData().status
            updateCreateProductData(address = productAddress?.replace("????. ", "")
                ?.replace("?????? ", ""), status = productStatus ?: 1)

            val response = productRepository.createProduct()
            when (response?.code()) {
                200 -> {
                    val photosUploaded = uploadProductPhotos(response.body()?.first()?.id!!)
                    val videosUploaded = uploadProductVideos(response.body()?.first()?.id!!)
                    if(productStatus == 5)
                        changeProductStatus(response.body()!!.first().id, 5)
                    else
                        if (photosUploaded && videosUploaded)
                           viewState.loaded(response.body()!!.first())
                }
                400 -> {
                    val bodyString = getStringFromResponse(response.errorBody()!!)
                    viewState.error(bodyString)
                }
                500 -> viewState.error("500 Internal Server Error")
                null -> {
                    viewState.error("?????? ??????????????????")
                }
                else -> viewState.error("????????????")
            }
        }
    }

    fun createDraftProduct() {
        val currentProductData = productRepository.loadCreateProductData()
       // updateCreateProductData(status = 5)
        if(currentProductData.address != null) {
            if (currentProductData.id == null)
                createProduct()
            else
                changeProduct()
        }else{
            presenterScope.launch{
                val addresses = addressesRepository.getAllAddresses()
                if(addresses.isNotEmpty()){
                    val address = addresses.first()
                    val cityStr = address.address.city
                    val streetStr = "????. ${address.address.street}"
                    val houseStr = "?????? ${address.address.house}"
                    val strings =
                        arrayOf(cityStr, streetStr, houseStr)
                    val chosenAddressStr = strings.joinToString(", ")
                    Log.d("MyTag", "address = $chosenAddressStr")
                    updateCreateProductData(address = chosenAddressStr, status = 5)
                    Log.d("mydebug", chosenAddressStr)
                    if (currentProductData.id == null)
                        createProduct()
                    else
                        changeProduct()
                }else
                    viewState.error("?????? ???????????????????? ?? ?????????????????? ???????????????????? ?????????????? ????????-???? ???????? ??????????")
            }

        }
    }

    fun updateCreateProductData(
            forWho: Int? = null,
            idCategory: Int? = null,
            idBrand: Long? = null,
            phone: String? = null,
            price: String? = null,
            sale: Float? = null,
            about: String? = null,
            address: String? = null,
            extProperty: Property2? = null,
            extProperties: List<Property2>? = null,
            id: Long? = null,
            newPrice: String? = null,
            name: String? = null,
            status: Int? = null,
            dimensions: ProductDimensions? = null
    ) {
        productRepository.updateCreateProductData(
                forWho,
                idCategory,
                idBrand,
                phone,
                price,
                sale,
                about,
                address,
                extProperty,
                extProperties,
                id,
                newPrice,
                name,
                status,
                dimensions

        )
    }

    fun updateCreateProductDataPhotosVideos(photoVideo: File, position: Int) {
        productRepository.updateCreateProductPhotoVideo(photoVideo, position)
        viewState.success()
    }

    fun updateCreateProductDataPhotosVideos(photoVideoUri: Uri, position: Int) {
        presenterScope.launch {
            val success = productRepository.updateCreateProductPhotoVideo(photoVideoUri, position)
            if (success)
                viewState.success()
            else viewState.error("????????????")

        }
    }

    fun updateCreateProductDataPhotosVideos(bitmap: Bitmap, position: Int) {
        presenterScope.launch {
            val success = productRepository.updateCreateProductPhotoVideo(bitmap, position)
            if (success)
                viewState.success()
            else viewState.error("????????????")
        }
    }

    fun updateCreateProductDataPhotosVideosFromWeb(webUris: MutableMap<Int, String>) {
        presenterScope.launch {
            val fileMap = productRepository.loadCreateProductPhotosVideos()
            webUris.forEach { entry ->
                if (fileMap[entry.key] != null)
                    webUris.remove(entry.key)
            }

            val success = webUris.map { entry ->
                productRepository.updateCreateProductDataPhotoVideoFromWeb(entry.value, entry.key)
            }
            if (success.all { it })
                viewState.success()
            else viewState.error("????????????")
        }
    }

    fun removeProperty(propertyCategory: Long) {
        productRepository.removeProperty(propertyCategory)
    }

    fun loadCreateProductData() {
        viewState.loaded(productRepository.loadCreateProductData())
    }

    fun clearCreateProductData() {
        productRepository.clearCreateProductData()
    }

    fun loadCreateProductPhotosVideos() {
        viewState.loaded(productRepository.loadCreateProductPhotosVideos())
    }


    private suspend fun uploadProductPhotos(productId: Long): Boolean {
        val photosVideosMap = productRepository.loadCreateProductPhotosVideos()
        val photos =
                photosVideosMap.filter { it.value.extension == "jpg" || it.value.extension == "png" || it.value.extension == "webp" }
                        .toSortedMap().map { it.value }

        if (photos.isEmpty()) {
            return true
        }

        val response = productRepository.uploadProductPhotos(photos, productId)
        when (response?.code()) {
            200 -> {
                return true
            }
            400 -> {
                val bodyString = getStringFromResponse(response.errorBody()!!)
                viewState.error(bodyString)
                return false
            }
            500 -> {
                viewState.error("500 Internal Server Error")
                return false
            }
            null -> {
                viewState.error("?????? ??????????????????")
                return false
            }
            else -> {
                viewState.error("????????????")
                return false
            }

        }

    }

    private suspend fun uploadProductVideos(productId: Long): Boolean {
        val photosVideosMap = productRepository.loadCreateProductPhotosVideos()
        val videos = photosVideosMap.filter { it.value.extension == "mp4" }
                .map { it.value }

        if (videos.isEmpty()) {
            return true
        }

        val response = productRepository.uploadProductVideos(videos, productId)
        when (response?.code()) {
            200 -> {
                return true
            }
            400 -> {
                val bodyString = getStringFromResponse(response.errorBody()!!)
                viewState.error(bodyString)
                return false
            }
            500 -> {
                viewState.error("500 Internal Server Error")
                return false
            }
            null -> {
                viewState.error("?????? ??????????????????")
                return false
            }
            else -> {
                viewState.error("????????????")
                return false
            }

        }

    }

    fun getShoosTypes() {
        getProperties(15)
    }

    fun getJeansStyles() {
        getProperties(17)
    }

    fun getTopClotStyles() {
        getProperties(18)
    }

    fun changeProductStatus(productId: Long, status: Int) {
        presenterScope.launch {
            val response = productRepository.changeProductStatus(productId, status)
            when (response?.code()) {
                200 -> viewState.loaded("Success")
                null -> {
                }
            }
        }
    }

    private fun changeProduct() {
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.changeProductData()
            when (response?.code()) {
                200 -> {
                    val photosUploaded = uploadProductPhotos(response.body()?.first()?.id!!)
                    val videosUploaded = uploadProductVideos(response.body()?.first()?.id!!)
                    if (photosUploaded && videosUploaded)
                        viewState.loaded(response.body()!!.first())
                }
                400 -> {
                    val bodyString = getStringFromResponse(response.errorBody()!!)
                    viewState.error(bodyString)
                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("?????? ??????????????????")
                else -> viewState.error("????????????")

            }


        }
    }

    fun changeProductAndPublish() {
        updateCreateProductData(status = 1)
        changeProduct()
    }

    fun deletePhotoBackground(file: File? = null, uri: Uri? = null) {
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.deletePhotoBackground(file, uri)
            when (response?.code()) {
                200 -> {
                    viewState.loaded(response.body()!!.first())

                }
                400 -> {
                    val bodyString = getStringFromResponse(response.errorBody()!!)
                    viewState.error(bodyString)
                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("?????? ??????????????????")
                else -> viewState.error("????????????")

            }
        }
    }

    fun loadOnboardingData() {
        presenterScope.launch {
            val response = userRepository.loadBrandsProperties()
            when (response?.code()) {
                200 -> {
                    //viewState.loaded(Unit)
                    viewState.loaded(response.body()!!)
                }
                null -> viewState.loaded(Unit)
                else -> {
                    viewState.showOnBoarding()
                }
            }
        }
    }

    fun getUserId() {
        viewState.loaded(authRepository.getUserId())
    }

    fun loadCart(userId: Long?, productId: Long, callback: (hasValue: Boolean)->Unit) {
        presenterScope.launch {
            val response = ordersRepository.getCartItems(
                userId = userId
            )

            when (response?.code()) {
                200 -> {
                    val inCart = response.body()?.find { it.product.id == productId }
                    callback(inCart != null)
                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("?????? ??????????????????")
            }
        }
    }

    fun getDialogs() {
        presenterScope.launch {
            val response = chatRepository.getDialogs()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
            }
        }
    }

    fun playVideo(fileUri: Uri? = null, file: File? = null) {
        presenterScope.launch {
            viewState.loading()
            val intent = productRepository.getPlayVideoIntent(fileUri, file)
            if (intent == null) {
                viewState.error("????????????")
                return@launch
            }
            viewState.loaded(intent)
        }
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
                400 -> {}
                500 -> {}
                null -> {}
                else -> {}

            }

        }
    }

    private suspend fun getFcmToken() : String? {
        return suspendCoroutine {
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token ->
                    Log.d("mylog", "token $token")
                    it.resume(token)

                }
                .addOnFailureListener { exception ->
                    Log.d("mylog", exception.message.toString())
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

    private suspend fun getAllOrders(): List<Order>?{
        val response = ordersRepository.getAllOrders()
        return if(response?.code() == 200) response.body()
        else null
    }


   fun viewProduct(idProduct: Long){
       presenterScope.launch{
          val response = productRepository.viewProduct(authRepository.getUserId(), idProduct)
           when(response?.code()){
               200 -> viewState.loaded(response.body()!!)
           }
       }
   }

    fun getBanners(sex: Int){
        Log.d("mytest", "getBanners")
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