package org.example.bookride.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class DriverLocation(
    @JsonProperty("driver_id")
    val driverId: UUID,
    @JsonProperty("location")
    val location: Location,
)
