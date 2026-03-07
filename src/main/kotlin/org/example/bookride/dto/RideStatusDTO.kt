package org.example.bookride.dto

import org.example.bookride.model.RideStatus
import java.util.UUID

data class RideStatusDTO(
    val rideId: UUID,
    val driverId: UUID?,
    val status: RideStatus,
    val driverLocation: String? = null,
)
