package com.project.morestore.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

sealed class Message {
    class Divider(@StringRes val stringId :Int) :Message()
    class My(val time :String, @DrawableRes val status :Int, val message :String) :Message()
    class Companion(val msgs :List<Msg>) :Message()
    class CompanionMedia(val time :String, @DrawableRes val status :Int, val media :Array<Media>, val message: String? = null): Message()
    class MyMedia(val time :String, @DrawableRes val status :Int, val media :Array<Media>, val message: String? = null) :Message()
    sealed class Special :Message(){
        class DealRequest(val time :String, val price: Int, val suggestId: Long) :Special()
        class BuyRequest(val time :String, @DrawableRes val statusIcon :Int) :Special()
        class DealAccept(val time :String): Special()
        class DealCancel(val time: String): Special()
        object DealDetails : Special()
        object BuyDetails : Special()
        object GeoDetails : Special()
        class PriceRequest(val time :String, @DrawableRes val status :Int, val newPrice :String) :Special()
        class PriceSubmit(val time: String, val newPrice: String, val suggestId: Long): Special()
        class PriceSubmitted(val time: String, val newPrice: String): Special()
        class PriceCanceled(val time: String, val newPrice: String): Special()
        class PriceAccepted(val newPrice :String) :Special()
    }
}

class Msg(val time :String, val message :String)

sealed class Media {
    class Photo(val photoUri :String, val count :Int = 1) :Media()
    class Video(val videoUri :String, val count :Int = 1) :Media()
}