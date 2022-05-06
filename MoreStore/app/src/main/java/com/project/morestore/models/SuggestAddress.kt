package com.project.morestore.models

class SuggestAddress(
    val country :String,
    val city :String,
    val street :String,
    val index :String,
    val house :String,
//    val address :String,
    val distance :String,
    val region :String
){
    val address :String get() = arrayOf(country, city, street, house).joinToString()

    /*
        val country = parts.firstOrNull{ it.kinds.contains(COUNTRY) }?.name
        val city = parts.firstOrNull { it.kinds.contains(LOCALITY) }?.name
        val region = parts.firstOrNull { it.kinds.contains(REGION) }?.name
     */
}