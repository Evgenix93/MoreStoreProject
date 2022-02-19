package com.project.morestore.presenters

import android.content.Context
import android.util.Log
import com.project.morestore.models.*
import com.project.morestore.mvpviews.MainMvpView
import com.project.morestore.repositories.AuthRepository
import com.project.morestore.repositories.ProductRepository
import com.project.morestore.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpPresenter
import moxy.presenterScope
import okhttp3.ResponseBody

class MainPresenter(context: Context): MvpPresenter<MainMvpView>() {
    private val authRepository = AuthRepository(context)
    private val productRepository = ProductRepository(context)
    private val userRepository = UserRepository(context)
    private var searchJob: Job? = null
    private var searchJob2: Job? = null


    fun addProductToWishList(id: Long){
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.addProductToWishList(BrandWishList(listOf(id)))
            when(response?.code()){
                200 ->  viewState.loaded(response.body()!!)
                400 -> viewState.error(getStringFromResponse(response.errorBody()!!))
                404 -> { viewState.loaded(emptyList<Product>())
                    viewState.error(getStringFromResponse(response.errorBody()!!))

                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("нет интернета")

            }


        }
    }

    fun getProductWishList(){
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.getProductWishList()
            when(response?.code()){
                200 ->  viewState.loaded(response.body()!!)
                400 -> viewState.error(getStringFromResponse(response.errorBody()!!))
                404 -> { viewState.loaded(emptyList<Product>())
                    viewState.error(getStringFromResponse(response.errorBody()!!))

                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("нет интернета")

            }

        }
    }

    fun checkToken(){
        presenterScope.launch {
            val token = authRepository.loadToken()
            val tokenSaveTime = authRepository.loadTokenSaveTime()
            val tokenExpiresTime = authRepository.loadTokenExpires()

            if(token == null || tokenSaveTime == null || tokenExpiresTime == null){
                Log.d("error", token.orEmpty())
                Log.d("error", tokenSaveTime.toString())
                Log.d("error", tokenExpiresTime.toString())


                viewState.loaded(false)
                return@launch
            }

            val diffTime = System.currentTimeMillis() - tokenSaveTime
            val isExpired = (diffTime/1000)/60 > tokenExpiresTime
            if(isExpired){
                viewState.loaded(false)
            }else {
                authRepository.setupToken(token)
                viewState.loaded(true)
            }
        }
    }


    fun loadOnBoardingViewed() {
        presenterScope.launch {
            viewState.loading()
            if(productRepository.loadOnBoardingViewed()){
                viewState.loaded(Unit)
            }else{
                if(!authRepository.isTokenEmpty()) {
                    viewState.showOnBoarding()
                }else{
                    viewState.loaded(Unit)
                }
            }
        }
    }

    fun getProducts(queryStr: String? = null, productId: Long? = null, isFiltered: Boolean, productCategories: List<ProductCategory>? = null, forWho: List<Boolean>? = null){
        Log.d("mylog", "getProducts")
        presenterScope.launch {
            viewState.loading()
            val response = if(productCategories != null || forWho != null) {
                val filter = Filter(chosenForWho = forWho.orEmpty(),   categories = productCategories.orEmpty(), chosenProductStatus = listOf())
                productRepository.getProducts(
                    query = queryStr,
                    filter = filter,
                    productId = productId
                )
            }else
                 productRepository.getProducts(query = queryStr, filter =  if(isFiltered) userRepository.getFilter() else null, productId = productId)

            when(response?.code()){
                200 -> if(productId == null) viewState.loaded(response.body()!!) else viewState.loaded(response.body()?.first()!!)
                400 -> viewState.error(getStringFromResponse(response.errorBody()!!))
                404 -> { viewState.loaded(emptyList<Product>())
                    viewState.error(getStringFromResponse(response.errorBody()!!))

                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("нет интернета")

            }
        }
    }

    fun getYouMayLikeProducts(){
        presenterScope.launch {
            if(authRepository.getUserId() == 0.toLong()){
                return@launch
            }
            viewState.loading()
            val response = productRepository.getYouMayLikeProducts(4, authRepository.getUserId() )
            when(response?.code()){
                200 -> viewState.loaded(response.body()!!)
                400 -> viewState.error(getStringFromResponse(response.errorBody()!!))
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("нет интернета")
            }
        }
    }

    fun changeUserCity(city: String? = null, region: Region? = null){
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

    fun shareProduct(id: Long){
        viewState.loaded(productRepository.getShareProductIntent(id))
    }

    fun getFilter(){
        viewState.loaded(userRepository.getFilter())
    }

    fun collectSearchFlow(flow: Flow<String>){
        searchJob = flow
            .debounce(3000)
            .mapLatest { query ->
                productRepository.getSearchSuggestions(query)
            }
            .onEach { result ->
                result?.let {
                    if(result.code() == 200)
                    viewState.loadedSuggestions(result.body()!!)
                }
                result ?: viewState.error("нет интернета")


            }.launchIn(presenterScope)

    }

    fun cancelSearchJob(){
        searchJob?.cancel()
    }



    private suspend fun getStringFromResponse(body: ResponseBody): String {
        return withContext(Dispatchers.IO) {
            val str = body.string()
            Log.d("mylog", str)
            str
        }


    }

  fun updateProductCategories(productCategories: List<ProductCategory>){
     val filter = userRepository.getFilter()
     filter.categories = listOf(ProductCategory(0, "Stub", false)) + productCategories
     userRepository.updateFilter(filter)
     viewState.success()
  }

  fun loadFilter(){
      presenterScope.launch{
          userRepository.loadFilter()
          viewState.loaded(userRepository.getFilter())
      }
  }

    fun updateBrand(brand: ProductBrand){
        val filter = userRepository.getFilter().apply { brands = listOf(ProductBrand(0, "Stub", 0, null, null), brand) }
        userRepository.updateFilter(filter)
        viewState.success()
    }

    fun getCategories(forWho: Int){
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.getProductCategories()
            when(response?.code()){
                200 -> {
                    when (forWho) {
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

                400 -> viewState.error(getStringFromResponse(response.errorBody()!!))
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("нет интернета")

            }
        }
    }

    fun getBrands(){
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

    fun collectBrandSearchFlow(flow: Flow<String>, brands: List<ProductBrand>){
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


    fun getProperties(propertyId: Long){
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


    fun getTopSizesWomen(){
        getProperties(4)
    }

    fun getTopSizesMen(){
        getProperties(1)
    }

    fun getTopSizesKids(){
        getProperties(7)
    }

    fun getShoosMen(){
        getProperties(3)
    }





}