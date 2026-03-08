package org.example.bookride.service

import org.example.bookride.dto.DriverLocation

interface LocationService {
    fun updateLocation(driverLocation: DriverLocation)
}
