package com.mantenix.vehicle.dto;

import com.mantenix.vehicle.model.Vehicle;
import com.mantenix.vehicle.model.VehicleType;

import java.time.OffsetDateTime;
import java.util.UUID;

/** Data the API returns. The JPA entity is never exposed directly. */
public record VehicleResponse(
        UUID id,
        UUID ownerId,
        String plate,
        VehicleType type,
        String brand,
        String model,
        Integer modelYear,
        Integer mileage,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static VehicleResponse from(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getOwnerId(),
                vehicle.getPlate(),
                vehicle.getType(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getModelYear(),
                vehicle.getMileage(),
                vehicle.getCreatedAt(),
                vehicle.getUpdatedAt()
        );
    }
}
