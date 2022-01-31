package com.project.morestore.singletones

import com.project.morestore.models.Filter
import com.project.morestore.models.MaterialLine
import com.project.morestore.models.Region
import com.project.morestore.models.SizeLine
import com.squareup.moshi.JsonClass


object FilterState {
   /* var chosenMaterials = listOf<MaterialLine>()
    var chosenConditions = listOf<Boolean>()
    var chosenForWho = listOf<Boolean>()
    var chosenSizes = listOf<SizeLine>()
    var chosenProductStatus = listOf<Boolean>()
    var chosenStyles = listOf<Boolean>()
    var isAllCategories = true
    var categories = emptyList<Boolean>()
    var isAllBrands = true
    var segments = emptyList<Boolean>()
    var brands9 = emptyList<Boolean>()
    var brandsA = emptyList<Boolean>()
    var regions = emptyList<Region>()*/

    var filter = Filter()
}