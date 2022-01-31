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
                viewState.success(Unit)
            else
                viewState.error("Ошибка сохранения")
        }
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
        /* val productCategories = mutableListOf<ProductCategory1>()
      val chosenForWho = repository.loadForWho()
      chosenForWho.forEachIndexed{index, isChecked ->
          if(isChecked)
              when(index){
                  0 -> {
                     list.forEach{
                     //    if(it.name == "Одежда, обувь, аксессуары") {
                              //   Log.d("Debug", "subIsList = ${it.sub is List<*>}")
                               //  Log.d("Debug" ,"sub = ${it.sub}")
                             (it.sub as List<ProductCategory1>).forEach { productCategory ->
                                 when (productCategory.name) {
                                     "Женская одежда" -> productCategories.addAll(productCategory.sub as List<ProductCategory1>)
                                     "Мужская одежда" -> {}
                                     else -> productCategories.add(productCategory)
                                 }
                             }
                        // }
                     }
                  }
                  1 -> {
                      list.forEach{
                        //  if(it.name == "Одежда, обувь, аксессуары")
                              (it.sub as List<ProductCategory1>).forEach{productCategory ->
                                  when(productCategory.name){
                                      "Женская одежда" -> {}
                                      "Мужская одежда" -> productCategories.addAll(productCategory.sub as List<ProductCategory1>)
                                      else -> productCategories.add(productCategory)
                                  }
                              }
                      }
                  }
                  2 -> {
                     // list.forEach{
                       //   if(it.name == "Детская одежда и обувь")
                       //       (it.sub as List<ProductCategory1>) .forEach{productCategory ->
                         //        if(productCategory.name == "Для девочек")
                           //          productCategories.addAll(productCategory.sub as List<ProductCategory1>)
                             // }
                      }
                  }
              }
      }
     return productCategories*/
    }

    private fun filterProductCategoriesKids(productCategoriesKids: List<ProductCategoryKids1>): List<ProductCategory> {
        val productCategories = mutableListOf<ProductCategory>()
        productCategoriesKids.forEach {
            if(it.name == "Детская одежда и обувь")
                productCategories.addAll(it.sub)
        }
        return productCategories
    }

     fun saveColors(colors: List<Color>){
        repository.saveColors(colors)
    }

    fun loadColors(){
        val colors = repository.loadColors()
        if(colors.isNotEmpty())
         viewState.loaded(colors)
    }
}