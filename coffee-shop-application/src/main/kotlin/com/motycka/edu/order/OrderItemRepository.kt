package com.motycka.edu.order

interface OrderItemRepository {
    suspend fun selectByOrderId(orderId: OrderId): List<OrderItemDTO>
    suspend fun createOrderItems(orderItems: List<OrderItemDTO>)
}
