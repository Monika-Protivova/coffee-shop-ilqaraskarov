package com.motycka.edu.order

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Op

@Serializable
data class OrderUpdateRequest(
    val status: OrderStatus,
)
