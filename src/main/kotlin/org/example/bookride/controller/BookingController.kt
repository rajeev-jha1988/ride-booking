package org.example.bookride.controller

import org.example.bookride.dto.RideAcceptResponse
import org.example.bookride.dto.RideRequest
import org.example.bookride.dto.RideStatusDTO
import org.example.bookride.service.RideBookingService
import org.example.bookride.service.RideStatusService
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.net.http.HttpHeaders
import java.util.UUID

@RestController
class BookingController(
    val rideBookingService: RideBookingService,
    val rideStatusService: RideStatusService,
) {
    @PostMapping("/ride")
    fun createRide(
        @RequestBody rideRequest: RideRequest,
    ) {
        rideBookingService.createRide(rideRequest)
    }

    @PatchMapping("/rider/{rideId}")
    suspend fun bookRide(
        @PathVariable rideId: UUID,
    ) {
        rideBookingService.bookRide(rideId)
    }

    @PostMapping("/rider/{rideId}/accept")
    suspend fun acceptRide(
        @PathVariable rideId: UUID,
        headers: HttpHeaders,
    ): RideAcceptResponse {
        val driverId = headers.firstValue("driver-id")!!.orElse(null)
        return rideBookingService
            .acceptRideRequest(
                rideId,
                UUID
                    .fromString(driverId),
            )
    }

    @PostMapping("/rider/{rideId}/reject")
    suspend fun rejectRide(
        @PathVariable rideId: UUID,
        headers: HttpHeaders,
    ): RideAcceptResponse {
        val driverId = headers.firstValue("driver-id")!!.orElse(null)

        return rideBookingService
            .acceptRideRequest(
                rideId,
                UUID
                    .fromString(driverId),
            )
    }

    @PostMapping("/rider/{rideId}/complete")
    suspend fun completeRide(
        @PathVariable rideId: UUID,
        headers: HttpHeaders,
    ): RideAcceptResponse {
        val driverId = headers.firstValue("driver-id")!!.orElse(null)

        return rideBookingService
            .completeRide(
                rideId,
                UUID
                    .fromString(driverId),
            )
    }

    // server side event
    @GetMapping("/v1/rides/{rideId}/status-stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamRideStatus(
        @PathVariable rideId: UUID,
    ): Flux<ServerSentEvent<RideStatusDTO>> {
        // We listen to a Redis Pub/Sub channel or an internal Sink
        return rideStatusService
            .getStatusStream(rideId)
            .map { status ->
                ServerSentEvent
                    .builder(status)
                    .event("ride-update")
                    .build()
            }
    }
}
