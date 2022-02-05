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
    private val repository = UserRepository(context)
    private val authRepository = AuthRepository(context)
    private val productRepository = ProductRepository(context)


    private var photoUri: Uri? = null
    private var searchJob: Job? = null
    private var searchJob2: Job? = null

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
            val response = repository.changeUserData(
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
            val response = repository.changeUserData2(
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

    fun getUserInfo(){
        presenterScope.launch {
            viewState.loading()
            val response = repository.getCurrentUserInfo()
            when(response?.code()){
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

    fun getUserProducts(){
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.getCurrentUserProducts()
            when(response?.code()){
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
            val response = repository.uploadPhoto(uri)
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
            when(response?.code()){
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

    fun getAllCities(){
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.getCities()
            when(response?.code()){
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

    fun getAllCategorySegments(){
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.getCategories()
            when(response?.code()){
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

    fun getAllBrands(){
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.getBrands()
            when(response?.code()){
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

    fun getCityByCoordinates(coordinates: String){
        presenterScope.launch {
            viewState.loading()
            val response = repository.getCityByCoordinates(coordinates)
            when(response?.code()){
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

    fun changeUserCity(city: String? = null, region: Region? = null){
        presenterScope.launch {
            viewState.loading()
            val filter = repository.getFilter()
            region?.let {
                filter.currentLocation = region
                filter.isCurrentLocationFirstLoaded = false
                filter.isCurrentLocationChosen = true
                repository.updateFilter(filter)
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
            repository.updateFilter(filter)
            viewState.success(Unit)
        }


    }

    fun addBrandsToWishList(brandsIds: List<Long>){
        presenterScope.launch {
            viewState.loading()
            val wishList = BrandWishList(brandsIds)
            val response = repository.addBrandsToWishList(wishList)
            when (response?.code()){
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

    fun getBrandWishList(){
        presenterScope.launch {
            viewState.loading()
            val response = repository.getBrandWishList()
            when (response?.code()){
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

    fun collectRegionSearchFlow(flow: Flow<String>, regions: List<Region>){
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

    fun collectBrandsSearchFlow(flow: Flow<String>, brands: List<ProductBrand>){
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

    fun cancelJob(){
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
            if (repository.saveFilter())
                viewState.success("Фильтр сохранен")
            else
                viewState.error("Ошибка сохранения")
        }
    }

    fun clearFilter(){
        repository.clearFilter()
        viewState.success("Фильтр очищен")
    }

    fun getProductCategories() {
        presenterScope.launch {
            val chosenForWho = productRepository.loadForWho()
            chosenForWho.forEachIndexed { index, isChecked ->
                if (isChecked)
                    when (index) {
                        2 -> {
                            val response = productRepository.getProductCategoriesKids()
                            when (response?.code()) {
                                null -> viewState.error("Нет интернета")
                                200 -> {viewState.loaded(filterProductCategoriesKids(response.body()!!))}
                                400 -> viewState.error("Ошибка")
                            }
                        }
                        0 -> {
                            val response = productRepository.getProductCategoriesAdults()
                            when (response?.code()) {
                                null -> viewState.error("Нет интернета")
                                200 -> {
                                    viewState.loaded(filterProductCategoriesAdults(response.body()!!, false))
                                }
                                400 -> viewState.error("Ошибка")
                            }
                        }
                        1 -> {
                            val response = productRepository.getProductCategoriesAdults()
                            when (response?.code()) {
                                null -> viewState.error("Нет интернета")
                                200 -> {
                                    viewState.loaded(filterProductCategoriesAdults(response.body()!!, true))
                                }
                                400 -> viewState.error("Ошибка")
                            }
                        }
                    }
            }
        }
    }

    private fun filterProductCategoriesAdults(productCategoriesAdults: List<ProductCategoryAdults>, isMan: Boolean): List<ProductCategory> {
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
            if(it.name == "Детская одежда и обувь")
                productCategories.addAll(it.sub)
        }
        return productCategories
    }

    fun getFilter(){
        viewState.loaded(repository.getFilter())
    }

     fun saveColors(colors: List<Color>){
        repository.saveColors(colors)
    }

    fun loadColors(){
        val colors = repository.loadColors()
        if(colors.isNotEmpty())
         viewState.loaded(colors)
    }

    fun saveMaterials(materials: List<MaterialLine>){
        repository.saveMaterials(materials)
    }

    fun loadMaterials(){
        val materials = repository.loadMaterials()
        if(materials.isNotEmpty())
            viewState.loaded(materials)
    }

    fun saveConditions(conditions: List<Boolean>){
        repository.saveConditions(conditions)
    }

    fun loadConditions(){
        val conditions = repository.loadConditions()
        if(conditions.isNotEmpty()){
            viewState.loaded(conditions)
        }
    }

    fun saveForWho(forWho: List<Boolean>){
        repository.saveForWho(forWho)
    }

    fun loadForWho(){
        val forWho = repository.loadForWho()
        if(forWho.isNotEmpty()){
            viewState.loaded(forWho)
        }
    }

    fun saveTopSizes(sizes: List<SizeLine>){
        repository.saveTopSizes(sizes)
    }

    fun loadTopSizes(){
        val sizes = repository.loadTopSizes()
        if(sizes.isNotEmpty()){
            viewState.loaded(sizes)
        }
    }

    fun saveBottomSizes(sizes: List<SizeLine>){
        repository.saveBottomSizes(sizes)
    }

    fun loadBottomSizes(){
        val sizes = repository.loadBottomSizes()
        if(sizes.isNotEmpty()){
            viewState.loaded(sizes)
        }
    }

    fun saveShoosSizes(sizes: List<SizeLine>){
        repository.saveShoosSizes(sizes)
    }

    fun loadShoosSizes(){
        val sizes = repository.loadShoosSizes()
        if(sizes.isNotEmpty()){
            viewState.loaded(sizes)
        }
    }

    fun saveProductStatuses(statuses: List<Boolean>){
        repository.saveProductStatuses(statuses)
    }

    fun loadProductStatuses(){
        val statuses = repository.loadProductStatuses()
        if(statuses.isNotEmpty()){
            viewState.loaded(statuses)
        }
    }

    fun saveStyles(styles: List<Boolean>){
        repository.saveStyles(styles)
    }

    fun loadStyles(){
        val styles = repository.loadStyles()
        if(styles.isNotEmpty()){
            viewState.loaded(styles)
        }
    }

    fun getUser(){
        presenterScope.launch{
            val currentRegion = repository.getFilter().currentLocation
            if (currentRegion != null)
                viewState.loaded(currentRegion)
        }
    }
}