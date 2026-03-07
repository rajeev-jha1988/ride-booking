package org.example.bookride.service.impl

import com.uber.h3core.H3Core
import org.example.bookride.constant.BookRideConstant.DELHI_BOUNDRIES
import org.example.bookride.constant.BookRideConstant.MUMBAI_BOUNDARIES
import org.example.bookride.service.CityResolverService
import org.springframework.stereotype.Service

@Service
class CityResolverServiceImpl(
    val h3Core: H3Core,
) : CityResolverService {
    private val cityHexaMapping = mutableMapOf<String, String>()

    init {
        val delhiHexagons = h3Core.polygonToCellAddresses(DELHI_BOUNDRIES, null, 6)

        for (delhiHexagon in delhiHexagons) {
            cityHexaMapping[delhiHexagon] = "DELHI"
        }

        val mumbaiHexagons = h3Core.polygonToCellAddresses(MUMBAI_BOUNDARIES, null, 6)
        for (mumbaiHexagon in mumbaiHexagons) {
            cityHexaMapping[mumbaiHexagon] = "MUMBAI"
        }
        TODO("Not yet implemented")
    }

    override fun resolveCity(
        lat: Double,
        long: Double,
    ): String? {
        val resolution = 6 // Standard city-level resolution
        val h3Index = h3Core.latLngToCellAddress(lat, long, resolution)
        return cityHexaMapping[h3Index]
    }
}
