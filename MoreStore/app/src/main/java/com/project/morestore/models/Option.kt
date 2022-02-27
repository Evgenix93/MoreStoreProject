package com.project.morestore.models

data class Option(
    val name: String,
    var isChecked: Boolean,
    var address: String? = null
)
