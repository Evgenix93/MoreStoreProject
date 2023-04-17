package com.project.morestore.data.models.slidermenu

data class SliderMenu<T>(
    val icon: Int?,
    val content: String,
    var itemsCount: UInt,
    var isSelected: Boolean,
    val type: T
)