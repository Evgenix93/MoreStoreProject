package com.project.morestore.singletones

import com.project.morestore.models.CreateProductData
import java.io.File

object CreateProductData {
   /* var idCategory: Int? = null
    var idBrand: Long? = null
    var address: String? = null
    var date: Long? = null
    var dateEnd: Long? = null
    var status: Int? = null
    var price: String? = null
    var sale: Int? = null
    var name: String? = null
    var about: String? = null
    var phone: String? = null
    var property: MutableList<Property2>? = null*/
   var createProductData = CreateProductData()
   val productPhotosMap = mutableMapOf<Int, File>()
}