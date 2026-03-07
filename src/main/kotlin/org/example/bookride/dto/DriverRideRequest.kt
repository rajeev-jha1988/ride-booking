package org.example.bookride.dto

import java.util.UUID

data class DriverRideRequest(
    val rideId: UUID,
    val driver: DriverInfo,
)

data class DriverInfo(
    val driverId: UUID,
)
