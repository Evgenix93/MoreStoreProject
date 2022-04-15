package com.project.morestore.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

sealed class Message {
    class Divider(val text :String) :Message()
    class My(val time :String, @DrawableRes val status :Int, val message :String) :Message()
    class Companion(val msgs :List<Msg>) :Message()
    class MyMedia(val time :String, @DrawableRes val status :Int, val media :Array<Media>, val message: String? = null) :Message()
    sealed class Special :Message(){
        class DealRequest(val time :String) :Special()
        class BuyRequest(val time :String, @DrawableRes val statusIcon :Int, val text: String, val textColor: Int, @DrawableRes val submitStatus: Int) :Special()
        class DealAccept(val time :String) :Special()
        class DealDetails() :Special()
        class BuyDetails() :Special()
        class GeoDetails() :Special()
        class PriceRequest(val time :String, @DrawableRes val status :Int, val newPrice :String, val text: String, val textColor: Int, @DrawableRes val submitStatus: Int ) :Special()
        class PriceAccepted(val newPrice :String) :Special()
    }
}

class Msg(val time :String, val message :String)

sealed class Media {
    class Photo(val photoUri :String, val count :Int = 1) :Media()
    class Video(val videoUri :String, val count :Int = 1) :Media()
}