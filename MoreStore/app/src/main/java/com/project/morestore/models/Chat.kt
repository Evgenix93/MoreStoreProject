package com.project.morestore.models

import java.security.acl.LastOwnerException

sealed class Chat(
    val id: Long,
    val name :String,
    val description :String,
    open val avatar :Int?
){
    class Support(
        id: Long,
        name :String,
        desc :String
    ) :Chat(id, name, desc, null)

    class Deal(
        id: Long,
        product :String,
        seller :String,
        override val avatar :Int,
        val price :Float = 0f
    ) :Chat(id, product, seller, avatar)

    class Lot(
        id: Long,
        product :String,
        countBuyers :String,
        override val avatar :Int,
        val price :Float,
        val totalUnread :Int = 0
    ) :Chat(id, product, countBuyers, avatar)

    class Personal(
        id: Long,
        name :String,
        lastMessage :String,
        override val avatar :Int,
        val price :Float,
        val totalUnread :Int = 0,
        val online :Boolean = false
    ) :Chat(id, name, lastMessage, avatar)
}