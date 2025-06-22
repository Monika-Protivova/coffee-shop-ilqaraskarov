package com.motycka.edu.order

import com.motycka.edu.customer.CustomerRepository
import com.motycka.edu.menu.MenuItemDTO
import com.motycka.edu.menu.MenuRepository
import com.motycka.edu.security.getUserIdentity

class OrderService(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val menuRepository: MenuRepository,
    private val customerRepository: CustomerRepository
) {

    suspend fun createOrder(userId: Long, request: OrderRequest): OrderResponse {
        val customer = customerRepository.selectCustomer(userId)
            ?: throw IllegalArgumentException("Customer not found")

        val menuItems = menuRepository.selectMenuItems(
            ids = request.items.map { it.menuItemId }.toSet()
        )

        val orderItems = request.items.map {
            OrderItemDTO(
                id = null,
                orderId = 0L, // temporary, will be replaced after creating order
                menuItemId = it.menuItemId,
                quantity = it.quantity
            )
        }

        val totalPrice = PriceCalculator.calculatePrice(
            menuItems = menuItems.toList(),
            discountInPercent = customer.discountPercent,
            orderItems = orderItems
        )

        val orderDTO = orderRepository.create(
            OrderDTO(id = null, customerId = customer.id, status = OrderStatus.PENDING)
        )

        val finalOrderItems = orderItems.map {
            it.copy(orderId = orderDTO.id!!)
        }

        orderItemRepository.createOrderItems(finalOrderItems)

        val itemResponses = finalOrderItems.map { item ->
            val menuItem = menuItems.find { it.id == item.menuItemId }
                ?: throw IllegalStateException("Menu item not found: ${item.menuItemId}")
            OrderItemResponse(menuItem = menuItem.toResponse(), quantity = item.quantity)
        }

        return OrderResponse(
            id = orderDTO.id!!,
            menuItems = itemResponses,
            totalPrice = totalPrice,
            status = orderDTO.status
        )
    }

    suspend fun getAllOrders(): List<OrderResponse> {
        val orders = orderRepository.selectAll()
        return orders.map { getOrderById(it.id!!) }
    }

    suspend fun getOrderById(orderId: OrderId): OrderResponse {
        val order = orderRepository.selectById(orderId)
            ?: throw IllegalArgumentException("Order not found")

        val orderItems = orderItemRepository.selectByOrderId(orderId)

        val menuItems = menuRepository.selectMenuItems(
            ids = orderItems.map { it.menuItemId }.toSet()
        )

        val itemResponses = orderItems.map { item ->
            val menuItem = menuItems.find { it.id == item.menuItemId }
                ?: throw IllegalStateException("Menu item not found: ${item.menuItemId}")
            OrderItemResponse(menuItem = menuItem.toResponse(), quantity = item.quantity)
        }

        val totalPrice = PriceCalculator.calculatePrice(
            menuItems = menuItems.toList(),
            discountInPercent = 0.0, // optional: use real customer discount if needed here
            orderItems = orderItems
        )

        return OrderResponse(
            id = order.id!!,
            menuItems = itemResponses,
            totalPrice = totalPrice,
            status = order.status
        )
    }

    suspend fun updateOrderStatus(orderId: OrderId, updateRequest: OrderUpdateRequest): OrderResponse {
        val existing = orderRepository.selectById(orderId)
            ?: throw IllegalArgumentException("Order not found")

        val updated = orderRepository.update(existing.copy(status = updateRequest.status))
        return getOrderById(updated.id!!)
    }
}
