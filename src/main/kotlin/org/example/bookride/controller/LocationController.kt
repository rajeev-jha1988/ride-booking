package org.example.bookride.controller

import org.example.bookride.dto.DriverLocation
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/location")
class LocationController {
    @PostMapping
    fun create(
        @RequestBody driverLocation: DriverLocation,
    ) {
    }
}
