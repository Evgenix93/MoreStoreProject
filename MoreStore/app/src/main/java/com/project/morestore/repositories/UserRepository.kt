package com.project.morestore.repositories

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.project.morestore.models.*
import com.project.morestore.singletones.FilterState
import com.project.morestore.singletones.Network
import com.project.morestore.singletones.Token
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.File
import java.io.IOException

class UserRepository(val context: Context) {
    private val userApi = Network.userApi

    suspend fun changeUserData(
        phone: String? = null,
        email: String? = null,
        name: String? = null,
        surname: String? = null,
        sex: String? = null,
        step: Int? = null,
        code: Int? = null
    ): Response<RegistrationResponse>? {
        return try {
            userApi.changeUserData(
                email = email,
                phone = phone,
                name = name,
                surname = surname,
                sex = sex,
                step = step,
                code = code
            )
        } catch (e: Throwable) {
            if (e is IOException) {
                null
            } else {
                try {
                    val response = userApi.changeUserDataGetError(
                        email = email,
                        phone = phone,
                        step = step,
                        code = code,
                        name = name,
                        surname = surname,
                        sex = sex
                    )
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

    suspend fun changeUserData2(
        phone: String? = null,
        email: String? = null,
        step: Int? = null,
        code: String? = null
    ): Response<RegistrationResponse2>? {
        return try {
           /* userApi.changeUserData2(
                email = email,
                phone = phone,
                step = step,
                code = code
            )*/
            Log.d("MyTestTag", "fun changeUserData2()")
            userApi.changeUserData2(RegistrationData(
                phone = phone,
                step = step,
                code = code
            ))
        } catch (e: Throwable) {
            Log.d("mylog", e.message.toString())
            if (e is IOException) {
                null
            } else {
                try {
                    val response = userApi.changeUserData2GetError(
                        email = email,
                        phone = phone,
                        step = step,
                        code = code
                    )
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Log.d("mylog", e.message.toString())
                    Response.error(400, "ошибка".toResponseBody(null))
                }

            }
        }

    }

    suspend fun uploadPhoto(uri: Uri): Response<Unit>? {
        return withContext(Dispatchers.IO) {
            val file = File(
                context.cacheDir,
                "photo.${context.contentResolver.getType(uri)?.substringAfter('/') ?: "jpg"}"
            )
            context.contentResolver.openInputStream(uri).use { input ->
                file.outputStream().use { output ->
                    input?.copyTo(output)
                }

            }
            val photo = PhotoVideo(
                file.name.substringAfter('.'),
                Base64.encodeToString(file.readBytes(), Base64.DEFAULT)
            )
            val photoData = PhotoData(
                "EditUser",
                Token.userId,
                listOf(photo)
            )
            try {
                userApi.uploadPhoto(photoData)
            } catch (e: Exception) {
                if (e is IOException) {
                    null
                } else {
                    try {
                        val response = userApi.uploadPhotoGetError(photoData)
                        if(response.code() == 500){
                            Response.error(500, "".toResponseBody(null))
                        }else {
                            Response.error(400, response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null))
                        }
                    }catch (e: Throwable){
                        Response.error(400, "ошибка".toResponseBody(null))
                    }
                }
            }
        }
    }

    suspend fun getCityByCoordinates(coordinates: String): Response<Address>?{
        return try {
            userApi.getCityByCoords(coordinates)
        } catch (e: Throwable) {
            Log.d("mylog", e.message.toString())
            if (e is IOException) {
                null
            } else {
                try {
                    val response = userApi.getCityByCoordsGetError(coordinates)
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Log.d("mylog", e.message.toString())
                    Response.error(400, "ошибка".toResponseBody(null))
                }

            }
        }

    }

    suspend fun addBrandsToWishList(wishList: BrandWishList): Response<List<Long>>?{
        return try {
            userApi.addBrandsToWishList(wishList)
        } catch (e: Throwable) {
            Log.d("mylog", e.message.toString())
            if (e is IOException) {
                null
            } else {
                try {
                    val response = userApi.addBrandsToWishListGetError(wishList)
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Log.d("mylog", e.message.toString())
                    Response.error(400, "ошибка".toResponseBody(null))
                }

            }
        }

    }

    suspend fun getBrandWishList(): Response<List<ProductBrand>>?{
        return try {
            userApi.getBrandWishList()
        } catch (e: Throwable) {
            Log.d("mylog", e.message.toString())
            if (e is IOException) {
                null
            } else {
                try {
                    val response = userApi.getBrandWishListGetError()
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Log.d("mylog", e.message.toString())
                    Response.error(400, "ошибка".toResponseBody(null))
                }

            }
        }

    }

    suspend fun addProductToWishList(wishList: BrandWishList): Response<List<Long>>?{
        return try {
            userApi.addProductToWishList(wishList)
        } catch (e: Throwable) {
            Log.d("mylog", e.message.toString())
            if (e is IOException) {
                null
            } else {
                try {
                    val response = userApi.addProductToWishListGetError(wishList)
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Log.d("mylog", e.message.toString())
                    Response.error(400, "ошибка".toResponseBody(null))
                }

            }
        }

    }

    suspend fun getProductWishList(): Response<List<Product>>? {
        return try {
            userApi.getProductWishList()
        } catch (e: Throwable) {
            Log.d("mylog", e.message.toString())
            if (e is IOException) {
                null
            } else {
                try {
                    val response = userApi.getProductWishListGetError()
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Log.d("mylog", e.message.toString())
                    Response.error(400, "ошибка".toResponseBody(null))
                }

            }
        }

    }

    suspend fun getSellersWishList(): Response<List<User>>?{
        return try {
            userApi.getSellersWishList()
        } catch (e: Throwable) {
            Log.d("mylog", e.message.toString())
            if (e is IOException) {
                null
            } else {
                try {
                    val response = userApi.getSellersWishListGetError()
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Log.d("mylog", e.message.toString())
                    Response.error(400, e.message.orEmpty().toResponseBody(null))
                }

            }
        }

    }

    suspend fun addDeleteSellerInWishList(wishList: BrandWishList): Response<List<Long>>?{
        return try {
            userApi.addDeleteSellerToWishList(wishList)
        } catch (e: Throwable) {
            Log.d("mylog", e.message.toString())
            if (e is IOException) {
                null
            } else {
                try {
                    val response = userApi.addDeleteSellerToWishListGetError(wishList)
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Log.d("mylog", e.message.toString())
                    Response.error(400, e.message.orEmpty().toResponseBody(null))
                }

            }
        }

    }

   suspend fun getFavoriteSearches(): Response<List<FavoriteSearch>>?{
        return try {
            userApi.getFavoriteSearches()
        } catch (e: Throwable) {
            Log.d("mylog", e.message.toString())
            if (e is IOException) {
                null
            } else {
                try {
                    val response = userApi.getFavoriteSearchesGetError()
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Log.d("mylog", e.message.toString())
                    Response.error(400, e.message.orEmpty().toResponseBody(null))
                }

            }
        }

    }

    suspend fun getFavoriteSearchById(id: Long): Response<FavoriteSearch>?{
        return try {
            userApi.getFavoriteSearchById(id)
        } catch (e: Throwable) {
            Log.d("mylog", e.message.toString())
            if (e is IOException) {
                null
            } else {
                try {
                    val response = userApi.getFavoriteSearchByIdGetError(id)
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Log.d("mylog", e.message.toString())
                    Response.error(400, e.message.orEmpty().toResponseBody(null))
                }

            }
        }

    }

    suspend fun saveFavoriteSearch(search: FavoriteSearchValue): Response<Id>?{
        return try {
            userApi.saveFavoriteSearch(search)
        } catch (e: Throwable) {
            Log.d("mylog", e.message.toString())
            if (e is IOException) {
                null
            } else {
                try {
                    val response = userApi.saveFavoriteSearchGetError(search)
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Log.d("mylog", e.message.toString())
                    Response.error(400, e.message.orEmpty().toResponseBody(null))
                }

            }
        }

    }

    suspend fun editFavoriteSearch(search: FavoriteSearchValue): Response<FavoriteSearchValue>?{
        return try {
            userApi.editFavoriteSearch(search)
        } catch (e: Throwable) {
            Log.d("mylog", e.message.toString())
            if (e is IOException) {
                null
            } else {
                try {
                    val response = userApi.editFavoriteSearchGetError(search)
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Log.d("mylog", e.message.toString())
                    Response.error(400, e.message.orEmpty().toResponseBody(null))
                }

            }
        }

    }

    suspend fun deleteFavoriteSearch(id: Id): Response<Boolean>?{
        return try {
            userApi.deleteFavoriteSearch(id)
        } catch (e: Throwable) {
            Log.d("mylog", e.message.toString())
            if (e is IOException) {
                null
            } else {
                try {
                    val response = userApi.deleteFavoriteSearchGetError(id)
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Log.d("mylog", e.message.toString())
                    Response.error(400, e.message.orEmpty().toResponseBody(null))
                }

            }
        }

    }

    suspend fun getCurrentUserInfo(): Response<User>?{
        return try {
            userApi.getUserInfoById(Token.userId)
        } catch (e: Throwable) {
            Log.d("mylog", e.message.toString())
            if (e is IOException) {
                null
            } else {
                try {
                    val response = userApi.getUserInfoByIdGetError(Token.userId)
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Log.d("mylog", e.message.toString())
                    Response.error(400, "ошибка".toResponseBody(null))
                }

            }
        }

    }

    suspend fun getSellerInfo(userId: Long): Response<User>?{
        return try {
            userApi.getUserInfoById(userId)
        } catch (e: Throwable) {
            Log.d("mylog", e.message.toString())
            if (e is IOException) {
                null
            } else {
                try {
                    val response = userApi.getUserInfoByIdGetError(Token.userId)
                    if (response.code() == 500) {
                        Response.error(500, "".toResponseBody(null))
                    } else {
                        Response.error(
                            400,
                            response.body()?.toResponseBody(null) ?: "ошибка".toResponseBody(null)
                        )
                    }
                } catch (e: Throwable) {
                    Log.d("mylog", e.message.toString())
                    Response.error(400, "ошибка".toResponseBody(null))
                }

            }
        }
    }

    suspend fun saveFilter(): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val filterJsonString = Moshi.Builder().build().adapter(Filter::class.java).toJson(
                    FilterState.filter)
                val sharedPrefs = context.getSharedPreferences(ProductRepository.USER_PREFS, Context.MODE_PRIVATE)
                sharedPrefs.edit().putString(ProductRepository.FILTER_KEY, filterJsonString).commit()
            }
        } catch (e: Throwable) {
            Log.e("Debug", e.message.orEmpty(), e)
            false
        }
    }

