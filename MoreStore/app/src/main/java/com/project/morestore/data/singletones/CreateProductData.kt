package com.project.morestore.data.singletones

import com.project.morestore.data.models.CreateProductData
import java.io.File

object CreateProductData {
   var createProductData = CreateProductData()
   val productPhotosMap = mutableMapOf<Int, File>()
}