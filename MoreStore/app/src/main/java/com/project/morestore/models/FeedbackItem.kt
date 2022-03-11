package com.project.morestore.models

import android.net.Uri

sealed class FeedbackItem {
    object AddPhoto :FeedbackItem()
    object Description :FeedbackItem()
    class Photo(val photo : Uri) :FeedbackItem()
}