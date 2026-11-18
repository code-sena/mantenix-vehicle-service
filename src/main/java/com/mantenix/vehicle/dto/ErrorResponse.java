package com.mantenix.vehicle.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/** Standard error format shared by all Mantenix services (07-api/contracts/openapi/_shared.yaml). */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(String error, String message, List<FieldError> details) {

    public record FieldError(String field, String message) {
    }

    public static ErrorResponse of(String error, String message) {
        return new ErrorResponse(error, message, List.of());
    }
}
