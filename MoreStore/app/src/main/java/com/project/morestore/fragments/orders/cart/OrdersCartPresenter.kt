package com.project.morestore.fragments.orders.cart

import android.content.Context
import android.util.Log
import com.project.morestore.R
import com.project.morestore.adapters.cart.CartAdapter
import com.project.morestore.dialogs.DeleteDialog
import com.project.morestore.models.cart.CartItem
import com.project.morestore.repositories.OrdersRepository
import com.project.morestore.repositories.ProductRepository
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.presenterScope
import okhttp3.internal.toImmutableList

class OrdersCartPresenter(val context: Context) : MvpPresenter<OrdersCartView>() {

    private lateinit var adapter: CartAdapter
    private val ordersRepository = OrdersRepository(context)
    private val productRepository = ProductRepository(context)

    ///////////////////////////////////////////////////////////////////////////
    //                      private
    ///////////////////////////////////////////////////////////////////////////

    fun loadCartData(
        userId: Long? = null,
        onDelete: (CartItem) -> Unit
    ) {
        Log.d("mylog", "addProductToCart")
        presenterScope.launch {
            val response = ordersRepository.getCartItems(
                userId = userId
            )

            when (response?.code()) {
                200 -> {
                    initContent(response.body(), onDelete)
                }
                500 -> viewState.error("500 Internal Server Error")
                null -> viewState.error("нет интернета")
                404 -> initContent(emptyList(), onDelete)
            }
        }
    }

    private suspend fun initContent(items: List<CartItem>?, onDelete: (CartItem) -> Unit) {
        if (items != null) {

            adapter = CartAdapter(items.toMutableList(), {
                   presenterScope.launch {
                       val product =
                           productRepository.getProducts(productId = it.product.id)?.body()?.first()
                       if (product?.statusUser?.buy?.status == 0 || product?.statusUser?.buy?.status == 1)
                           viewState.error("Товар уже забронирован")
                       else if(product != null)
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