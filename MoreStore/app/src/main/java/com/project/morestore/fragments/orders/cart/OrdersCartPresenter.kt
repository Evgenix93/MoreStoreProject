package com.project.morestore.fragments.orders.cart

import android.content.Context
import com.project.morestore.R
import com.project.morestore.adapters.cart.CartAdapter
import com.project.morestore.dialogs.DeleteDialog
import com.project.morestore.data.models.cart.CartItem
import com.project.morestore.repositories.CartRepository
import com.project.morestore.repositories.ProductRepository
import com.project.morestore.util.errorMessage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import javax.inject.Inject

class OrdersCartPresenter @Inject constructor(@ApplicationContext val context: Context,
                                              private val productRepository: ProductRepository,
                                              private val cartRepository: CartRepository) : MvpPresenter<OrdersCartView>() {

    private lateinit var adapter: CartAdapter


    fun loadCartData(
        userId: Long? = null,
        onDelete: (CartItem) -> Unit
    ) {

        presenterScope.launch {
            val response = cartRepository.getCartItems(
                userId = userId
            )
            when (response?.code()) {
                200 -> {
                    initContent(response.body(), onDelete)
                }
                404 -> initContent(emptyList(), onDelete)
                else -> {
                    viewState.error(errorMessage(response))
                }
            }
        }
    }

    private suspend fun initContent(items: List<CartItem>?, onDelete: (CartItem) -> Unit) {
        if (items != null) {

            adapter = CartAdapter(items.toMutableList(), {
                   presenterScope.launch {
                       val product =
                           productRepository.getProducts(productId = it.product.id)?.body()?.first()
                        if(product != null)
                           viewState.navigate(it.product, it.id)
                       else
                           viewState.error("Ошибка")
                   }
            }, {}, {
                val deleteDialog = DeleteDialog(
                    title = context.getString(R.string.cart_delete_title),
                    message = null,
                    context = context,
                    confirmCallback = {
                        onDelete(it)
                        adapter.remove(it)
                    }
                );

                viewState.showDeleteDialog(deleteDialog)
            }, {user ->  viewState.navigate(user) })
            viewState.initCart(adapter)
        }
    }
}