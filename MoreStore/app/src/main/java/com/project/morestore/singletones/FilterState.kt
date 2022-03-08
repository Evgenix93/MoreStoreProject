package com.project.morestore.singletones

import com.project.morestore.models.Filter
import com.project.morestore.models.MaterialLine
import com.project.morestore.models.Region
import com.project.morestore.models.SizeLine
import com.squareup.moshi.JsonClass


object FilterState {

    var isLoadedFilter = false
    var filter = Filter()
}