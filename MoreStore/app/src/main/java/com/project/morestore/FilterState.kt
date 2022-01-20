package com.project.morestore

import android.widget.CheckBox
import com.project.morestore.models.Region

object FilterState {
    var isAllCategories = true
    var categories = emptyList<Boolean>()

    var isAllBrands = true
    var segments = emptyList<Boolean>()
    var brands9 = emptyList<Boolean>()
    var brandsA = emptyList<Boolean>()
    var regions = emptyList<Region>()
}