package com.project.morestore.presentation.adapters.cart

import com.project.morestore.data.models.cart.OrderItem

interface OrderClickListener {
    fun acceptMeeting(orderItem: OrderItem)
    fun declineMeeting(orderItem: OrderItem)

    fun acceptOrder(orderItem: OrderItem)
    fun reportProblem(orderItem: OrderItem)
    fun payForOrder(orderItem: OrderItem)
}