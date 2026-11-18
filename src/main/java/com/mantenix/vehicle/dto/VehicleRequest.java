package com.mantenix.vehicle.dto;

import com.mantenix.vehicle.model.VehicleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/** Data the client sends to register or update a vehicle. */
public record VehicleRequest(

        @Schema(description = "Owner user id (from auth-service)", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull
        UUID ownerId,

        @Schema(description = "Colombian plate: ABC123 (car) or ABC12D (motorcycle)", example = "ABC123")
        @NotBlank
        @Pattern(regexp = "^[A-Za-z]{3}[0-9]{2}[0-9A-Za-z]$", message = "must be a valid plate, e.g. ABC123 or ABC12D")
        String plate,

        @Schema(example = "CAR")
        @NotNull
        VehicleType type,

        @Schema(example = "Mazda")
        @NotBlank
        @Size(max = 50)
        String brand,

        @Schema(example = "CX-30")
        @NotBlank
        @Size(max = 50)
        String model,

        @Schema(example = "2022")
        @NotNull
        @Min(1950)
        @Max(2100)
        Integer modelYear,

        @Schema(description = "Current mileage in km", example = "35000")
        @NotNull
        @PositiveOrZero
        Integer mileage
) {
}
