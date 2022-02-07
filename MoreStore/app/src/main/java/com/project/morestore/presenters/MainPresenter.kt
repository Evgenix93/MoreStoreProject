package com.project.morestore.presenters

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.project.morestore.models.Filter
import com.project.morestore.models.Product
import com.project.morestore.models.Region
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

    fun getProducts(queryStr: String? = null, productId: Long? = null, isFiltered: Boolean){
        Log.d("mylog", "getProducts")
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.getProducts(query = queryStr, filter =  if(isFiltered) userRepository.getFilter() else null, productId = productId)

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
            if(authRepository.getUserId() == 0){
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



  fun loadFilter(){
      presenterScope.launch{
          productRepository.loadFilter()
      }
  }

}