package org.example.bookride.service.impl

import org.example.bookride.dto.DriverLocation
import org.example.bookride.model.Location
import org.example.bookride.repository.LocationRepository
import org.example.bookride.service.CityResolverService
import org.example.bookride.service.LocationService
import org.springframework.data.geo.Point
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class LocationServiceImpl(
    val locationRepository: LocationRepository,
    val redisTemplate: RedisTemplate<String, Any>,
    val cityResolverService: CityResolverService,
) : LocationService {
    override fun updateLocation(driverLocation: DriverLocation) {
        val currentCity =
            cityResolverService.resolveCity(
                driverLocation.location.latitude,
                driverLocation.location.longitude,
            )

        val driverCity = "driver:${driverLocation.driverId}:city"

        val oldCity = redisTemplate.opsForValue().get(driverCity)

        val driverLocation =
            Location(
                id = UUID.randomUUID(),
                driverId = driverLocation.driverId,
                latitude = driverLocation.location.latitude,
                longitude = driverLocation.location.longitude,
            )
        locationRepository.save(
            driverLocation,
        )

        val newGeoKey = "driver:$currentCity"
        val point =
            Point(
                driverLocation.longitude,
                driverLocation.latitude,
            )

        redisTemplate
            .opsForValue()
            .set(driverCity, currentCity!!)

        redisTemplate
            .opsForGeo()
            .add(newGeoKey, point, driverLocation.driverId.toString())

        if (oldCity != null && oldCity != currentCity) {
            redisTemplate.opsForGeo().remove(
                "driver:$oldCity",
                driverLocation
                    .driverId
                    .toString(),
            )
        }
    }
}