    suspend fun loadFilter(){
        if(FilterState.isLoadedFilter)
            return
        withContext(Dispatchers.IO){
            val sharedPrefs = context.getSharedPreferences(ProductRepository.USER_PREFS, Context.MODE_PRIVATE)
            val filterJsonString = sharedPrefs.getString(ProductRepository.FILTER_KEY, null)
            if (filterJsonString != null){
                FilterState.filter = Moshi.Builder().build().adapter(Filter::class.java).fromJson(filterJsonString)!!
            }else{
                try{
                    val propertiesId = Network.userApi.loadBrandsProperties(Token.userId).body()?.last()?.data
                        ?.property?.split(';')?.mapNotNull{it.toLongOrNull()}
                    when{
                        propertiesId?.any{it == 140L} == true -> FilterState.filter.chosenForWho = listOf(true, false, false)
                        propertiesId?.any{it == 141L} == true -> FilterState.filter.chosenForWho = listOf(false, true, false)
                        propertiesId?.any{it == 142L} == true -> FilterState.filter.chosenForWho = listOf(false, false, true)
                    }
                }catch(e: Throwable){

                }
            }
            FilterState.isLoadedFilter = true
        }
    }

    fun getFilter(): Filter{
        return FilterState.filter
    }

    fun updateFilter(filter: Filter){
        FilterState.filter = filter
    }

