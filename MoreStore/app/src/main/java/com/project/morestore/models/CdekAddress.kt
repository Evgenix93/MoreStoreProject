package com.project.morestore.models

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
class CdekAddress(val code: String, val location :Location): Parcelable {

    @Parcelize
    @JsonClass(generateAdapter = true)
    class Location(

        val city :String?,
        @Json(name = "postal_code")
        val index :String?,

        @Json(name = "latitude")
        val lat :Double,
        @Json(name = "longitude")
        val lon :Double,
        val address :String
    ): Parcelable

    /*
    {
        "code": "STZH2",
        "name": "3-й Микрорайон",
        "nearest_station": "Дом Природы",
        "work_time": "Пн-Пт 10:00-19:00, Сб 10:00-16:00",
        "phones": [
            {
                "number": "+79131131255"
            },
            {
                "number": "+79131131255"
            }
        ],
        "email": "STRJ@CDEK.RU",
        "type": "PVZ",
        "owner_code": "cdek",
        "take_only": false,
        "is_handout": true,
        "is_dressing_room": true,
        "have_cashless": true,
        "have_cash": true,
        "allowed_cod": true,
        "office_image_list": [
            {
                "url": "edu.api-pvz.imageRepository.service.cdek.tech:8008/images/2413/2784_1_STZH2"
            }
        ],
        "work_time_list": [
            {
                "day": 1,
                "time": "10:00/19:00"
            },
            {
                "day": 2,
                "time": "10:00/19:00"
            },
            {
                "day": 3,
                "time": "10:00/19:00"
            },
            {
                "day": 4,
                "time": "10:00/19:00"
            },
            {
                "day": 5,
                "time": "10:00/19:00"
            },
            {
                "day": 6,
                "time": "10:00/16:00"
            }
        ],
        "location": {
            "country_code": "RU",
            "region_code": 53,
            "region": "Томская обл.",
            "city_code": 515,
            "city": "Стрежевой",
            "postal_code": "636782",
            "longitude": 77.6148,
            "latitude": 60.731537,
            "address": "3-й Микрорайон, 313, 1",
            "address_full": "Россия, Томская обл., Стрежевой, 3-й Микрорайон, 313, 1"
        },
        "fulfillment": false
    },
     */
}