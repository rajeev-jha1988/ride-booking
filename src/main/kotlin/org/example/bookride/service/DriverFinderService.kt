package org.example.bookride.service

import org.example.bookride.dto.DriverRideRequest
import org.example.bookride.dto.Location
import java.util.UUID

interface DriverFinderService {
    suspend fun finderDriver(
        rideId: UUID,
        source: Location,
        excludedDrivers: Set<UUID> = emptySet(),
    ): DriverRideRequest
}
