package com.motycka.edu.order

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable

object OrderTable : LongIdTable("orders") {
    val customerId = long("customer_id")
    val status = varchar("status", 50) // Will store enum name like "PENDING"
}

class OrderDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<OrderDAO>(OrderTable)

    var customerId by OrderTable.customerId
    var status by OrderTable.status

    fun toDTO(): OrderDTO {
        return OrderDTO(
            id = this.id.value,
            customerId = this.customerId,
            status = OrderStatus.valueOf(this.status)
        )
    }
}
