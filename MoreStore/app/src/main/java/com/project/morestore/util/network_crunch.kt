package com.project.morestore.util

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class FalseNull<T>(val item :T?){
    constructor(falseItem :Boolean) :this(null)
}