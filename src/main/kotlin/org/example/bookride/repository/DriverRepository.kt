package org.example.bookride.repository

import jakarta.persistence.LockModeType
import org.example.bookride.model.Driver
import org.example.bookride.model.DriverStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface DriverRepository : JpaRepository<Driver, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findByIdAndStatus(
        driverId: UUID,
        status: DriverStatus,
    ): Driver?
}
