package com.project.morestore.repositories

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.core.content.FileProvider
import com.project.morestore.models.*
import com.project.morestore.singletones.CreateProductData

import com.project.morestore.singletones.FilterState

import com.project.morestore.singletones.Network
import com.project.morestore.singletones.Token
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.File
import java.io.IOException

class ProductRepository(private val context: Context) {
    private val onBoardingApi = Network.onBoardingApi
    private val productApi = Network.productApi

    suspend fun getAllSizes(): Response<List<Size>>? {
        return try {
            onBoardingApi.getAllSizes()
        } catch (e: Throwable) {
            if (e is IOException) {
                null
            } else {
                try {
                    val response = onBoardingApi.getAllSizesGetError()
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }
        }
    }


    suspend fun getCategories(): Response<List<Category>>? {
        return try {
            onBoardingApi.getCategories()
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                try {
                    val response = onBoardingApi.getCategoriesGetError()
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }
        }
    }

    suspend fun getProducts(
        query: String? = null,
        filter: Filter? = null,
        userId: Long? = null,
        productId: Long? = null,
        limit: Int? = null
    ): Response<List<Product>>? {
        return try {
            var categoryStr = listOf<String>()
            var brandsStr = listOf<String>()
            var citiesStr = listOf<String>()
            var queryStr = listOf<String>()
            var productIdStr = listOf<String>()
            var productPropertyStr = listOf<String>()

            if (filter?.categories?.isNotEmpty() == true) {
                categoryStr =
                    filter.categories.filter { it.isChecked == true }.map { "id_category=${it.id}" }
            }
            if (filter?.brands?.isNotEmpty() == true) {
                val brandsIds =
                    filter.brands.filter { it.isChecked == true }.map { it.id }
                brandsStr = listOf("id_brand=${brandsIds.joinToString(",")}")

            }
            if (filter?.regions?.isNotEmpty() == true) {
                citiesStr =
                    if (filter.regions.all { it.isChecked == true })
                        listOf()
                    else
                        filter.regions.filter { it.isChecked == true }.map { "id_city=${it.id}" }
            }



            if (filter?.isCurrentLocationFirstLoaded == false && filter.currentLocation != null) {
                Log.d("mylog", "load current location")
                citiesStr = citiesStr + listOf("id_city=${filter.currentLocation?.id}")
                Log.d("mylog", "citystr = $citiesStr")
            }

            if (!query.isNullOrEmpty()) {
                queryStr = listOf("text=${query.orEmpty()}")
            }

            productId?.let {
                productIdStr = listOf("id=$it")
            }

            val statusStr = "status= 1"

            productPropertyStr =
                filter?.chosenTopSizes?.filter { it.isSelected }
                    ?.map { "property[${it.idCategory}][${it.id}]=on" }
                    .orEmpty() + filter?.chosenBottomSizes?.filter { it.isSelected }
                    ?.map { "property[${it.idCategory}][${it.id}]=on" }
                    .orEmpty() + filter?.chosenShoosSizes?.filter { it.isSelected }
                    ?.map { "property[${it.idCategory}][${it.id}]=on" }
                    .orEmpty() + filter?.chosenTopSizesMen?.filter { it.isSelected }
                    ?.map { "property[${it.idCategory}][${it.id}]=on" }
                    .orEmpty() + filter?.chosenBottomSizesMen?.filter { it.isSelected }
                    ?.map { "property[${it.idCategory}][${it.id}]=on" }
                    .orEmpty() + filter?.chosenShoosSizesMen?.filter { it.isSelected }
                    ?.map { "property[${it.idCategory}][${it.id}]=on" }
                    .orEmpty() + filter?.chosenTopSizesKids?.filter { it.isSelected }
                    ?.map { "property[${it.idCategory}][${it.id}]=on" }
                    .orEmpty() + filter?.chosenBottomSizesKids?.filter { it.isSelected }
                    ?.map { "property[${it.idCategory}][${it.id}]=on" }
                    .orEmpty() + filter?.chosenShoosSizesKids?.filter { it.isSelected }
                    ?.map { "property[${it.idCategory}][${it.id}]=on" }
                    .orEmpty() + filter?.chosenMaterials?.filter { it.isSelected }
                    ?.map { "property[${it.idCategory}][${it.id}]=on" }
                    .orEmpty() + filter?.colors?.filter { it.isChecked == true }
                    ?.map { "property[${it.idCategory}][${it.id}]=on" }.orEmpty()


            var conditionList = listOf<String>()
            if (filter?.chosenConditions?.isNotEmpty() == true) {
                if (filter!!.chosenConditions[0]) {
                    conditionList = conditionList + listOf<String>("property[11][111]=on")
                }
                if (filter.chosenConditions[1]) {
                    conditionList = conditionList + listOf<String>("property[11][112]=on")
                }

                if (filter.chosenConditions[2]) {
                    conditionList = conditionList + listOf<String>("property[11][113]=on")
                }

                if (filter.chosenConditions[3]) {
                    conditionList = conditionList + listOf<String>("property[11][114]=on")
                }


            }

            var forWhoList = listOf<String>()
            if (filter?.chosenForWho?.isNotEmpty() == true) {
                if (filter!!.chosenForWho[0]) {
                    forWhoList = forWhoList + listOf<String>("property[14][140]=on")
                }
                if (filter.chosenForWho[1]) {
                    forWhoList = forWhoList + listOf<String>("property[14][141]=on")
                }

                if (filter.chosenForWho[2]) {
                    forWhoList = forWhoList + listOf<String>("property[14][142]=on")
                }


            }

            var stylesList = listOf<String>()
            if (filter?.chosenStyles?.isNotEmpty() == true) {
                if (filter.chosenStyles[1].isChecked == true) {
                    stylesList = stylesList + listOf<String>("property[10][108]=on")
                }
                if (filter.chosenStyles[2].isChecked == true) {
                    stylesList = stylesList + listOf<String>("property[10][109]=on")
                }

                if (filter.chosenStyles[3].isChecked == true) {
                    stylesList = stylesList + listOf<String>("property[10][110]=on")
                }


            }

            productPropertyStr = productPropertyStr + conditionList + forWhoList + stylesList
            productApi.getProducts(
                limit,
                PRODUCT_OPTIONS,
                (categoryStr + brandsStr + citiesStr + queryStr + productIdStr + productPropertyStr + statusStr).joinToString(
                    ";"
                ),
                userId
            )

        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = productApi.getProductsGetError(
                        PRODUCT_OPTIONS,
                        listOf<String>().joinToString(";"),
                        userId
                    )
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(404, "не найдено".toResponseBody(null))
                    }
                } catch (e: Throwable) {
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }
        }
    }

    suspend fun getCurrentUserProducts(): Response<List<Product>>? {
        return getProducts(userId = Token.userId.toLong())
    }

    suspend fun getSellerProducts(userId: Long): Response<List<Product>>? {
        return getProducts(userId = userId)
    }

    suspend fun getYouMayLikeProducts(limit: Int, userId: Long): Response<List<Product>>? {
        return try {
            productApi.getYouMayLikeProducts(limit, userId)
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = productApi.getYouMayLikeProductsGetError(limit, userId)
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }
        }
    }

    suspend fun getSearchSuggestions(query: String): Response<List<Suggestion>>? {
        return try {
            productApi.getSearchSuggestions(query)
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = productApi.getSearchSuggestionsGetError(query)
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }
        }
    }

    suspend fun getCities(): Response<List<Region>>? {
        return try {
            productApi.getCities()
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = productApi.getCitiesGetError()
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }
        }
    }

    suspend fun getBrands(): Response<List<ProductBrand>>? {
        return try {
            productApi.getAllBrands()
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = productApi.getAllBrandsGetError()
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }
        }
    }

    suspend fun getProperties(): Response<List<Property>>? {
        return try {
            productApi.getProperties()
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = productApi.getPropertiesGetError()
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }
        }
    }


    fun saveSizes(
        topSizesList: List<Size>,
        bottomSizesList: List<Size>,
        shoesSizesList: List<Size>
    ) {
        // return try {
        //suspend fun saveSizes(topSizesList: List<Size>, bottomSizesList: List<Size>, shoesSizesList: List<Size>){
        /* return try {
             withContext(Dispatchers.IO){
                 //error("error")
                 val prefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
                 val stringSet = mutableSetOf<String>()
                 val stringSet2 = mutableSetOf<String>()
                 val stringSet3 = mutableSetOf<String>()

                 for(size in topSizesList){
                     stringSet.add(size.id.toString())
                 }
                 for(size in bottomSizesList){
                     stringSet2.add(size.id.toString())
                 }
                 for(size in shoesSizesList){
                     stringSet3.add(size.id.toString())
                 }
                 prefs.edit().apply {
                     putStringSet(TOP_SIZES_KEY, stringSet )
                     putStringSet(BOTTOM_SIZES_KEY, stringSet2)
                     putStringSet(SHOES_SIZES_KEY, stringSet3)
                 }.commit()

             }
         }catch (e: Throwable){
             false
         }*/
        val sizeList = listOf(
            SizeLine(
                0,
                "XXS",
                "26-27",
                "42",
                "32",
                "32",
                false,
                -1
            ),
            SizeLine(
                0,
                "XS",
                "28-29",
                "44",
                "34",
                "34",
                false,
                -1
            ),
            SizeLine(
                0,
                "S",
                "30-31",
                "46",
                "36",
                "36",
                false,
                -1
            ),
            SizeLine(
                0,
                "M",
                "32-33",
                "48",
                "38",
                "38",
                false,
                -1
            ),
            SizeLine(
                0,
                "L",
                "34-35",
                "50",
                "40",
                "40",
                false,
                -1
            ),
            SizeLine(
                0,
                "XL",
                "36-37",
                "52",
                "42",
                "42",
                false,
                -1
            ),
            SizeLine(
                0,
                "XL",
                "36-37",
                "52",
                "42",
                "42",
                false,
                -1
            ),
            SizeLine(
                0,
                "XL",
                "36-37",
                "52",
                "42",
                "42",
                false,
                -1
            ),
            SizeLine(
                0,
                "XL",
                "36-37",
                "52",
                "42",
                "42",
                false,
                -1
            ),
            SizeLine(
                0,
                "XL",
                "36-37",
                "52",
                "42",
                "42",
                false,
                -1
            ),
            SizeLine(
                0,
                "XL",
                "36-37",
                "52",
                "42",
                "42",
                false,
                -1
            ),
            SizeLine(
                0,
                "XL",
                "36-37",
                "52",
                "42",
                "42",
                false,
                -1
            ),
            SizeLine(
                0,
                "XL",
                "36-37",
                "52",
                "42",
                "42",
                false,
                -1
            ),
            SizeLine(
                0,
                "XL",
                "36-37",
                "52",
                "42",
                "42",
                false,
                -1
            ),
            com.project.morestore.models.SizeLine(
                0,
                "",
                "",
                "",
                "",
                "",
                false,
                -1
            )
        )
        topSizesList.forEachIndexed { index, size ->
            if (sizeList[index].int == size.name)
                sizeList[index].isSelected = size.chosen ?: false
            sizeList[index].id = size.id
            sizeList[index].idCategory = size.id_category ?: -1
        }
        FilterState.filter.chosenTopSizes = sizeList
    }


    fun safeCategories(segmentsChecked: List<Boolean>) {
        FilterState.filter.segments = segmentsChecked
        FilterState.filter.isAllBrands = segmentsChecked.all { !it }
    }


    suspend fun loadSizes(): List<MutableSet<String>?> {
        return withContext(Dispatchers.IO) {
            val prefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
            val set = prefs.getStringSet(TOP_SIZES_KEY, null)
            val set2 = prefs.getStringSet(BOTTOM_SIZES_KEY, null)
            val set3 = prefs.getStringSet(SHOES_SIZES_KEY, null)
            listOf(set, set2, set3)
        }
    }

    suspend fun loadCategories(): MutableSet<String>? {
        return withContext(Dispatchers.IO) {
            val prefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
            prefs.getStringSet(CATEGORIES, null)
        }
    }

    suspend fun saveOnBoardingViewed(): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val prefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
                prefs.edit().putBoolean(ONBOARDINGVIEWED, true).commit()

            }
        } catch (e: Throwable) {
            false
        }
    }

    suspend fun loadOnBoardingViewed(): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val prefs = context.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
                prefs.getBoolean(ONBOARDINGVIEWED, false)

            }
        } catch (e: Throwable) {
            false
        }

    }

    suspend fun getProductCategoriesAdults(): Response<List<ProductCategoryAdults>>? {
        return try {
            Network.productApi.getProductCategoriesAdults()
        } catch (e: Throwable) {
            Log.e("Debug", e.message.orEmpty(), e)
            if (e is IOException)
                null
            else
                Response.error(400, "".toResponseBody())
        }
    }

    suspend fun getProductCategoriesKids(): Response<List<ProductCategoryKids1>>? {
        return try {
            Network.productApi.getProductCategoriesKids()
        } catch (e: Throwable) {
            Log.e("Debug", e.message.orEmpty(), e)
            if (e is IOException)
                null
            else
                Response.error(400, "".toResponseBody())
        }
    }

    fun getShareProductIntent(id: Long): Intent {
        val uri = Uri.withAppendedPath(Uri.parse("https://morestore.ru/products/"), id.toString())
        return Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, uri.toString())
            type = "text/plain"
        }
    }


    fun loadForWho(): List<Boolean> {
        return FilterState.filter.chosenForWho
    }

    suspend fun getColors(): Response<List<Property>>? {
        return try {
            Network.productApi.getProperties()
        } catch (e: Throwable) {
            if (e is IOException)
                null
            else
                Response.error(400, "".toResponseBody())
        }
    }

    suspend fun getProductCategories(): Response<List<ProductCategory>>? {
        return try {
            Network.productApi.getProductCategories()
        } catch (e: Throwable) {
            if (e is IOException)
                null
            else
                Response.error(400, "".toResponseBody(null))
        }
    }

    suspend fun addNewBrand(brand: NewProductBrand): Response<NewProductBrand>?{
        return try {
            productApi.addBrand(brand)
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = productApi.addBrandGetError(brand)
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Response.error(400, "ошибка".toResponseBody(null))
                }
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

        val property = when (forWho) {
            0 -> Property2(140, 14)
            1 -> Property2(141, 14)
            2 -> Property2(142, 14)
            else -> null
        }
        if(idCategory != null)
        CreateProductData.createProductData.idCategory = idCategory
        if(idBrand != null)
        CreateProductData.createProductData.idBrand = idBrand
        CreateProductData.createProductData.date = System.currentTimeMillis() / 1000
        CreateProductData.createProductData.dateEnd = (System.currentTimeMillis() + 3000000) / 1000
        CreateProductData.createProductData.status = 1
        if(phone != null)
        CreateProductData.createProductData.phone = phone
        if(price != null)
        CreateProductData.createProductData.price = price
        if(sale != null)
        CreateProductData.createProductData.sale = sale
        if(about != null)
        CreateProductData.createProductData.about = about
        if(address != null)
        CreateProductData.createProductData.address = address
        if(property != null)
            if(CreateProductData.createProductData.property == null)
              CreateProductData.createProductData.property = mutableListOf(property)
            else
                CreateProductData.createProductData.property!!.add(property)
        if(extProperty != null)
            if(CreateProductData.createProductData.property == null)
                CreateProductData.createProductData.property = mutableListOf(extProperty)
            else
                CreateProductData.createProductData.property!!.add(extProperty)

        if(extProperties != null)
            if(CreateProductData.createProductData.property == null)
                CreateProductData.createProductData.property = extProperties.toMutableList()
            else
                CreateProductData.createProductData.property!!.addAll(extProperties)

        if(id != null)
            CreateProductData.createProductData.id = id

        if(newPrice != null)
            CreateProductData.createProductData.priceNew = newPrice

        if(name != null)
            CreateProductData.createProductData.name = name

        Log.d("Debug", "createProductData = ${CreateProductData.createProductData}")
    }


    fun updateCreateProductPhotoVideo(photoVideo: File, position: Int){
        CreateProductData.productPhotosMap[position] = photoVideo
    }

    suspend fun updateCreateProductPhotoVideo(photoVideoUri: Uri, position: Int): Boolean{
        return withContext(Dispatchers.IO) {
            val file = File(
                context.cacheDir,
                "${System.currentTimeMillis()/1000}.${context.contentResolver.getType(photoVideoUri)?.substringAfter('/') ?: "jpg"}"
            )
            try {
                context.contentResolver.openInputStream(photoVideoUri).use { input ->
                    file.outputStream().use { output ->
                        input?.copyTo(output)
                    }
                }
                CreateProductData.productPhotosMap[position] = file
                Log.d("mylog", CreateProductData.productPhotosMap[position]?.name.toString())
                true
            }catch (e: Throwable){
                false
            }




        }

    }

    suspend fun updateCreateProductPhotoVideo(bitmap: Bitmap, position: Int): Boolean{
        return withContext(Dispatchers.IO) {
            try {
                val file = File(context.cacheDir, "${System.currentTimeMillis() / 1000}.jpg")
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, file.outputStream())
                CreateProductData.productPhotosMap[position] = file
                true
            }catch (e: Throwable){
                false
            }
        }
    }

    fun removeProperty(propertyCategory: Long) {
        CreateProductData.createProductData.property?.removeAll {
            it.propertyCategory == propertyCategory
        }
    }

    suspend fun createProduct(): Response<List<CreatedProductId>>? {
        Log.d("Debug", "productData = ${CreateProductData.createProductData}")

        return try {
            Network.productApi.createProduct(CreateProductData.createProductData)
        } catch (e: Throwable) {
            if (e is IOException)
                null
            else
                Response.error(400, "".toResponseBody(null))
        }
    }

    fun loadCreateProductData(): com.project.morestore.models.CreateProductData{
        return CreateProductData.createProductData
    }

    fun clearCreateProductData(){
        CreateProductData.createProductData = CreateProductData()
        CreateProductData.productPhotosMap.clear()
    }

    fun loadCreateProductPhotosVideos(): MutableMap<Int, File>{
        return CreateProductData.productPhotosMap
    }

    suspend fun uploadProductPhotos(files: List<File>, productId: Long): Response<List<ProductPhoto>>?{
        val photos = files.map { PhotoVideo(
            it.name.substringAfter('.'),
            Base64.encodeToString(it.readBytes(), Base64.DEFAULT)
        ) }
        val photoData = PhotoData("ProductPhoto", productId, photos)

        return try {
                productApi.uploadProductPhotos(photoData)
            } catch (e: Exception) {
                if (e is IOException) {
                    null
                } else {
                    Log.d("mylog", e.message.toString())
                    try {
                        val response = productApi.uploadProductPhotosGetError(photoData)
                        if (response.code() == 500) {
                            Response.error(500, "".toResponseBody(null))
                        } else {
                            Response.error(
                                400,
                                response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                            )
                        }
                    } catch (e: Throwable) {
                        Response.error(400, "ошибка".toResponseBody(null))
                    }
                }
            }




    }

    suspend fun uploadProductVideos(files: List<File>, productId: Long): Response<List<ProductVideo>>?{
        val videos = files.map { PhotoVideo(
            it.name.substringAfter('.'),
            Base64.encodeToString(it.readBytes(), Base64.DEFAULT)
        ) }
        val videoData = VideoData("Product", productId, videos)

        return try {
            productApi.uploadProductVideos(videoData)
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = productApi.uploadProductVideosGetError(videoData)
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }
        }




    }

    suspend fun changeProductStatus(productId: Long, status: Int): Response<Unit>?{
       return try{
           Network.productApi.changeProductStatus(ChangeStatus(productId, status))
       }catch (e: Throwable){
           return null
       }
    }

    suspend fun changeProductData(): Response<List<CreatedProductId>>?{
        return try {
            productApi.changeProduct(CreateProductData.createProductData)
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = productApi.changeProductGetError(CreateProductData.createProductData)
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }
        }
    }

    suspend fun deletePhotoBackground(file: File? = null, uri: Uri? = null): Response<List<ProductPhoto>>?{
        val photoFile = if(file != null){
            file
        }else{
            withContext(Dispatchers.IO){
                val fileToUpload = File(
                    context.cacheDir,
                    "${System.currentTimeMillis()/1000}.${context.contentResolver.getType(uri!!)?.substringAfter('/') ?: "jpg"}"
                )
                    context.contentResolver.openInputStream(uri).use { input ->
                        fileToUpload.outputStream().use { output ->
                            input?.copyTo(output)
                        }
                    }

                fileToUpload
            }
        }
        val photoData = PhotoData(
            "EditUser",
            -1,
            listOf(PhotoVideo("png", Base64.encodeToString(photoFile.readBytes(), Base64.DEFAULT)))
        )

        return try {
            productApi.deletePhotoBackground(photoData)
        } catch (e: Exception) {
            if (e is IOException) {
                null
            } else {
                Log.d("mylog", e.message.toString())
                try {
                    val response = productApi.deletePhotoBackgroundGetError(photoData)
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Response.error(400, "ошибка".toResponseBody(null))
                }
            }
        }
    }

    suspend fun getPlayVideoIntent(fileUri: Uri? = null, file: File? = null): Intent?{
        var videoToPlay: File? = null
        fileUri?.let {
            if(context.contentResolver.getType(it)?.contains("mp4") != true &&
                    it.toString().contains("mp4").not())
                return null

            if(it.toString().contains("content").not()){
                val webFile = downLoadFile(it.toString())
                webFile ?: return null
                videoToPlay = webFile

            }
        }

        file?.let {
            if(it.extension != "mp4")
                return null
            else videoToPlay = it
        }

        return Intent().apply {
            action = Intent.ACTION_VIEW
            type = "video/mp4"
            val uri = if(videoToPlay != null) FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName +
                        ".provider",
                videoToPlay!!)
            else fileUri
            data = uri
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

    }

    suspend fun downLoadFile(url: String): File?{
        return withContext(Dispatchers.IO){
            try{
                val webFile = File(context.externalCacheDir, "${System.currentTimeMillis()/1000}.mp4")
                val response = productApi.downloadImage(url)
                response.byteStream().use { input ->
                    webFile.outputStream().use { output ->
                        input.copyTo(output)

                    }
                }
                webFile

            }catch (e: Throwable){
                null
            }
        }

    }


    companion object {
        const val USER_PREFS = "user_prefs"
        const val TOP_SIZES_KEY = "top_sizes_key"
        const val BOTTOM_SIZES_KEY = "bottom_sizes_key"
        const val SHOES_SIZES_KEY = "shoes_sizes_key"
        const val CATEGORIES = "categoryIdList"
        const val ONBOARDINGVIEWED = "viewedOnBoarding"
        const val PRODUCT_OPTIONS =
            "service,user,category,property,statistics,brand,category,property_open_category"
        const val FILTER_KEY = "filter"
    }
}