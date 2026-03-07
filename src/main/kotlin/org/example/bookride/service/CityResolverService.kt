package org.example.bookride.service

interface CityResolverService {
    fun resolveCity(
        lat: Double,
        long: Double,
    ): String?
}
