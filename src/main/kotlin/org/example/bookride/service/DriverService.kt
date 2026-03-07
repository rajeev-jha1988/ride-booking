package org.example.bookride.service

import org.example.bookride.model.Driver
import org.example.bookride.model.DriverStatus
import java.util.UUID

interface DriverService {
    suspend fun findDriverById(id: UUID): Driver?

    suspend fun updateDriverStatus(
        id: UUID,
        fromStatus: DriverStatus,
        toStatus: DriverStatus,
    ): Boolean
}