   suspend fun clearFilter(){
       FilterState.filter = Filter()
        try{
            val propertiesId = Network.userApi.loadBrandsProperties(Token.userId).body()?.last()?.data
                ?.property?.split(';')?.mapNotNull{it.toLongOrNull()}
            when{
                propertiesId?.any{it == 140L} == true -> FilterState.filter.chosenForWho = listOf(true, false, false)
                propertiesId?.any{it == 141L} == true -> FilterState.filter.chosenForWho = listOf(false, true, false)
                propertiesId?.any{it == 142L} == true -> FilterState.filter.chosenForWho = listOf(false, false, true)
            }
        }catch(e: Throwable){

        }
    }

    fun reserveFilter(){
        FilterState.filterTemp = FilterState.filter
    }

    fun restoreFilter(){
        FilterState.filter = FilterState.filterTemp
    }

    fun loadMaterials(): List<MaterialLine>{
        return FilterState.filter.chosenMaterials
    }

    fun saveConditions(conditions: List<Boolean>){
        FilterState.filter.chosenConditions = conditions
    }

    fun loadConditions(): List<Boolean>{
        return FilterState.filter.chosenConditions

    }

    fun loadTopSizes(): List<SizeLine>{
        return FilterState.filter.chosenTopSizesWomen
    }

