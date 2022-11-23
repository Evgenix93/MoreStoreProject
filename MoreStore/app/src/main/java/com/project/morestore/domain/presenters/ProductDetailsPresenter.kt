package com.project.morestore.domain.presenters

import com.project.morestore.data.models.*
import com.project.morestore.data.repositories.*
import com.project.morestore.presentation.mvpviews.ProductDetailsMvpView
import com.project.morestore.util.ProductStatus
import com.project.morestore.util.errorMessage
import com.project.morestore.util.getStringFromResponse
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class ProductDetailsPresenter @Inject constructor(private val userRepository: UserRepository,
                              private val cartRepository: CartRepository,
                              private val productRepository: ProductRepository,
                              private val authRepository: AuthRepository,
                              private val chatRepository: ChatRepository): MvpPresenter<ProductDetailsMvpView>() {

    fun updateBrand(brand: ProductBrand) {
        val chosenForWho = userRepository.getFilter().chosenForWho
        val filter = Filter()
        filter.chosenForWho = chosenForWho
        filter.brands = listOf(ProductBrand(0, "Stub", 0, null, null), brand.apply { isChecked = true })
        userRepository.updateFilter(filter)
        viewState.success()
    }

    fun addProductToWishList(id: Long) {
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.addProductToWishList(BrandWishList(listOf(id)))
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
                404 -> {
                    viewState.loaded(emptyList<Product>())
                    viewState.error(response.errorBody()!!.getStringFromResponse())

                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun addProductToCart(
        productId: Long,
        userId: Long? = null,
    ) {
        presenterScope.launch {
            viewState.loading()
            val response = cartRepository.addCartItem(
                productId = productId,
                userId = userId
            )

            when (response?.code()) {
                200 -> {
                    viewState.loaded("")
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun removeProductFromCart(
        productId: Long,
        userId: Long? = null,
    ) {
        presenterScope.launch {
            viewState.loading()
            val response = cartRepository.removeCartItem(
                productId = productId,
                userId = userId
            )

            when (response?.code()) {
                200 -> {
                    viewState.loaded("")
                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun getProductWishList() {
        presenterScope.launch {
            viewState.loading()
            val response = userRepository.getProductWishList()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
                404 -> {
                    viewState.loaded(emptyList<Product>())
                    viewState.error(response.errorBody()!!.getStringFromResponse())

                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun getProducts(
        queryStr: String? = null,
        productId: Long? = null,
        isFiltered: Boolean,
        productCategories: List<ProductCategory>? = null,
        forWho: List<Boolean>? = null,
        isGuest: Boolean = false,
        status: Int? = 1,
        useDefaultFilter: Boolean = false
    ) {
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
                    productId = productId,
                    limit = 500,
                    isGuest = isGuest,
                    status = status
                )
            } else {
                productRepository.getProducts(
                    query = queryStr,
                    filter = if (isFiltered){
                        if(!useDefaultFilter)
                            userRepository.getFilter()
                        else {
                            val currentForWho = userRepository.getFilter().chosenForWho
                            val filter = Filter()
                            filter.chosenForWho = currentForWho
                            filter
                        }
                    } else null,
                    productId = productId,
                    limit = 500,
                    isGuest = isGuest,
                    status = status
                )
            }
            when (response?.code()) {
                200 -> {
                    val filter = userRepository.getFilter()
                    val currentUserId = authRepository.getUserId()
                    response.body()?.forEach {
                        val productStatus = when (it.statusUser?.order?.status) {
                            0 -> if (it.statusUser.order.idUser == currentUserId && it.statusUser.buy?.status != 2) ProductStatus.APPROVED.value
                            else if (it.idUser == currentUserId && it.statusUser.buy?.status != 2) ProductStatus.APPROVED.value
                            else if (it.statusUser.buy?.status != 2) ProductStatus.BOOKED.value else it.status
                            1 -> ProductStatus.SOLD.value
                            null -> it.status
                            else -> it.status
                        }
                        it.status = productStatus
                    }
                    if(isFiltered) {
                        val products = response.body()!!.toMutableList()
                        filter.chosenProductStatus.forEachIndexed { index, chosen ->
                            if (chosen.not())
                                when (index) {
                                    0 -> products.removeAll(
                                        response.body()!!.filter { it.status == 1 })
                                    1 -> products.removeAll(
                                        response.body()!!
                                            .filter { it.status == 6 || it.status == 7 })
                                    2 -> products.removeAll(
                                        response.body()!!.filter { it.status == 8 })
                                }
                        }
                        if (productId == null) viewState.loaded(products.toList()) else viewState.loaded(
                            products.toList().first()
                        )
                    }else{
                        if (productId == null) viewState.loaded(response.body()!!) else viewState.loaded(
                            response.body()!!.first()
                        )
                    }
                }
                404 -> {
                    viewState.loaded(emptyList<Product>())


                }
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun shareProduct(id: Long) {
        viewState.loaded(productRepository.getShareProductIntent(id))
    }

    fun getYouMayLikeProducts(category: Category?) {
        presenterScope.launch {
            viewState.loading()
            val response = if (category == null) productRepository.getYouMayLikeProducts(4, authRepository.getUserId())
            else productRepository.getProducts(limit = 4, filter = Filter(categories = listOf(ProductCategory(category.id, category.name, true)), chosenForWho = listOf()))
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
                else -> viewState.error(errorMessage(response))
            }
        }
    }

    fun getUserId() {
        viewState.loaded(authRepository.getUserId())
    }

    fun getDialogs() {
        presenterScope.launch {
            val response = chatRepository.getDialogs()
            when (response?.code()) {
                200 -> viewState.loaded(response.body()!!)
            }
        }
    }

    fun viewProduct(idProduct: Long){
        presenterScope.launch{
            val response = productRepository.viewProduct(authRepository.getUserId(), idProduct)
            when(response?.code()){
                200 -> viewState.loaded(response.body()!!)
            }
        }
    }

    fun loadCart(userId: Long?, productId: Long, callback: (hasValue: Boolean)->Unit) {
        presenterScope.launch {
            val response = cartRepository.getCartItems(
                userId = userId
            )

            when (response?.code()) {
                200 -> {
                    val inCart = response.body()?.find { it.product.id == productId }
                    callback(inCart != null)
                }
                401 -> {}
                else -> viewState.error(errorMessage(response))
            }
        }
    }


}