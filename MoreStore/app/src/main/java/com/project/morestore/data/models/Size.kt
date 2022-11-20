package com.project.morestore.data.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Size(
    val id: Int,
    val name: String,
    val id_category: Int?,
    var chosen: Boolean?,
    var fr: String? = null,
    var us: String? = null,
    var uk: String? = null,
    var w: String? = null
){

    fun toInt(): Int{
        var counter = 0
        var coff = 1
        for(char in name){
            when(char) {
                'X' -> {
                    coff++
                }
                'S' -> {
                    counter = -1
                }
                'M' -> {
                    counter = 1
                }
                'L' -> {
                    counter = 2
                }
            }

        }
        return counter * coff
    }


}
