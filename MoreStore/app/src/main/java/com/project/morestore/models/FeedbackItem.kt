package com.project.morestore.models

import android.net.Uri

sealed class FeedbackItem {
    class AddPhoto(val isChat: Boolean = false) :FeedbackItem()
    class Description(val isChat: Boolean = false) :FeedbackItem()
    class Photo(val photo : Uri) :FeedbackItem()
}