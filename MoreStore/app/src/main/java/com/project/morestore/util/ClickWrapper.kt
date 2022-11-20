package com.project.morestore.util

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClickWrapper(val onClick: () -> Unit): Parcelable


