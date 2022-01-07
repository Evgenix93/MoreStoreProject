package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Size(
    val id: Int,
    val name: String,
    val id_category: Int,
    var chosen: Boolean?
){

    fun toInt(): Int{
        var counter = 0
        var coff = 1
        for(char in name){
            if(char == 'X'){
                coff++
            }
            if(char == 'S'){
                counter = -1
            }
            if(char == 'M'){
                counter = 1
            }
            if(char == 'L'){
                counter = 2
            }

        }
        return counter * coff
    }


}
