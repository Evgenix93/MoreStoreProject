package com.project.morestore.data.models.cart

enum class OrderStatus {
    DELIVERY,
    RECEIVED,
    RECEIVED_SELLER,
    AT_COURIER,
    MEETING_NOT_ACCEPTED,
    MEETING_NOT_ACCEPTED_SELLER,
    CHANGE_MEETING,
    CHANGE_MEETING_SELLER,
    MEETING_NOT_ACCEPTED_FROM_ME,
    CHANGE_MEETING_FROM_ME,
    NOT_SUBMITTED,
    DECLINED,
    RECEIVED_SUCCESSFULLY,
    DECLINED_BUYER,
    NOT_SUBMITTED_SELLER,
    ADD_MEETING,
    PAYED,
    NOT_PAYED,
    NOT_PAYED_SELLER,
    CREATE_DELIVERY,
    DELIVERY_STATUS_NOT_VALID,
    DELIVERY_STATUS_ACCEPTED,
    DELIVERY_STATUS_NOT_DEFINED
}