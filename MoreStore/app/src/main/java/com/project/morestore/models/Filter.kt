package com.project.morestore.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Filter(
    var chosenMaterials: List<MaterialLine> = listOf<MaterialLine>(),
    var chosenConditions: List<Boolean> = listOf<Boolean>(),
    var chosenForWho: List<Boolean> = listOf<Boolean>(true, false, false),
    var chosenTopSizes: List<SizeLine> = listOf<SizeLine>(),
    var chosenBottomSizes: List<SizeLine> = listOf(),
    var chosenShoosSizes: List<SizeLine> = listOf(),

    var chosenProductStatus: List<Boolean> = listOf<Boolean>(),
    var chosenStyles: List<Boolean> = listOf<Boolean>(),
    var isAllCategories: Boolean = true,
    var categories: List<ProductCategory> = listOf(),
    var isAllBrands: Boolean = true,
    var segments: List<Boolean> = emptyList<Boolean>(),
    var brands: List<ProductBrand> = emptyList<ProductBrand>(),
    var regions: List<Region> = emptyList<Region>(),
    var colors: List<Color> = emptyList(),
    var currentLocation: Region? = null,
    var isCurrentLocationFirstLoaded: Boolean = false
)
