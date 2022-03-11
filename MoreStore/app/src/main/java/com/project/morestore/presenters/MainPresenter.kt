package com.project.morestore.presenters

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.project.morestore.models.*
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.repositories.AuthRepository
import com.project.morestore.repositories.ChatRepository
import com.project.morestore.repositories.ProductRepository
import com.project.morestore.repositories.UserRepository
import com.project.morestore.singletones.Token
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpPresenter
import moxy.presenterScope
import okhttp3.ResponseBody
import java.io.File

class MainPresenter(context: Context) : MvpPresenter<MainMvpView>() {
    private val authRepository = AuthRepository(context)
    private val productRepository = ProductRepository(context)
    private val userRepository = UserRepository(context)
    private val chatRepository = ChatRepository()
    private var searchJob: Job? = null
    private var searchJob2: Job? = null
    private lateinit var user: User


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
                null -> viewState.error("нет интернета")

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
                null -> viewState.error("нет интернета")

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


    fun loadOnBoardingViewed() {
        presenterScope.launch {
            viewState.loading()
            if (productRepository.loadOnBoardingViewed()) {
                viewState.loaded(Unit)
            } else {
                if (!authRepository.isTokenEmpty()) {
                    viewState.showOnBoarding()
                } else {
                    viewState.loaded(Unit)
                }
            }
        }
    }

    fun getProducts(
        queryStr: String? = null,
        productId: Long? = null,
        isFiltered: Boolean,
        productCategories: List<ProductCategory>? = null,
        forWho: List<Boolean>? = null
    ) {
        Log.d("mylog", "getProducts")
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
                    productId = productId
                )
            } else
                productRepository.getProducts(
                    query = queryStr,
                    filter = if (isFiltered) userRepository.getFilter() else null,
                    productId = productId
                )

            when (response?.code()) {
                200 -> if (productId == null) viewState.loaded(response.body()!!) else viewState.loaded(
                    response.body()?.first()!!
                )
                400 -> viewState.error(getStringFromResponse(response.errorBody()!!))
                404 -> {
                    viewState.loaded(emptyList<Product>())
                    viewState.error(getStringFromResponse(response.errorBody()!!))

                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("нет интернета")

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
            val response = if(category == null) productRepository.getYouMayLikeProducts(4, authRepository.getUserId())
               else productRepository.getProducts(limit = 4, filter = Filter(categories = listOf(ProductCategory(category.id, category.name, true)), chosenForWho = listOf()))
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
                400 -> viewState.error(getStringFromResponse(response.errorBody()!!))
                500 -> viewState.error("500 Internal Server Error")
                null -> {
                    viewState.error("нет интернета")
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
            Log.d("mylog", filter.currentLocation?.name.orEmpty())
            viewState.loaded(Unit)
        }


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
                val queryStr = query.replace(" и ", " ", true)
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
                result ?: viewState.error("нет интернета")


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

  fun loadFilter(){
      presenterScope.launch{
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

    fun addBrand(brandId: Long) {
        // presenterScope.launch {
        val filter = userRepository.getFilter()
        if (filter.brands.isNotEmpty())
            filter.brands.find { it.id == brandId }?.apply { isChecked = true }
        else {
            filter.brands = listOf(ProductBrand(brandId, "", null, true, null))
            //val response = productRepository.getBrands()
            // if(response?.code() == 200) {
            // filter.brands = response.body()!!
            //filter.brands.find { it.id == brandId }?.apply { isChecked = true }
            // }
        }
        userRepository.updateFilter(filter)

        //}

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
                null -> viewState.error("нет интернета")

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
                null -> viewState.error("нет интернета")
                else -> viewState.error("ошибка")

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
                null -> viewState.error("нет интернета")
                else -> viewState.error("ошибка")

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
                removeProperty(6)
                getProperties(6)
            }
            1 -> {
                removeProperty(3)
                getProperties(3)
            }
            2 -> {
                removeProperty(9)
                getProperties(9)
            }
        }
    }

    fun getTopSizesWomen() {
        getProperties(4)
    }

    fun getTopSizesMen() {
        getProperties(1)
    }

    fun getTopSizesKids() {
        getProperties(7)
    }

    fun getBottomSizesWomen() {

    }

    fun getShoosMen() {
        getProperties(3)
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
                null -> viewState.error("нет интернета")
                else -> viewState.error("ошибка")

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

    fun addNewBrand(brandName: String) {
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.addNewBrand(NewProductBrand(null, brandName))
            when (response?.code()) {
                200 -> {
                    viewState.loaded(response.body()!!.apply { name = brandName })
                }
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

    fun getUserData() {
        presenterScope.launch {
            viewState.loading()
            val response = authRepository.getUserData()
            when (response?.code()) {
                200 -> {

                    viewState.loaded(response.body()!!)
                    authRepository.setupUserId(response.body()!!.id)
                }
                400 -> {
                    val bodyString = getStringFromResponse(response.errorBody()!!)
                    viewState.error(bodyString)
                }

                404 -> {}
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("нет интернета")
                else -> viewState.error("ошибка")

            }
        }
    }

    fun createProduct() {
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.createProduct()
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
                null -> {
                    viewState.error("нет интернета")
                }
                else -> viewState.error("ошибка")
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
        name: String? = null
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
            name

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
            else viewState.error("ошибка")

        }
    }

    fun updateCreateProductDataPhotosVideos(bitmap: Bitmap, position: Int) {
        presenterScope.launch {
            val success = productRepository.updateCreateProductPhotoVideo(bitmap, position)
            if (success)
                viewState.success()
            else viewState.error("ошибка")
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
            photosVideosMap.filter { it.value.extension == "jpg" || it.value.extension == "png" }
                .map { it.value }

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
                viewState.error("нет интернета")
                return false
            }
            else -> {
                viewState.error("ошибка")
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
                viewState.error("нет интернета")
                return false
            }
            else -> {
                viewState.error("ошибка")
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
                null -> {}
            }
        }
    }

    fun changeProduct() {
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.changeProductData()
            when (response?.code()) {
                200 -> {
                    viewState.loaded(response.body()!!.first())
                }
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

    fun deletePhotoBackground(file: File? = null, uri: Uri? = null) {
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.deletePhotoBackground(file, uri)
            when (response?.code()) {
                200 -> {
                    viewState.loaded(response.body()!!.first())
                    //viewState.loaded(productRepository.downLoadProductImage(response.body()!!.first().photo) ?: "")
                }
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

    fun loadOnboardingData(){
        presenterScope.launch {
        val response = userRepository.loadBrandsProperties()
         when(response?.code()){
             200 -> {
                 viewState.loaded(Unit)
             }
             null -> viewState.loaded(Unit)
             else -> {
                 viewState.showOnBoarding()
             }
         }
        }
    }

    fun getUserId(){
        viewState.loaded(authRepository.getUserId())
    }

    fun getDialogs(){
        presenterScope.launch {
            val response = chatRepository.getDialogs()
            when(response?.code()){
                200 -> viewState.loaded(response.body()!!)
            }
        }
    }

    fun playVideo(fileUri: Uri? = null, file: File? = null){
        presenterScope.launch {
            viewState.loading()
            val intent = productRepository.getPlayVideoIntent(fileUri, file)
            if(intent == null){
                viewState.error("ошибка")
                return@launch
            }
            viewState.loaded(intent)
        }
    }


}