    fun loadBottomSizes(): List<SizeLine>{
        return FilterState.filter.chosenBottomSizesWomen
    }

    fun loadShoosSizes(): List<SizeLine>{
        return FilterState.filter.chosenShoosSizesWomen
    }

    fun loadProductStatuses(): List<Boolean>{
        return FilterState.filter.chosenProductStatus
    }

   suspend fun saveBrandsProperties(brandsId: List<Long>?, propertiesId: List<Long>): Response<Boolean>?{
      return  try {
            Network.userApi.saveBrandsProperties(BrandsPropertiesData(Token.userId, brandsId, propertiesId))
        }catch(e: Throwable){
            Log.e("MyDebug", "error = ${e.message}")
            null
        }
    }

    suspend fun loadBrandsProperties(): Response<List<BrandsPropertiesDataWrapper>>?{
    return try {
        Log.d("MyDebug", "loadBrandsProperties")
        Network.userApi.loadBrandsProperties(Token.userId)
    }catch (e: Throwable){
        Log.e("MyDebug", "error = ${e.message}")
        null
    }
    }

    fun changeCurrentUserAddress(address: Address){
        Token.currentUserAddress = address
    }
    fun getCurrentUserAddress(): Address?{
        return Token.currentUserAddress
    }

   suspend fun getWaitReviewSellers(): Response<List<User>>?{
       return try{
           Network.userApi.getWaitReviewSellers()
       }catch (e: Throwable){
           Log.e("MyDebug", "error = ${e.message}")
           null
       }
    }

  suspend fun getUser(id: Long): Response<User>?{
      return try {
          Network.userApi.getUser(id)
      }catch (e: Throwable){
        if(e is IOException)
            null
          else try{
              val error = Network.userApi.getUserError(id)
              Response.error(400, error.toResponseBody())
          }catch (e: Throwable){
              Response.error(0, "Ошибка".toResponseBody())
          }
      }
  }

    suspend fun blockUnblockUser(id: Long): Response<String>?{
        return try {
            userApi.blockUnblockUser(id)
        }catch (e: Throwable){
            if(e is IOException)
                null
            else
                Response.error(400, e.message.orEmpty().toResponseBody())
        }
    }

  suspend fun getBlackList(): Response<List<User>>?{
      return try {
          Network.userApi.getBlackList()
      }catch (e: Throwable){
          Log.e("MyDebug", "getBlackListError ${e.message}")
          if(e is IOException)
              null
          else
             Response.error(0, e.message?.toResponseBody() ?: "".toResponseBody())
      }
  }
}