package com.motycka.edu.order

import com.motycka.edu.config.suspendTransaction

class OrderRepositoryImpl : OrderRepository {

    override suspend fun selectAll(): List<OrderDTO> = suspendTransaction {
        OrderDAO.all().map { it.toDTO() }
    }

    override suspend fun selectById(id: OrderId): OrderDTO? = suspendTransaction {
        OrderDAO.findById(id)?.toDTO()
    }

    override suspend fun create(order: OrderDTO): OrderDTO = suspendTransaction {
        val newOrder = OrderDAO.new {
            customerId = order.customerId
            status = order.status.name
        }
        newOrder.toDTO()
    }

    override suspend fun update(order: OrderDTO): OrderDTO = suspendTransaction {
        val existingOrder = OrderDAO.findById(order.id!!)
            ?: throw IllegalArgumentException("Order not found with ID: ${order.id}")
        existingOrder.customerId = order.customerId
        existingOrder.status = order.status.name
        existingOrder.toDTO()
    }
}
