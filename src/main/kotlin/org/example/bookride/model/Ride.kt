package org.example.bookride.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.SourceType
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "ride")
data class Ride(
    @Id
    val id: UUID,
    @Column(nullable = false, name = "source_lat")
    val sourceLat: Double,
    @Column(nullable = false, name = "source_long")
    val sourceLong: Double,
    @Column(nullable = false, name = "destination_lat")
    val destinationLat: Double,
    @Column(nullable = false, name = "destination_long")
    val destinationLong: Double,
    @Column(insertable = false, updatable = false, name = "source_geohash")
    val sourceGoHash: String? = null,
    @Column(insertable = false, updatable = false, name = "destination_geohash")
    val destinationGoHash: String? = null,
    @Column(nullable = false, name = "driver_id")
    var driverId: UUID? = null,
    @Column(nullable = false, name = "status")
    @Enumerated(EnumType.STRING)
    var status: RideStatus,
    @Column(nullable = false, name = "price")
    val price: Double,
    @Column(name = "created_at")
    @CreationTimestamp(source = SourceType.DB)
    val createdAt: Instant? = null,
    @Column(name = "updated_at")
    @UpdateTimestamp(source = SourceType.DB)
    val updatedAt: Instant? = null,
)

enum class RideStatus {
    IN_PROGRESS,
    DRIVER_ASSIGNED,
    CANCELLED,
    COMPLETED,
}
