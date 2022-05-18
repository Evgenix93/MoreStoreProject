package com.project.morestore.models.cart

enum class OrderStatus {
    DELIVERY,
    RECEIVED,
    AT_COURIER,
    MEETING_NOT_ACCEPTED,
    CHANGE_MEETING,
    MEETING_NOT_ACCEPTED_FROM_ME,
    CHANGE_MEETING_FROM_ME,
    NOT_SUBMITTED,
    DECLINED
}