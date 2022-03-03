package com.project.morestore.models

import androidx.annotation.DrawableRes

sealed class FeedbackItem {
    object AddPhoto :FeedbackItem()
    object Description :FeedbackItem()
    class Photo(@DrawableRes val photo :Int) :FeedbackItem()
}