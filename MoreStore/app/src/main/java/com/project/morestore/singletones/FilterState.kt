package com.project.morestore.singletones

import com.project.morestore.data.models.Filter


object FilterState {

    var isLoadedFilter = false
    var filter = Filter()
    var filterTemp = Filter()
}