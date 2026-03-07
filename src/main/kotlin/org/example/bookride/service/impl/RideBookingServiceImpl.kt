package org.example.bookride.service.impl

import org.example.bookride.dto.Location
import org.example.bookride.dto.RideAcceptResponse
import org.example.bookride.dto.RideRequest
import org.example.bookride.dto.RideResponse
import org.example.bookride.dto.RideStatusDTO
import org.example.bookride.model.DriverStatus
import org.example.bookride.model.Ride
import org.example.bookride.model.RideStatus
import org.example.bookride.repository.RideRepository
import org.example.bookride.service.DriverFinderService
import org.example.bookride.service.DriverService
import org.example.bookride.service.RideBookingService
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.util.UUID

@Service
class RideBookingServiceImpl(
    val rideRepository: RideRepository,
    val driverService: DriverService,
    val redisTemplate: RedisTemplate<String, Any>,
    val driverFinderService: DriverFinderService,
) : RideBookingService {
    override fun createRide(ride: RideRequest): RideResponse {
        val ride =
            Ride(
                id = UUID.randomUUID(),
                status = RideStatus.IN_PROGRESS,
                sourceLat = ride.source.latitude,
                sourceLong = ride.source.longitude,
                destinationLat = ride.destination.latitude,
                destinationLong = ride.destination.longitude,
                price = 100.00,
            )
        rideRepository.save(
            ride,
        )

        return RideResponse(
            rideId = ride.id,
            price = ride.price,
        )
    }

    @Transactional
    override suspend fun acceptRideRequest(
        rideId: UUID,
        driverId: UUID,
    ): RideAcceptResponse {
        if (driverService.updateDriverStatus(
                driverId,
                DriverStatus.OFFER_PENDING,
                DriverStatus.ON_RIDE,
            )
        ) {
            val ride = rideRepository.findById(rideId).orElseThrow()
            ride.driverId = driverId
            ride.status = RideStatus.DRIVER_ASSIGNED
            rideRepository.save(ride)
            // invalidate offer time
            redisTemplate.delete("offer_timer:$rideId:$driverId:${ride.sourceLat}:${ride.sourceLong}")

            // set driver status on ride
            redisTemplate
                .opsForValue()
                .set(
                    "driver:$driverId:status",
                    DriverStatus.ON_RIDE,
                    Duration.ofHours(2), // estimated time + system buffer
                )
            val rideUpdate = RideStatusDTO(rideId, driverId, RideStatus.DRIVER_ASSIGNED, null)

            redisTemplate.convertAndSend("ride_updates:$rideId", rideUpdate)
            return RideAcceptResponse(
                rideId = rideId,
                source = Location(ride.sourceLat, ride.sourceLong),
                destination = Location(ride.destinationLat, ride.destinationLong),
                price = 100.00,
            )
        }

        throw RuntimeException("Ride not found")
    }

    override suspend fun cancelRideRequest(
        rideId: UUID,
        driverId: UUID,
    ) {
        driverService.updateDriverStatus(
            driverId,
            DriverStatus.OFFER_PENDING,
            DriverStatus.AVAILABLE,
        )
    }

    override suspend fun completeRide(
        rideId: UUID,
        driverId: UUID,
    ): RideAcceptResponse {
        // mark rider available
        driverService.updateDriverStatus(
            driverId,
            DriverStatus.ON_RIDE,
            DriverStatus.AVAILABLE,
        )

        // remove driver satus
        redisTemplate.delete("driver:$driverId:status")

        // update ride status
        // this could be done via rest call from UI in system
        // here we may have recheck pricing based on actual time and city
        val ride = rideRepository.findById(rideId).orElseThrow()
        ride.status = RideStatus.COMPLETED
        rideRepository.save(ride)

        return RideAcceptResponse(
            rideId = rideId,
            source = Location(ride.sourceLat, ride.sourceLong),
            destination = Location(ride.destinationLat, ride.destinationLong),
            price = 100.00,
        )
    }

    override suspend fun bookRide(rideId: UUID) {
        val ride = rideRepository.findById(rideId).orElseThrow()

        val driverRide = driverFinderService.finderDriver(rideId, Location(ride.sourceLat, ride.sourceLong))

        println("bookride $rideId: $driverRide")
        // send notification to driver and wait
    }
}
