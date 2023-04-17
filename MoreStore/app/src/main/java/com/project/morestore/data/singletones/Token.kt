package com.project.morestore.data.singletones

import com.project.morestore.data.models.Address

object Token {
    var token = ""
    var userId: Long = 0
    var currentUserAddress: Address? = null
}