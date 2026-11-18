package com.mantenix.vehicle.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * A car or motorcycle registered by its owner.
 * Mapped to the "vehicles" table created by Flyway (V1__create_vehicles_table.sql).
 */
@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    private UUID id;

    /** User id from auth-service. Stored as a plain UUID: no foreign key across services (ADR-004). */
    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(nullable = false, unique = true, length = 10)
    private String plate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private VehicleType type;

    @Column(nullable = false, length = 50)
    private String brand;

    @Column(nullable = false, length = 50)
    private String model;

    @Column(name = "model_year", nullable = false)
    private Integer modelYear;

    @Column(nullable = false)
    private Integer mileage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        createdAt = OffsetDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getOwnerId() { return ownerId; }
    public void setOwnerId(UUID ownerId) { this.ownerId = ownerId; }

    public String getPlate() { return plate; }
    public void setPlate(String plate) { this.plate = plate; }

    public VehicleType getType() { return type; }
    public void setType(VehicleType type) { this.type = type; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Integer getModelYear() { return modelYear; }
    public void setModelYear(Integer modelYear) { this.modelYear = modelYear; }

    public Integer getMileage() { return mileage; }
    public void setMileage(Integer mileage) { this.mileage = mileage; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
