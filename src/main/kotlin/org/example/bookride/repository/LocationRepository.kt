package org.example.bookride.repository

import org.example.bookride.model.Location
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface LocationRepository : JpaRepository<Location, UUID>
