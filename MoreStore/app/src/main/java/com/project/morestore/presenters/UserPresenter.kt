package com.project.morestore.presenters

import android.content.Context
import android.net.Uri
import android.util.Log
import com.project.morestore.models.ProductBrand
import com.project.morestore.models.Region
import com.project.morestore.models.*
import com.project.morestore.mvpviews.UserMvpView
import com.project.morestore.repositories.AuthRepository
import com.project.morestore.repositories.ProductRepository
import com.project.morestore.repositories.UserRepository
import com.project.morestore.util.isEmailValid
import com.project.morestore.util.isPhoneValid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpPresenter
import moxy.presenterScope
import okhttp3.ResponseBody

class UserPresenter(context: Context) : MvpPresenter<UserMvpView>() {
    private val userRepository = UserRepository(context)
    private val authRepository = AuthRepository(context)
    private val productRepository = ProductRepository(context)


    private var photoUri: Uri? = null
    private var searchJob: Job? = null
    private var searchJob2: Job? = null
    private var searchJob3: Job? = null

    fun changeUserData(
        phone: String? = null,
        email: String? = null,
        name: String? = null,
        surname: String? = null,
        sex: String? = null,
        step: Int? = null,
        code: Int? = null
    ) {
        presenterScope.launch {
            viewState.loading()
            val unmaskedPhone = phone?.filter { it != '(' && it != ')' && it != '-' }
            if (email != null && !email.trim().isEmailValid()) {
                viewState.error("почта указана неверно")
                return@launch
            }
            if (unmaskedPhone != null && !unmaskedPhone.trim().isPhoneValid()) {
                viewState.error("телефон указан неверно")
                return@launch
            }
            if (step == null && phone == null && email == null && name.isNullOrBlank()) {
                viewState.error("укажите имя")
                return@launch
            }
            if (step == null && phone == null && email == null && surname.isNullOrBlank()) {
                viewState.error("укажите фамилию")
                return@launch
            }
            val response = userRepository.changeUserData(
                phone = unmaskedPhone?.trim(),
                email = email?.trim(),
                name = name,
                surname = surname,
                sex = sex,
                step = step,
                code = code
            )

            when (response?.code()) {
                200 -> {
                    if (photoUri != null) {
                        Log.d("Debug", "photoUri = $photoUri")
                        uploadPhoto(photoUri!!)
                        return@launch
                    }

                    if (response.body()?.email?.err != null || response.body()?.phone?.err != null) {
                        Log.d("mylog", response.body()?.email?.err.toString())
                        viewState.error(
                            response.body()?.email?.err ?: response.body()?.phone?.err.toString()
                        )
                        return@launch
                    }

                    viewState.success(Unit)
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


    fun changeUserData2(
        phone: String? = null,
        email: String? = null,
        step: Int? = null,
        code: Int? = null
    ) {
        presenterScope.launch {
            viewState.loading()
            val unmaskedPhone = phone?.filter { it != '(' && it != ')' && it != '-' }
            if (email != null && !email.trim().isEmailValid()) {
                viewState.error("почта указана неверно")
                return@launch
            }
            if (unmaskedPhone != null && !unmaskedPhone.trim().isPhoneValid()) {
                viewState.error("телефон указан неверно")
                return@launch
            }
            val response = userRepository.changeUserData2(
                phone = unmaskedPhone?.trim(),
                email = email?.trim(),
                step = step,
                code = code
            )

            when (response?.code()) {
                200 -> {
                    viewState.success(Unit)
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

    fun getUserInfo() {
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.getCurrentUserInfo()
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

    fun getUserData() {
        presenterScope.launch {
            viewState.loading()
            val response = authRepository.getUserData()
            when (response?.code()) {
                200 -> {
                    authRepository.setupUserId(response.body()!!.id)
                    getUserInfo()
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

    fun getSellerInfo(userId: Int) {
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.getSellerInfo(userId)
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

    fun getUserProducts() {
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.getCurrentUserProducts()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
                400 -> {
                    val bodyString = getStringFromResponse(response.errorBody()!!)
                    viewState.error(bodyString)
                }
                404 -> viewState.loaded(emptyList<Product>())
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("нет интернета")
                else -> viewState.error("ошибка")

            }


        }
    }

    fun getSellerProducts(userId: Int) {
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.getSellerProducts(userId)
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
                400 -> {
                    val bodyString = getStringFromResponse(response.errorBody()!!)
                    viewState.error(bodyString)
                }
                404 -> viewState.loaded(emptyList<Product>())
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("нет интернета")
                else -> viewState.error("ошибка")

            }


        }
    }

    fun getNewCode(phone: String? = null, email: String? = null) {
        presenterScope.launch {
            Log.d("mylog", "getNewCode")
            viewState.loading()
            val response = authRepository.getNewCode(phone?.trim(), email?.trim())
            when (response?.code()) {
                200 -> {
                    viewState.successNewCode()
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

    fun safePhotoUri(uri: Uri) {
        photoUri = uri
    }


    private fun uploadPhoto(uri: Uri) {
        presenterScope.launch {
            val response = userRepository.uploadPhoto(uri)
            when (response?.code()) {
                200 -> {
                    viewState.success(Unit)
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

    fun getAllSizes() {
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.getAllSizes()
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

    fun getAllCategorySegments() {
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.getCategories()
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

    fun getAllBrands() {
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

    fun getMaterials() {
        getProperties(13)
    }

    fun getTopSizesWomen() {
        getProperties(4)
    }

    fun getBottomSizesWomen() {
        getProperties(5)
    }

    fun getShoosSizesWomen() {
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


    fun getCityByCoordinates(coordinates: String) {
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.getCityByCoordinates(coordinates)
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

    fun changeUserCity(city: String? = null, region: Region? = null) {
        presenterScope.launch {
            viewState.loading()
            val filter = userRepository.getFilter()
            region?.let {
                filter.currentLocation = region
                filter.isCurrentLocationFirstLoaded = false
                filter.isCurrentLocationChosen = true
                userRepository.updateFilter(filter)
                viewState.success(Unit)
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
            viewState.success(Unit)
        }


    }

    fun addBrandsToWishList(brandsIds: List<Long>) {
        presenterScope.launch {
            viewState.loading()
            val wishList = BrandWishList(brandsIds)
            val response = userRepository.addBrandsToWishList(wishList)
            when (response?.code()) {
                200 -> viewState.success(response.body()!!)
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

    fun getBrandWishList() {
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.getBrandWishList()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!.map { it.id })
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

    fun collectBrandsSearchFlow(flow: Flow<String>, brands: List<ProductBrand>) {
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

    fun cancelJob() {
        searchJob?.cancel()
        searchJob2?.cancel()
    }


    private suspend fun getStringFromResponse(body: ResponseBody): String {
        return withContext(Dispatchers.IO) {
            val str = body.string()
            Log.d("mylog", str)
            str
        }


    }


    fun checkToken() {
        viewState.loaded(authRepository.isTokenEmpty())
    }

    fun clearToken() {
        authRepository.clearToken()
        viewState.success(Unit)
    }

    fun saveFilter() {
        presenterScope.launch {
            if (userRepository.saveFilter())
                viewState.success("Фильтр сохранен")
            else
                viewState.error("Ошибка сохранения")
        }
    }

    fun clearFilter() {
        userRepository.clearFilter()
        viewState.success("Фильтр очищен")
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
                                       /* it.id == 1 ||
                                                it.id == 2 ||
                                                it.id == 3 ||
                                                it.id == 4 ||
                                                it.id == 5 ||
                                                it.id == 6 ||
                                                it.id == 7 ||
                                                it.id == 9 ||
                                                it.id == 10 ||
                                                it.id == 11 ||
                                                it.id == 12 ||
                                                it.id == 13 ||
                                                it.id == 14 ||
                                                it.id == 15 ||
                                                it.id == 16 ||
                                                it.id == 17 ||
                                                it.id == 18 ||
                                                it.id == 19 ||
                                                it.id == 20*/
                                        it.id == 8 || it.id == 21 || it.id == 22
                                    })
                                }
                                1 -> {
                                    viewState.loaded(response.body()!!.filterNot {
                                               /* it.id == 3 ||
                                                it.id == 4 ||
                                                it.id == 6 ||
                                                it.id == 7 ||
                                                it.id == 10 ||
                                                it.id == 18 ||
                                                it.id == 20 ||
                                                it.id == 21 ||
                                                it.id == 22*/
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
                400 -> viewState.error("Ошибка")
                null -> viewState.error("Нет интернета")
            }
        }
    }

    private fun filterProductCategoriesAdults(
        productCategoriesAdults: List<ProductCategoryAdults>,
        isMan: Boolean
    ): List<ProductCategory> {
        val productCategories = mutableListOf<ProductCategory>()
        productCategoriesAdults.forEach {
            when (it.name) {
                "Женская одежда" -> if (!isMan) productCategories.addAll(it.sub)
                "Мужская одежда" -> if (isMan) productCategories.addAll(it.sub)
                "Аксессуары" -> productCategories.add(ProductCategory(it.id, it.name, null))
                else -> productCategories.addAll(it.sub)
            }

        }
        return productCategories
    }

    private fun filterProductCategoriesKids(productCategoriesKids: List<ProductCategoryKids1>): List<ProductCategory> {
        val productCategories = mutableListOf<ProductCategory>()
        productCategoriesKids.forEach {
            if (it.name == "Детская одежда и обувь")
                productCategories.addAll(it.sub)
        }
        return productCategories
    }

    fun getFilter() {
        viewState.loaded(userRepository.getFilter())
    }

    fun saveColors(colors: List<Property>) {
        val filter = userRepository.getFilter()
        filter.colors = colors
        userRepository.updateFilter(filter)
    }

    fun loadColors() {
        //     val colors = userRepository.loadColors()
        //   if(colors.isNotEmpty())
        //  viewState.loaded(colors)
    }

    fun saveMaterials(materials: List<MaterialLine>) {
        val filter = userRepository.getFilter().apply { chosenMaterials = materials }
        userRepository.updateFilter(filter)
    }

    fun loadMaterials() {
        val materials = userRepository.loadMaterials()
        if (materials.isNotEmpty())
            viewState.loaded(materials)
    }

    fun saveConditions(conditions: List<Boolean>) {
        userRepository.saveConditions(conditions)
    }

    fun loadConditions() {
        val conditions = userRepository.loadConditions()
        if (conditions.isNotEmpty()) {
            viewState.loaded(conditions)
        }
    }

    fun saveForWho(forWho: List<Boolean>) {
        val filter = userRepository.getFilter()
        filter.chosenForWho = forWho
        userRepository.updateFilter(filter)
    }


    fun saveTopSizes(sizes: List<SizeLine>) {
        val filter = userRepository.getFilter().apply {
            chosenTopSizes =
                if (sizes.size >= chosenTopSizes.size) sizes else sizes + if (chosenTopSizes.isNotEmpty()) listOf(
                    chosenTopSizes.last()
                ) else listOf(SizeLine(0, "", "", "", "", "", false, idCategory = -1))
        }
        userRepository.updateFilter(filter)

    }

    fun loadTopSizes() {
        val sizes = userRepository.loadTopSizes()
        if (sizes.isNotEmpty()) {
            viewState.loaded(sizes)
        }
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

    fun saveBottomSizes(sizes: List<SizeLine>) {
        val filter = userRepository.getFilter().apply {
            chosenBottomSizes =
                if (sizes.size >= chosenBottomSizes.size) sizes else sizes + if (chosenBottomSizes.isNotEmpty()) listOf(
                    chosenBottomSizes.last()
                ) else listOf(
                    SizeLine(0, "", "", "", "", "", false, idCategory = -1)
                )
        }
        userRepository.updateFilter(filter)
    }

    fun loadBottomSizes() {
        val sizes = userRepository.loadBottomSizes()
        if (sizes.isNotEmpty()) {
            viewState.loaded(sizes)
        }
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

    fun saveShoosSizes(sizes: List<SizeLine>) {
        val filter = userRepository.getFilter().apply {
            chosenShoosSizes =
                if (sizes.size >= chosenShoosSizes.size) sizes else sizes + if (chosenShoosSizes.isNotEmpty()) listOf(
                    chosenShoosSizes.last()
                ) else listOf(SizeLine(0, "", "", "", "", "", false, idCategory = -1))
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


    fun loadShoosSizes() {
        val sizes = userRepository.loadShoosSizes()
        if (sizes.isNotEmpty()) {
            viewState.loaded(sizes)
        }
    }

    fun saveStatuses(statuses: List<Boolean>) {
        val filter = userRepository.getFilter()
        filter.chosenProductStatus = statuses
        userRepository.updateFilter(filter)
    }

    fun loadProductStatuses() {
        val statuses = userRepository.loadProductStatuses()
        if (statuses.isNotEmpty()) {
            viewState.loaded(statuses)
        }
    }

    fun saveStyles(styles: List<Boolean>) {
        val filter = userRepository.getFilter()
        filter.chosenStyles = styles
        userRepository.updateFilter(filter)
    }

    fun loadStyles() {
        val styles = userRepository.loadStyles()
        if (styles.isNotEmpty()) {
            viewState.loaded(styles)
        }
    }

    fun getCurrentRegion() {
        presenterScope.launch {
            val currentRegion = userRepository.getFilter().currentLocation
            if (currentRegion != null)
                viewState.loaded(currentRegion)
        }
    }

    fun saveCategories(productCategories: List<ProductCategory>) {
        val filter = userRepository.getFilter()
        filter.categories = productCategories
        userRepository.updateFilter(filter)
    }

    fun savePrices(fromPrice: Int?, untilPrice: Int?) {
        val filter = userRepository.getFilter()
        filter.fromPrice = fromPrice
        filter.untilPrice = untilPrice
        userRepository.updateFilter(filter)
    }

    fun getColors() {
       /* presenterScope.launch {
            val response = productRepository.getColors()
            when (response?.code()) {
                200 -> {
                    viewState.loaded(response.body()!!.filter { it.idCategory?.toInt() == 12 })
                }
                400 -> viewState.error("Ошибка")
                null -> viewState.error("Нет интернета")
            }
        }*/
        getProperties(12)
    }

}