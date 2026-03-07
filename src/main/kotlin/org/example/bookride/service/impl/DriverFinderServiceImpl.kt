package org.example.bookride.service.impl

import org.example.bookride.dto.DriverInfo
import org.example.bookride.dto.DriverRideRequest
import org.example.bookride.dto.Location
import org.example.bookride.error.DriverNotAvailableInSystem
import org.example.bookride.model.DriverStatus
import org.example.bookride.service.CityResolverService
import org.example.bookride.service.DriverFinderService
import org.example.bookride.service.DriverService
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Metrics
import org.springframework.data.redis.connection.RedisGeoCommands
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.domain.geo.GeoReference
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.UUID

@Service
class DriverFinderServiceImpl(
    val redisTemplate: RedisTemplate<String, Any>,
    val cityResolverService: CityResolverService,
    val driverService: DriverService,
) : DriverFinderService {
    override suspend fun finderDriver(
        rideId: UUID,
        source: Location,
        excludedDrivers: Set<UUID>,
    ): DriverRideRequest {
        val city = cityResolverService.resolveCity(source.latitude, source.longitude)

        // prepare key where we have all driver
        val key = "driver:$city"
        // We search in a 10km radius from the rider's point
        // 2. Reference point (Rider's location)
        val reference = GeoReference.fromCoordinate<Any>(source.longitude, source.latitude)
        val distance = Distance(10.0, Metrics.KILOMETERS)
        // val geoShape = GeoShape.byRadius(distance)
        // 3. Configure search to include distance and order by closest
        val args: RedisGeoCommands.GeoSearchCommandArgs =
            RedisGeoCommands.GeoSearchCommandArgs
                .newGeoSearchArgs()
                .includeDistance()
                .sortAscending()
                .limit(50)

        val geoResults = redisTemplate.opsForGeo().search(key, reference, distance, args)
        val driverIds = geoResults.content.map { it.content.name.toString() }
        val statusKeys = driverIds.map { "driver:$it:status" }
        val statuses = redisTemplate.opsForValue().multiGet(statusKeys) ?: emptyList()

        for (i in geoResults.content.indices) {
            val driverIdStr =
                geoResults.content[i]
                    .content.name
                    .toString()

            // Skip if Redis says they are busy or its added in exclude list like black list
            if (statuses[i] != null ||
                excludedDrivers
                    .contains(UUID.fromString(driverIdStr))
            ) {
                continue
            }

            val driverUuid = UUID.fromString(driverIdStr)

            // 4. TRY TO LOCK IN DATABASE
            // If this returns true, we officially "own" this driver for 10 seconds
            if (driverService.updateDriverStatus(rideId, DriverStatus.AVAILABLE, DriverStatus.OFFER_PENDING)) {
                // 5. Update Redis to prevent other searches from picking them
                val lockKey = "driver:$driverIdStr:status"
                val timerKey = "offer_timer:$rideId:$driverIdStr:${source.latitude}:${source.longitude}"

                redisTemplate.opsForValue().set(lockKey, "OFFER_PENDING", Duration.ofSeconds(12))
                redisTemplate.opsForValue().set(timerKey, "", Duration.ofSeconds(10))

                return DriverRideRequest(rideId = rideId, driver = DriverInfo(driverId = driverUuid))
            }

            // If Database Lock failed (someone else took the ride or driver),
            // the loop naturally continues to the next nearest driver (#2, #3...)
        }

        throw DriverNotAvailableInSystem()
    }
}
