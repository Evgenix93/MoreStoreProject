package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Filter(
    var chosenMaterials: List<MaterialLine> = listOf<MaterialLine>(),
    var chosenConditions: List<Boolean> = listOf<Boolean>(),
    var chosenForWho: List<Boolean> = listOf<Boolean>(true, false, false),
    var chosenSizes: List<SizeLine> = listOf<SizeLine>(),
    var chosenProductStatus: List<Boolean> = listOf<Boolean>(),
    var chosenStyles: List<Boolean> = listOf<Boolean>(),
    var isAllCategories: Boolean = true,
    var categories: List<ProductCategory> = listOf(),
    var isAllBrands: Boolean = true,
    var segments: List<Boolean> = emptyList<Boolean>(),
    var brands9: List<Boolean> = emptyList<Boolean>(),
    var brandsA: List<Boolean> = emptyList<Boolean>(),
    var regions: List<Region> = emptyList<Region>(),
    var colors: List<Color> = emptyList()
)
