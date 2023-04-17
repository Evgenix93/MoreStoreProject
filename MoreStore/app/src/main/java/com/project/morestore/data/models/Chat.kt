package com.project.morestore.data.models

sealed class Chat(
    val id: Long,
    val name :String,
    val description :String,
    val isUnread: Boolean,
    open val avatar: String?
    //open val avatar :Int?,
){
    class Support(
        id: Long,
        name :String,
        desc :String,
        isUnread: Boolean = false
    ) :Chat(id, name, desc, isUnread, null)

    class Deal(
        id: Long,
        product :String,
        seller :String,
        isUnread: Boolean = false,
        override val avatar :String,
        val price :Float = 0f
    ) :Chat(id, product, seller, isUnread, avatar)

    class Lot(
        id: Long,
        product :String,
        countBuyers :String,
        isUnread: Boolean = false,
        override val avatar :String,
        val price :Float,
        val productId: Long,
        val totalUnread :Int = 0,
    ) :Chat(id, product, countBuyers, isUnread, avatar)

    class Personal(
        id: Long,
        name :String,
        lastMessage :String,
        isUnread: Boolean = false,
        override val avatar :String,
        val price :Float,
        val totalUnread :Int = 0,
        val online :Boolean = false
    ) :Chat(id, name, lastMessage, isUnread, avatar)
}