package org.example.bookride.dto

import java.util.UUID

data class RideResponse(
    val rideId: UUID,
    val price: Double,
)

data class RideAcceptResponse(
    val rideId: UUID,
    val source: Location,
    val destination: Location,
    val price: Double,
)
