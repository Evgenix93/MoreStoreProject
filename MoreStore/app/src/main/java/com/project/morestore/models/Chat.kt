package com.project.morestore.models

sealed class Chat(
    val name :String,
    val description :String,
    open val avatar :Int?
){
    class Support(
        name :String,
        desc :String
    ) :Chat(name, desc, null)

    class Deal(
        product :String,
        seller :String,
        override val avatar :Int,
        val price :Float = 0f
    ) :Chat(product, seller, avatar)

    class Lot(
        product :String,
        countBuyers :String,
        override val avatar :Int,
        val price :Float,
        val totalUnread :Int = 0
    ) :Chat(product, countBuyers, avatar)

    class Personal(
        name :String,
        lastMessage :String,
        override val avatar :Int,
        val price :Float,
        val totalUnread :Int = 0,
        val online :Boolean = false
    ) :Chat(name, lastMessage, avatar)
}