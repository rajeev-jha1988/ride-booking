package org.example.bookride.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.SourceType
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "location")
data class Location(
    @Id
    val id: UUID,
    @Column(nullable = false, name = "driver_id")
    val driverId: UUID,
    @Column(nullable = false, name = "latitude")
    val latitude: Double,
    @Column(nullable = false, name = "longitude")
    val longitude: Double,
    @Column(nullable = false, name = "created_at")
    @CreationTimestamp(source = SourceType.DB)
    val createdAt: Instant? = null,
)
