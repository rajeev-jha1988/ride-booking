package org.example.bookride.repository

import org.example.bookride.model.Ride
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RideRepository : JpaRepository<Ride, UUID>
