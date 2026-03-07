package org.example.bookride.service

import org.example.bookride.dto.DriverLocation
import org.example.bookride.dto.RideRequest

interface LocationService {
    fun updateLocation(driverLocation: DriverLocation): RideRequest
}
