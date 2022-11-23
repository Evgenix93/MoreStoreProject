package com.project.morestore.domain.presenters

import android.content.Context
import android.net.Uri
import android.util.Log
import com.project.morestore.R
import com.project.morestore.data.models.ProductBrand
import com.project.morestore.data.models.Region
import com.project.morestore.data.models.*
import com.project.morestore.presentation.mvpviews.UserMvpView
import com.project.morestore.data.repositories.*
import com.project.morestore.util.errorMessage
import com.project.morestore.util.isEmailValid
import com.project.morestore.util.isPhoneValid
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpPresenter
import moxy.presenterScope
import okhttp3.ResponseBody
import javax.inject.Inject

class UserPresenter @Inject constructor(
    @ActivityContext private val context: Context,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val productRepository: ProductRepository,
    private val reviewsRepository: ReviewRepository
) : MvpPresenter<UserMvpView>() {

    private lateinit var user: User
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
                viewState.error(context.getString(R.string.email_format_incorrect))
                return@launch
            }
            if (unmaskedPhone != null && !unmaskedPhone.trim().isPhoneValid()) {
                viewState.error(context.getString(R.string.phone_format_incorrect))
                return@launch
            }
            if (step == null && phone == null && email == null && name.isNullOrBlank()) {
                viewState.error(context.getString(R.string.write_your_name))
                return@launch
            }
            if (step == null && phone == null && email == null && surname.isNullOrBlank()) {
                viewState.error(context.getString(R.string.write_your_second_name))
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
                else -> viewState.error(errorMessage(response))
            }
        }
    }




    private fun getUserInfo() {
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.getCurrentUserInfo()
            when (response?.code()) {
                200 -> {
                    user = response.body()!!
                    viewState.loaded(response.body()!!)
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun getUser() = user

    fun setUser(user: User) {
        this.user = user
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
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun getSellerInfo(userId: Long) {
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.getSellerInfo(userId)
            when (response?.code()) {
                200 -> {
                    user = response.body()!!
                    viewState.loaded(response.body()!!)
                }

                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun getUserProducts(isActive: Boolean, isOnModeration: Boolean) {
        presenterScope.launch {
            viewState.loading()
            val response =  productRepository.getCurrentUserProducts()
            when (response?.code()) {
                200 -> {
                    response.body()?.forEach {
                        val status = when {
                            it.statusUser?.order?.status == 1 -> 8
                            it.statusUser?.order?.status == 0 -> 6
                            else -> it.status
                        }
                        it.status = status
                    }
                    if (isActive) {
                        Log.d("mydebug", response.body().toString())
                        if(isOnModeration.not())
                        viewState.loaded(
                            response.body()!!
                                .filter { it.status == 1 || it.status == 6 })
                        else viewState.loaded(response.body()!!.filter { it.status == 0 })
                    } else {
                        viewState.loaded(
                            response.body()!!.filter { it.status == 8 }
                        )
                    }
                    val reviews = reviewsRepository.getReviews(authRepository.getUserId())
                    viewState.loaded(listOf(response.body()!!
                        .filter { it.status == 1 || it.status == 6 }.size,
                        response.body()!!.filter { it.status == 0 }.size,
                        response.body()!!.filter { it.status == 8 }.size,
                    reviews.size))
                }
                404 -> viewState.loaded(emptyList<Product>())
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun getSellerProducts(userId: Long) {
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.getSellerProducts(userId)
            when (response?.code()) {
                200 -> {
                    val currentUserId = authRepository.getUserId()
                    response.body()?.forEach {
                        val status = when (it.statusUser?.order?.status) {
                            0 -> if (it.statusUser.order.idUser == currentUserId && it.statusUser.buy?.status != 2) 6
                            else if (it.idUser == currentUserId && it.statusUser.buy?.status != 2) 6
                            else if (it.statusUser.buy?.status != 2) 7 else 1
                            1 -> 8
                            else -> 1
                        }
                        it.status = status
                    }
                    viewState.loaded(response.body()!!)
                }
                404 -> viewState.loaded(emptyList<Product>())
                else -> viewState.error(errorMessage(response))
            }
        }
    }






    private fun uploadPhoto(uri: Uri) {
        presenterScope.launch {
            val response = userRepository.uploadPhoto(uri)
            when (response?.code()) {
                200 -> {
                    viewState.success(Unit)
                }
                else -> viewState.error(errorMessage(response))
            }
        }
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

    fun getAllCategorySegments() {
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.getCategories()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun getAllBrands() {
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.getBrands()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
                else -> viewState.error(errorMessage(response))
            }
        }
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
        changeUserData()
        getProperties(2)
    }

    fun getShoosSizesMen() {
        getProperties(3)
    }


    fun getCityByCoordinates(coordinates: String) {
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.getCityByCoordinates(coordinates)
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
                else -> viewState.error(errorMessage(response))
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
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun getBrandWishList() {
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.getBrandWishList()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!.map { it.id })
                404 -> {}
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun getSellersWishList() {
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.getSellersWishList()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
                404 -> {
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }


    fun addDeleteSellersInWishList(sellersIds: List<Long>) {
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.addDeleteSellerInWishList(BrandWishList(sellersIds))
            when (response?.code()) {
                200 -> viewState.success(response.body()!!)
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
        presenterScope.launch {
            authRepository.clearToken()
            viewState.success(Unit)
        }
    }

    fun saveFilter() {
        presenterScope.launch {
            if (userRepository.saveFilter())
                viewState.success(context.getString(R.string.filter_saved))
            else
                viewState.error(context.getString(R.string.error_saving))
        }
    }


    fun saveFavoriteSearch() {
        presenterScope.launch {
            viewState.loading()
            val response =
                userRepository.saveFavoriteSearch(FavoriteSearchValue(value = userRepository.getFilter()))
            when (response?.code()) {
                200 -> {
                }
                404 -> {
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }


    fun clearFilter() {
        presenterScope.launch {
            userRepository.clearFilter()
            viewState.success(context.getString(R.string.filter_cleared))
        }
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



    fun saveTopSizes(sizes: List<SizeLine>) {
        val filter = userRepository.getFilter().apply {
            chosenTopSizesWomen =
                if (sizes.size >= chosenTopSizesWomen.size) sizes else sizes + if (chosenTopSizesWomen.isNotEmpty()) listOf(
                    chosenTopSizesWomen.last()
                ) else listOf(SizeLine(0, "", "", "", "", "", false, idCategory = -1))
        }
        userRepository.updateFilter(filter)
    }

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
            chosenBottomSizesWomen =
                if (sizes.size >= chosenBottomSizesWomen.size) sizes else sizes + if (chosenBottomSizesWomen.isNotEmpty()) listOf(
                    chosenBottomSizesWomen.last()
                ) else listOf(
                    SizeLine(0, "", "", "", "", "", false, idCategory = -1)
                )

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

    fun saveShoosSizes(sizes: List<SizeLine>) {
        val filter = userRepository.getFilter().apply {
            chosenShoosSizesWomen =
                if (sizes.size >= chosenShoosSizesWomen.size) sizes else sizes + if (chosenShoosSizesWomen.isNotEmpty()) listOf(
                    chosenShoosSizesWomen.last()
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


    fun saveStatuses(statuses: List<Boolean>) {
        val filter = userRepository.getFilter()
        filter.chosenProductStatus = statuses
        userRepository.updateFilter(filter)
    }



    fun loadStyles() {
        val styles = userRepository.getFilter().chosenStyles
        if (styles.isNotEmpty()) {
            viewState.loaded(styles)
        }
    }

    fun getCurrentUserAddress() {
        val currentAddress = userRepository.getCurrentUserAddress()
        if (currentAddress != null)
            viewState.loaded(currentAddress)
    }

    fun changeCurrentUserAddress(address: Address) {
        userRepository.changeCurrentUserAddress(address)
        viewState.success(Unit)
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



    fun loadOnboardingData() {
        presenterScope.launch {
            val response = userRepository.loadBrandsProperties()
            when (response?.code()) {
                200 -> {
                    Log.d("MyDebug", "load brandsPropert success")
                    viewState.loaded(response.body()!!)
                }
                else -> {
                    viewState.error(errorMessage(response))
                }
            }
        }
    }

    fun changeSortingType(sort: String) {
        val filter = userRepository.getFilter().apply { sortingType = sort }
        userRepository.updateFilter(filter)
    }

    fun getCurrentUserReviews(){
        presenterScope.launch {
            viewState.loading()
            val reviews = reviewsRepository.getReviews(authRepository.getUserId()).map { ReviewItem(it) }
            viewState.loaded(reviews)
        }
    }






}