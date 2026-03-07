package org.example.bookride.service

import org.example.bookride.dto.RideStatusDTO
import org.example.bookride.model.RideStatus
import org.example.bookride.repository.RideRepository
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.util.UUID

@Service
class RideStatusService(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, Any>,
    private val rideRepository: RideRepository, // Your Postgres Repo
) {
    fun getStatusStream(rideId: UUID): Flux<RideStatusDTO> {
        val channel = "ride_updates:$rideId"

        // 1. Fetch Initial State (Current Status)
        val initialState =
            Flux.defer {
                val status = fetchCurrentStatusFromCacheOrDb(rideId)
                if (status != null) Flux.just(status) else Flux.empty()
            }

        // 2. Subscribe to Live Redis Pub/Sub Updates
        val liveUpdates =
            reactiveRedisTemplate
                .listenToChannel(channel)
                .map { message ->
                    // The message.message is your RideStatusDTO
                    message.message as RideStatusDTO
                }

        // 3. Merge them: Send current status immediately, then follow with live updates
        return Flux
            .concat(initialState, liveUpdates)
            .distinctUntilChanged { it.status } // Don't send "MATCHING" twice
    }

    private fun fetchCurrentStatusFromCacheOrDb(rideId: UUID): RideStatusDTO? {
        // Check Redis first for speed, then DB
        val cachedStatus = redisTemplate.opsForValue().get("ride:$rideId:status") as? String
        if (cachedStatus == RideStatus.DRIVER_ASSIGNED.name) {
            // Fetch full driver details from DB if already matched
            val ride = rideRepository.findById(rideId).get()
            return RideStatusDTO(
                ride.id,
                ride.driverId,
                RideStatus.DRIVER_ASSIGNED,
            )
        }
        return RideStatusDTO(
            rideId = rideId,
            status =
                cachedStatus?.let {
                    RideStatus.valueOf(cachedStatus)
                } ?: RideStatus.IN_PROGRESS,
            driverId = null,
        )
    }
}
