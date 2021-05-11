package com.santaistiger.gomourdeliveryapp.data.model

/**
 * @param deliveryTime
 * 배달 완료 전 - 예상 도착 시간 / 배달 완료 후 - 도착시간
 */

data class Order(
    val orderId: String? = null,
    val customerUid: String? = null,
    val deliveryManUid: String? = null,
    val stores: List<Store>? = null,
    val deliveryCharge: Int? = null,
    val destination: Place? = null,
    val message: String? = null,
    val orderDate: Long? = null,
    var deliveryTime: Long? = null,
    var isCompleted: Boolean = false
)
