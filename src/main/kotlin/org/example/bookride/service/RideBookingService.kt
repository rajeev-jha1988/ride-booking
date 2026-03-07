package org.example.bookride.service

import org.example.bookride.dto.RideAcceptResponse
import org.example.bookride.dto.RideRequest
import org.example.bookride.dto.RideResponse
import java.util.UUID

interface RideBookingService {
    fun createRide(ride: RideRequest): RideResponse

    suspend fun acceptRideRequest(
        rideId: UUID,
        driverId: UUID,
    ): RideAcceptResponse

    suspend fun cancelRideRequest(
        rideId: UUID,
        driverId: UUID,
    )

    suspend fun completeRide(
        rideId: UUID,
        driverId: UUID,
    ): RideAcceptResponse

    suspend fun bookRide(rideId: UUID)
}
