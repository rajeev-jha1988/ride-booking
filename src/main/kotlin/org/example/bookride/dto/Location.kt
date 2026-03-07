package org.example.bookride.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class Location(
    @JsonProperty("latitude")
    val latitude: Double,
    @JsonProperty("longitude")
    val longitude: Double,
)
