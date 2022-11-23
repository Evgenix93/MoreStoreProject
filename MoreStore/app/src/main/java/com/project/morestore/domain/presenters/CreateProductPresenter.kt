package com.project.morestore.domain.presenters

import android.content.Context
import com.project.morestore.R
import com.project.morestore.data.models.*
import com.project.morestore.data.repositories.AddressesRepository
import com.project.morestore.data.repositories.AuthRepository
import com.project.morestore.data.repositories.CardRepository
import com.project.morestore.data.repositories.ProductRepository
import com.project.morestore.presentation.mvpviews.CreateProductMvpView
import com.project.morestore.util.errorMessage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class CreateProductPresenter @Inject constructor(@ApplicationContext private val context: Context,
                                                 private val productRepository: ProductRepository,
                                                 private val addressesRepository: AddressesRepository,
                                                 private val authRepository: AuthRepository,
                                                 private val cardRepository: CardRepository): MvpPresenter<CreateProductMvpView>() {

    fun createDraftProduct() {
        val currentProductData = productRepository.loadCreateProductData()
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
                    val streetStr = "ул. ${address.address.street}"
                    val houseStr = "дом ${address.address.house}"
                    val strings =
                        arrayOf(cityStr, streetStr, houseStr)
                    val chosenAddressStr = strings.joinToString(", ")
                    updateCreateProductData(address = chosenAddressStr, status = 5)
                    if (currentProductData.id == null)
                        createProduct()
                    else
                        changeProduct()
                }else
                    viewState.error(context.getString(R.string.for_saving_draft_create_address))
            }

        }
    }

    fun createProduct() {
        presenterScope.launch {
            viewState.loading()
            val productAddress = productRepository.loadCreateProductData().address
            val productStatus = productRepository.loadCreateProductData().status
            updateCreateProductData(address = productAddress?.replace("ул. ", "")
                ?.replace("дом ", ""), status = productStatus ?: 1)

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
                else -> viewState.error(errorMessage(response))
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
                else -> viewState.error(errorMessage(response))
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
        addressCdek: String? = null,
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
            addressCdek,
            extProperty,
            extProperties,
            id,
            newPrice,
            name,
            status,
            dimensions
        )
        //viewState.loaded(Unit)
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
            else -> {
                viewState.error(errorMessage(response))
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
        return when (response?.code()) {
            200 -> {
                true
            }
            else -> {
                viewState.error(errorMessage(response))
                false
            }
        }
    }

    fun changeProductStatus(productId: Long, status: Int) {
        presenterScope.launch {
            val response = productRepository.changeProductStatus(productId, status)
            when (response?.code()) {
                200 -> viewState.loaded("Success")
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun loadCreateProductPhotosVideos() {
        viewState.loaded(productRepository.loadCreateProductPhotosVideos())
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
                viewState.loaded(Unit)
            else viewState.error("ошибка")
        }
    }

    fun getColors() {
        getProperties(12)
    }

    fun getMaterials() {
        getProperties(13)
    }

    private fun getProperties(propertyId: Long) {
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.getProperties()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!.filter { it.idCategory == propertyId })
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun removeProperty(propertyCategory: Long) {
        productRepository.removeProperty(propertyCategory)
    }

    fun loadCreateProductData() {
        viewState.loaded(productRepository.loadCreateProductData())
    }

    fun collectMaterialSearchFlow(flow: Flow<String>, materials: List<Property>) {
         flow
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

    fun collectRegionSearchFlow(flow: Flow<String>, regions: List<Region>) {
         flow
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

    fun collectBrandSearchFlow(flow: Flow<String>, brands: List<ProductBrand>) {
         flow
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
                    getProperties(5)
                } else {
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
                    getProperties(2)
                } else {
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
                    getProperties(8)
                } else {
                    getProperties(7)
                }
            }
        }
    }

    fun getSizesShoos(forWho: Int) {
        when (forWho) {
            0 -> {
                getProperties(6)
            }
            1 -> {
                getProperties(3)
            }
            2 -> {
                getProperties(9)
            }
        }
    }

    fun tokenCheck() {
        viewState.loaded(authRepository.isTokenEmpty())
    }

    fun clearCreateProductData() {
        productRepository.clearCreateProductData()
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
                else -> viewState.error(errorMessage(response))
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

    fun getBrands() {
        presenterScope.launch {
            viewState.loading()
            val response = productRepository.getBrands()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
                else -> viewState.error(errorMessage(response))
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

                }
                404 -> {


                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun changeProductAndPublish() {
        updateCreateProductData(status = 0)
        changeProduct()
    }

    fun getActiveCard(){
        presenterScope.launch {
            viewState.loading()
            val response = cardRepository.getCards()
            when(response?.code()){
                200 -> viewState.loaded(response.body()!!.find { it.active == 1 } ?: emptyList<Card>())
                404 -> {viewState.loaded(emptyList<Card>())}
                else -> viewState.error(errorMessage(response))
            }
        }
    }

}