package com.project.morestore.models

import androidx.annotation.DrawableRes

class FeedbackProduct(
    @DrawableRes val photo :Int,
    val title :String,
    val brand :String,
    val status :String,
    val newPrice :Int,
    val oldPrice :Int
)