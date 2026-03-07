package org.example.bookride.service.impl

import org.example.bookride.model.Driver
import org.example.bookride.model.DriverStatus
import org.example.bookride.repository.DriverRepository
import org.example.bookride.service.DriverService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class DriverServiceImpl(
    val driverRepository: DriverRepository,
) : DriverService {
    override suspend fun findDriverById(id: UUID): Driver? = driverRepository.findById(id).orElse(null)

    @Transactional
    override suspend fun updateDriverStatus(
        id: UUID,
        fromStatus: DriverStatus,
        toStatus: DriverStatus,
    ): Boolean {
        val driver = driverRepository.findByIdAndStatus(id, fromStatus) ?: return false
        driver.status = toStatus
        driverRepository.save(driver)
        return true
    }
}
