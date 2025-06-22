package com.motycka.edu.order

import com.motycka.edu.security.getUserIdentity
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.orderRoutes(
    orderService: OrderService,
    basePath: String
) {
    route("$basePath/orders") {

        // GET /orders
        get {
            try {
                val orders = orderService.getAllOrders()
                call.respond(HttpStatusCode.OK, orders)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        // GET /orders/{id}
        get("{id}") {
            val idParam = call.parameters["id"]
            val id = idParam?.toLongOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID format"))
                return@get
            }

            try {
                val order = orderService.getOrderById(id)
                call.respond(HttpStatusCode.OK, order)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Order not found"))
            }
        }

        // POST /orders
        post {
            try {
                val userId = getUserIdentity().userId
                val request = call.receive<OrderRequest>()
                val createdOrder = orderService.createOrder(userId, request)
                call.respond(HttpStatusCode.Created, createdOrder)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }

        put("{id}") {
            val idParam = call.parameters["id"]
            val id = idParam?.toLongOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID format"))
                return@put
            }

            try {
                val updateRequest = call.receive<OrderUpdateRequest>()
                val updatedOrder = orderService.updateOrderStatus(id, updateRequest)
                call.respond(HttpStatusCode.OK, updatedOrder)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to "Order not found"))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
    }
}
