package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Filter(
    var chosenMaterials: List<MaterialLine> = listOf(),
    var chosenConditions: List<Boolean> = listOf(),
    var chosenForWho: List<Boolean> = listOf(true, false, false),
    var chosenTopSizes: List<SizeLine> = listOf(),
    var chosenBottomSizes: List<SizeLine> = listOf(),
    var chosenShoosSizes: List<SizeLine> = listOf(),
    var chosenProductStatus: List<Boolean> = listOf(true, true, false),
    var chosenStyles: List<Boolean> = listOf(),
    var isAllCategories: Boolean = true,
    var categories: List<ProductCategory> = listOf(),
    var isAllBrands: Boolean = true,
    var segments: List<Boolean> = emptyList(),
    var brands: List<ProductBrand> = emptyList(),
    var regions: List<Region> = emptyList(),
    var colors: List<Color> = emptyList(),
    var currentLocation: Region? = null,
    var isCurrentLocationFirstLoaded: Boolean = false,
    var isCurrentLocationChosen: Boolean = false
)
