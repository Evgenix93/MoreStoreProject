package com.project.morestore.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

sealed class Message {
    class Divider(@StringRes val stringId :Int) :Message()
    class My(val time :String, @DrawableRes val status :Int, val message :String) :Message()
    class Companion(val msgs :List<Msg>) :Message()
    class MyMedia(val time :String, @DrawableRes val status :Int, val media :Array<Media>) :Message()
    sealed class Special :Message(){
        class DealRequest(val time :String) :Special()
        class BuyRequest(val time :String, @DrawableRes val statusIcon :Int) :Special()
        class DealAccept(val time :String) :Special()
        class DealDetails() :Special()
        class BuyDetails() :Special()
        class GeoDetails() :Special()
        class PriceRequest(val time :String, @DrawableRes val status :Int, val newPrice :String) :Special()
        class PriceAccepted(val newPrice :String) :Special()
    }
}

class Msg(val time :String, val message :String)

sealed class Media {
    class Photo(@DrawableRes val photoId :Int, val count :Int = 1) :Media()
    class Video(@DrawableRes val videoId :Int) :Media()
}