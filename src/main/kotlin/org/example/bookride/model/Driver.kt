package org.example.bookride.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.SourceType
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "driver")
data class Driver(
    @Id
    val id: UUID,
    @Column(nullable = false, name = "name")
    val name: String,
    @Column(nullable = false, name = "status")
    @Enumerated(EnumType.STRING)
    var status: DriverStatus,
    @UpdateTimestamp(source = SourceType.DB)
    val updatedAt: Instant,
)

enum class DriverStatus {
    OFFER_PENDING,
    ON_RIDE,
    AVAILABLE,
    OUT_OF_SERVICE,
    IN_ACTIVE,
    BLOCKED,
}
