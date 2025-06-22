package com.motycka.edu.order

import com.motycka.edu.menu.MenuItemDTO

object PriceCalculator {

    fun calculatePrice(
        menuItems: List<MenuItemDTO>,
        discountInPercent: Double,
        orderItems: List<OrderItemDTO> = emptyList()
    ): Double {
        val total = orderItems.sumOf { item ->
            val menuItem = menuItems.find { it.id == item.menuItemId }
                ?: throw IllegalArgumentException("MenuItem with ID ${item.menuItemId} not found")
            menuItem.price * item.quantity
        }

        return "%.2f".format(total * (1 - discountInPercent / 100)).toDouble()
    }
}
