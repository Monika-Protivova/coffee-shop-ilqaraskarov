package com.motycka.edu.order

interface OrderRepository {

    suspend fun selectAll(): List<OrderDTO>

    suspend fun selectById(id: OrderId): OrderDTO?

    suspend fun create(order: OrderDTO): OrderDTO

    suspend fun update(order: OrderDTO): OrderDTO

}

