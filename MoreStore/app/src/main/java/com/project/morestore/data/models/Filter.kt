package com.project.morestore.data.models

import com.project.morestore.util.NotificationType
import com.project.morestore.util.SortingType
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Filter(
    var name: String? = "",
    var chosenMaterials: List<MaterialLine> = listOf(),
    var chosenConditions: List<Boolean> = listOf(),
    var chosenForWho: List<Boolean> = listOf(true, false, false),
    var chosenTopSizesWomen: List<SizeLine> = listOf(),
    var chosenBottomSizesWomen: List<SizeLine> = listOf(),
    var chosenShoosSizesWomen: List<SizeLine> = listOf(),
    var chosenTopSizesMen: List<SizeLine> = listOf(),
    var chosenBottomSizesMen: List<SizeLine> = listOf(),
    var chosenShoosSizesMen: List<SizeLine> = listOf(),
    var chosenTopSizesKids: List<SizeLine> = listOf(),
    var chosenBottomSizesKids: List<SizeLine> = listOf(),
    var chosenShoosSizesKids: List<SizeLine> = listOf(),
    var chosenProductStatus: List<Boolean> = listOf(true, false, false),
    var chosenStyles: List<Property> = listOf(),
    var isAllCategories: Boolean = true,
    var categories: List<ProductCategory> = listOf(),
    var isAllBrands: Boolean = true,
    var segments: List<Boolean> = emptyList(),
    var brands: List<ProductBrand> = emptyList(),
    var regions: List<Region> = emptyList(),
    var colors: List<Property> = emptyList(),
    var currentLocation: Region? = null,
    var isCurrentLocationFirstLoaded: Boolean = false,
    var isCurrentLocationChosen: Boolean = false,
    var fromPrice: Int? = null,
    var untilPrice: Int? = null,
    var status: Int = 1,
    var sortingType: String = SortingType.NEW.value,
    var notificationType: String = NotificationType.DAILY.value
)
