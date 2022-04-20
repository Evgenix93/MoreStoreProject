package com.project.morestore.singletones

import com.project.morestore.models.Address

object Token {
    var token = ""
    var userId: Long = 0
    var currentUserAddress: Address? = null
}