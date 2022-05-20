package com.project.morestore.adapters.cart

import com.project.morestore.models.User
import com.project.morestore.models.cart.OrderItem

interface OrderClickListener {
    fun acceptMeeting(orderItem: OrderItem)
    fun declineMeeting(orderItem: OrderItem)

    fun acceptOrder(orderItem: OrderItem)
    fun reportProblem(orderItem: OrderItem)
}