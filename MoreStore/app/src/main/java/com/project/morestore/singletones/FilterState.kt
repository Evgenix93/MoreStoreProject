package com.project.morestore.singletones

import com.project.morestore.models.MaterialLine
import com.project.morestore.models.SizeLine

object FilterState {
    var chosenMaterials = listOf<MaterialLine>()
    var chosenConditions = listOf<Boolean>()
    var chosenForWho = listOf<Boolean>()
    var chosenSizes = listOf<SizeLine>()
    var chosenProductStatus = listOf<Boolean>()
    var chosenStyles = listOf<Boolean>()
}