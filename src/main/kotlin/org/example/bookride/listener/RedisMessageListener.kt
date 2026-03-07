package org.example.bookride.listener

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.example.bookride.dto.Location
import org.example.bookride.model.DriverStatus
import org.example.bookride.service.DriverFinderService
import org.example.bookride.service.DriverService
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Component
import java.util.UUID
import kotlin.collections.dropLastWhile
import kotlin.collections.toTypedArray
import kotlin.text.isEmpty
import kotlin.text.split
import kotlin.text.startsWith
import kotlin.text.toRegex

@Component("RedisMessageListener")
class RedisMessageListener(
    private val finderService: DriverFinderService,
    private val driverService: DriverService,
    private val serviceScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
) : MessageListener {
    override fun onMessage(
        message: Message,
        pattern: ByteArray?,
    ) {
        val expiredKey = message.toString() // e.g., "show_ticket_123_A1"

        if (expiredKey.startsWith("offer_timer:")) {
            // Remove prefix and split the remaining "123_A1"

            // Split "offer_timer:{ride_id}:{driver_id}:{latitude}:{longitude}" -> status into parts
            println("received expired key: $expiredKey")

            val parts: Array<String?> =
                expiredKey
                    .split(":".toRegex())
                    .dropLastWhile { it.isEmpty() }
                    .toTypedArray()

            println("received parts: $parts")

            if (parts.size == 5) {
                val rideId = UUID.fromString(parts[1])
                val driverId = UUID.fromString(parts[2]) // "A1,A2,A3"
                val latitude = parts[3]!!.toDouble()
                val longitude = parts[4]!!.toDouble()
                serviceScope.launch {
                    // add driver to black list or mark out of service
                    driverService.updateDriverStatus(driverId, DriverStatus.OFFER_PENDING, DriverStatus.AVAILABLE)
                    // initiate new search rider for driver
                    finderService.finderDriver(rideId, Location(latitude, longitude), setOf(driverId))
                }
            }
        }
    }
}
