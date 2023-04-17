package com.project.morestore.domain.presenters

import com.project.morestore.data.models.User
import com.project.morestore.data.repositories.*
import com.project.morestore.presentation.mvpviews.CabinetMvpView
import com.project.morestore.util.errorMessage
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class CabinetPresenter @Inject constructor(private val userRepository: UserRepository,
                                           private val authRepository: AuthRepository,
                                           private val productRepository: ProductRepository,
                                           private val salesRepository: SalesRepository,
                                           private val ordersRepository: OrdersRepository,
                                           private val cartRepository: CartRepository,
                                           private val reviewRepository: ReviewRepository): MvpPresenter<CabinetMvpView>() {

    private lateinit var user: User


    fun checkToken() {
        viewState.isLoggedIn(authRepository.isTokenEmpty().not())
    }

    fun getUserData() {
        presenterScope.launch {
            viewState.loading(true)
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

     private fun getUserInfo() {
        presenterScope.launch {
            viewState.loading(true)
            val response = userRepository.getCurrentUserInfo()
            when (response?.code()) {
                200 -> {
                    viewState.loading(false)
                    user = response.body()!!
                    viewState.currentUserLoaded(response.body()!!)
                }
                else -> {
                    viewState.loading(false)
                    viewState.error(errorMessage(response))
                }
            }
        }
    }

    fun getUserProductsCounts(){
        presenterScope.launch {
            val response = productRepository.getCurrentUserProducts()
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
                      viewState.showProductsCounts(
                          listOf(
                              response.body()!!
                                  .filter { it.status == 1 || it.status == 6 }.size,
                              response.body()!!.filter { it.status == 0 }.size,
                              response.body()!!.filter { it.status == 8 }.size,
                              response.body()!!.filter { it.status == 5 }.size

                          )
                      )
                  }
                else -> {}
            }
        }
    }




    fun getSalesCount() {
        presenterScope.launch {
                val salesItems = salesRepository.getSales()?.body().orEmpty()
                val activeSalesFiltered = salesItems.filter { salesItems.find { saleCheck ->
                saleCheck.id != it.id && saleCheck.cart?.first()?.id == it.cart?.first()?.id &&
                        saleCheck.idUser == it.idUser && saleCheck.id > it.id } == null }
                val finishedSales = salesItems.filter{it.status == 1}
                viewState.showSalesCount(activeSalesFiltered.filter { it.status == 0 && it.cart != null }.size, finishedSales.size)
            }
    }

    fun getOrdersCount(){
        presenterScope.launch {
                val orderItems = ordersRepository.getAllOrders()?.body().orEmpty()
                val cartItems = cartRepository.getCartItems(authRepository.getUserId())?.body().orEmpty()
                val filteredOrderItems = orderItems.filter { orderItems.find { orderCheck -> orderCheck.id != it.id && orderCheck.cart?.first()?.id == it.cart?.first()?.id &&
                    orderCheck.id > it.id } == null &&
                    cartItems.find { cartItem -> cartItem.product.id == it.cart?.first()?.id  } == null
                    && orderItems.find { initialOrder -> initialOrder.id != it.id && initialOrder.cart?.first()?.id == it.cart?.first()?.id &&
                    initialOrder.status == 1} == null}
            val inactiveOrders = orderItems.filter{it.status == 1}
            viewState.showOrdersCount(filteredOrderItems.filter { it.status == 0 && it.cart != null }.size, inactiveOrders.size)
            }
        }

    fun getReviewCount(){
        presenterScope.launch {
            val reviews = reviewRepository.getReviews(authRepository.getUserId())
            viewState.showReviewCount(reviews.size)
        }
    }

    fun getUser() = user

    fun setUser(user: User) {
        this.user = user
    }

    fun clearToken() {
        presenterScope.launch {
            authRepository.clearToken()

        }
    }
}