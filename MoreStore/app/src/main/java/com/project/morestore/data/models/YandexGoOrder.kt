package com.project.morestore.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class YandexGoOrder(
    @Json(name = "id_order")
    val idOrder: Long,
    val comment: String,
    @Json(name = "emergency_contact_name")
    val emergencyContactName: String,
    @Json(name = "emergency_contact_phone")
    val emergencyContactPhone: String,
    @Json(name = "items_quantity")
    val itemQuantity: String,
   @Json(name = "items_product_name")
    val itemsProductName: String,
   @Json(name = "product_price")
    val productPrice: String,
   @Json(name = "route_points_address_coordinates")
    val pointAddressCoordinates: List<Double>,
   @Json(name = "route_points_address_fullname")
    val pointFullName: String,
   @Json(name = "route_points_contact_email")
    val pointContactEmail: String,
   @Json(name = "route_points_contact_name")
    val pointContactName: String,
   @Json(name = "route_points_contact_phone")
    val pointContactPhone: String,
    @Json(name = "take_route_points_address_coordinates")
    val takePointCoordinates: List<Double>,
    @Json(name = "take_route_points_address_fullname")
    val takePointFullName: String,
    @Json(name = "take_route_points_contact_email")
    val takePointContactEmail: String,
    @Json(name = "take_route_points_contact_name")
    val takePointContactName: String,
    @Json(name = "take_route_points_contact_phone")
    val takePointContactPhone: String,
    @Json(name = "item_weight")
    val itemWeight: Float
)

@JsonClass(generateAdapter = true)
data class YandexClaimId(
    @Json(name = "claim_id")
    val claimId: String,
    val version: Int = 1
)

@JsonClass(generateAdapter = true)
data class YandexCancelClaimId(
    @Json(name = "claim_id")
    val claimId: String,
    @Json(name = "cancel_state")
    val cancelState: String = "free",
    val version: Int = 1
)

object YandexDeliveryStatus{
     val statuses = mapOf(
         "new" to "Новая заявка",
    "estimating" to	"Идет процесс оценки заявки.",
    "estimating_failed" to "Не удалось оценить заявку.",
    "ready_for_approval" to	"Заявка успешно оценена и ожидает подтверждения от клиента.",
    "accepted" to "Заявка подтверждена клиентом.",
    "performer_lookup" to	"Заявка взята в обработку.",
    "performer_draft" to	"Идет поиск водителя.",
    "performer_found" to	"Водитель найден и едет в точку А.",
    "performer_not_found" to	"Не удалось найти водителя.",
    "pickup_arrived" to	"Водитель приехал в точку А.",
    "ready_for_pickup_confirmation" to	"Водитель ждет, когда отправитель назовет ему код подтверждения.",
    "pickuped" to 	"Водитель успешно забрал груз.",
    "pay_waiting" to	"Заказ ожидает оплаты.",
    "delivery_arrived" to 	"Водитель приехал в точку Б.",
    "ready_for_delivery_confirmation" to	"Водитель ждет, когда получатель назовет ему код подтверждения.",
    "delivered" to	"Водитель успешно доставил груз.",
    "delivered_finish" to	"Заказ завершен.",
    "returning" to	"Водителю пришлось вернуть груз.",
    "return_arrived" to	"Водитель приехал в точку возврата.",
    "ready_for_return_confirmation" to	"Водитель в точке возврата ожидает, когда ему назовут код подтверждения.",
    "returned" to	"Водитель успешно вернул груз.",
    "returned_finish" to	"Заказ завершен.",
    "cancelled" to	"Заказ был отменен клиентом бесплатно.",
    "cancelled_with_payment" to	"Заказ был отменен клиентом платно (водитель уже приехал).",
    "cancelled_by_taxi" to	"Водитель отменил заказ (до получения груза).",
    "cancelled_with_items_on_hands" to	"Клиент платно отменил заявку без необходимости возврата груза",
    "failed" to	"При выполнение заказа произошла ошибка")
}

object PositiveYandexKeys{
    val keys = listOf(
                "new",
                "estimating",
                "ready_for_approval",
                "accepted",
                "performer_lookup",
                "performer_draft",
                "performer_found",
                "pickup_arrived",
                "ready_for_pickup_confirmation",
                "pickuped",
                "delivery_arrived",
                "ready_for_delivery_confirmation",
                "pay_waiting",
                "delivered",
                "delivered_finish",
                "returning",
                "return_arrived",
                "ready_for_return_confirmation",
                "returned",
                "returned_finish",


    )
}

object NegativeYandexKeys{
    val keys = listOf(
        "estimating_failed",
        "performer_not_found",
        "failed",
        "cancelled",
        "cancelled_with_payment",
        "cancelled_by_taxi",
        "cancelled_with_items_on_hands"
    )
}

data class YandexStatusKeys(
    val key: String,
    val isPositive: Boolean
)
