package com.project.morestore.models

class Search(
    val id: Long,
    val title :String,
    val filters :Array<String>,
    val notification :Notification,
){
    enum class Notification { DISABLE, WEEKLY, DAYLY }
}