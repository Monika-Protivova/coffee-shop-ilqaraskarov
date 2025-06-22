package com.motycka.edu.order

import kotlinx.serialization.Serializable

@Serializable
data class OrderResponse(
    val id: OrderId?,
    val menuItems: List<OrderItemResponse>,
    val totalPrice: Double,
    val status: OrderStatus
)
