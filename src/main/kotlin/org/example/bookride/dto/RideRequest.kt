package org.example.bookride.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class RideRequest(
    @JsonProperty("source")
    val source: Location,
    @JsonProperty("destination")
    val destination: Location,
)
