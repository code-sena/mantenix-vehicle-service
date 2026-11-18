package com.mantenix.vehicle.controller;

import com.mantenix.vehicle.dto.VehicleRequest;
import com.mantenix.vehicle.dto.VehicleResponse;
import com.mantenix.vehicle.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/** REST endpoints for vehicles. Receives and returns DTOs only. */
@RestController
@RequestMapping("/vehicles")
@Tag(name = "Vehicles", description = "Register and manage cars and motorcycles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a vehicle")
    public VehicleResponse create(@Valid @RequestBody VehicleRequest request) {
        return vehicleService.create(request);
    }

    @GetMapping
    @Operation(summary = "List vehicles", description = "Optionally filter by owner")
    public List<VehicleResponse> findAll(@RequestParam(required = false) UUID ownerId) {
        return vehicleService.findAll(ownerId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a vehicle by id")
    public VehicleResponse findById(@PathVariable UUID id) {
        return vehicleService.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a vehicle", description = "Mileage cannot be lower than the current value")
    public VehicleResponse update(@PathVariable UUID id, @Valid @RequestBody VehicleRequest request) {
        return vehicleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a vehicle")
    public void delete(@PathVariable UUID id) {
        vehicleService.delete(id);
    }
}
