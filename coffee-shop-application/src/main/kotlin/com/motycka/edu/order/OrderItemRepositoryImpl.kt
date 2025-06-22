package com.motycka.edu.order

import com.motycka.edu.config.suspendTransaction

class OrderItemRepositoryImpl : OrderItemRepository {

    override suspend fun selectByOrderId(orderId: OrderId): List<OrderItemDTO> = suspendTransaction {
        OrderItemDAO.find { OrderItemTable.orderId eq orderId }
            .map { it.toDTO() }
    }

    override suspend fun createOrderItems(orderItems: List<OrderItemDTO>) = suspendTransaction {
        orderItems.forEach { dto ->
            OrderItemDAO.new {
                this.orderId = dto.orderId
                this.menuItemId = dto.menuItemId
                this.quantity = dto.quantity
            }
        }
    }
}
