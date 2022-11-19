package com.project.morestore.adapters.cart

import android.graphics.Color
import android.graphics.Paint
import android.util.Range
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.project.morestore.R
import com.project.morestore.databinding.ItemCartProductBinding
import com.project.morestore.models.User
import com.project.morestore.models.cart.CartItem
import com.project.morestore.util.getDrawableRes

class CartAdapter(
    private val items: MutableList<CartItem>,
    private val onPurchaseClickListener: (CartItem) -> Unit,
    private val onDeliveryClickListener: (CartItem) -> Unit,
    private val onDeleteClickListener: (CartItem) -> Unit,
    private val onProfileClickListener: (User) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartItemHolder>() {

    inner class CartItemHolder(menuItem: View) : RecyclerView.ViewHolder(menuItem) {
        val binding: ItemCartProductBinding by viewBinding()

        fun bind(cartItem: CartItem) {
            with(binding) {
                Glide.with(itemView.context)
                    .load(cartItem.product.user?.avatar?.photo.toString())
                    .into(cartProductUserIcon)
                cartProductUserName.text = cartItem.product.user?.name
                Glide.with(itemView.context)
                    .load(cartItem.product.photo[0].photo)
                    .into(cartProductPreview)
                cartProductName.text = cartItem.product.name
                cartProductCondition.text =
                    cartItem.product.property?.find { 11 == it.id.toInt() }?.value
                cartProductSizeText.visibility = View.GONE
                cartProductSizeSymbol.text = cartItem.product.property?.find {
                    it.id.toInt() in 1..9
                }?.value

                val color = cartItem.product.property?.filter { it.name == COLOR_PROPERTY }?.firstOrNull()
                if (color == null) {
                    cartProductColorName.visibility = View.INVISIBLE
                    cartProductColorDot.background =
                        itemView.resources.getDrawableRes(R.drawable.color2)
                } else {
                    cartProductColorName.text = color.value
                    if(color.ico != null) cartProductColorDot.background.setTint(Color.parseColor(color.ico))
                    else {
                        cartProductColorDot.background =
                            itemView.resources.getDrawableRes(R.drawable.color2)
                        cartProductColorDot.backgroundTintList = null
                    }
                }
                if (cartItem.product.priceNew != null) {
                    cartProductCostDiscount.apply {
                        paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        text = "${cartItem.product.price} ₽"
                    }
                    cartProductCostFinal.text = "${cartItem.product.priceNew} ₽"
                } else {
                    cartProductCostDiscount.visibility = View.INVISIBLE
                    cartProductCostFinal.text = "${cartItem.product.price} ₽"
                }
                //cartProductDeliveryCost.text = "${cartItem.product.deliveryPrice} ₽"

                cartProductUserIcon.setOnClickListener {
                    onProfileClickListener(cartItem.product.user!!)

                }
                cartProductUserName.setOnClickListener {
                    onProfileClickListener(cartItem.product.user!!)
                }
                val isOrderNull = cartItem.product.statusUser?.order == null
                val isBuyStatusTwo = cartItem.product.statusUser?.buy?.status == 2
                cartProductPurchaseButton.isVisible =    isOrderNull || isBuyStatusTwo
                orderItemDeliveryChangeIcon.isVisible =  !isOrderNull && !isBuyStatusTwo
                orderItemDeliveryChangeTitle.isVisible = !isOrderNull && !isBuyStatusTwo
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    //                      Recycler impl
    ///////////////////////////////////////////////////////////////////////////

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemHolder {
        val binding = ItemCartProductBinding.inflate(LayoutInflater.from(parent.context)).root
        return CartItemHolder(binding)
    }

    override fun onBindViewHolder(holder: CartItemHolder, position: Int) {
        val item = items[position];
        holder.bind(item)
        holder.binding.cartProductPurchaseButton.setOnClickListener() {
            onPurchaseClickListener(item)
        }
        holder.binding.cartProductDeliveryAdditional.setOnClickListener() {
            onDeliveryClickListener(item)
        }
        holder.binding.cartProductUserDelete.setOnClickListener {
            onDeleteClickListener(item)
        }
    }

    override fun getItemCount() = items.size

    ///////////////////////////////////////////////////////////////////////////
    //                          public
    ///////////////////////////////////////////////////////////////////////////

    fun remove(item: CartItem) {
        val index = items.indexOf(item)
        items.removeAt(index)
        notifyItemRemoved(index)
    }

    companion object {
        const val COLOR_PROPERTY = "Цвет"
    }
}