package com.mantenix.vehicle.service;

import com.mantenix.vehicle.dto.VehicleRequest;
import com.mantenix.vehicle.dto.VehicleResponse;
import com.mantenix.vehicle.exception.BusinessRuleException;
import com.mantenix.vehicle.exception.ResourceNotFoundException;
import com.mantenix.vehicle.model.Vehicle;
import com.mantenix.vehicle.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/** Business logic for vehicles. Controllers only call this class; they never touch the repository. */
@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Transactional
    public VehicleResponse create(VehicleRequest request) {
        String plate = normalizePlate(request.plate());
        if (vehicleRepository.existsByPlate(plate)) {
            throw new BusinessRuleException("PLATE_ALREADY_EXISTS", "A vehicle with plate " + plate + " already exists");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setOwnerId(request.ownerId());
        vehicle.setPlate(plate);
        applyChanges(vehicle, request);
        vehicle.setMileage(request.mileage());

        return VehicleResponse.from(vehicleRepository.save(vehicle));
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> findAll(UUID ownerId) {
        List<Vehicle> vehicles = (ownerId == null)
                ? vehicleRepository.findAll()
                : vehicleRepository.findByOwnerId(ownerId);
        return vehicles.stream().map(VehicleResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public VehicleResponse findById(UUID id) {
        return VehicleResponse.from(getVehicle(id));
    }

    @Transactional
    public VehicleResponse update(UUID id, VehicleRequest request) {
        Vehicle vehicle = getVehicle(id);

        String plate = normalizePlate(request.plate());
        if (vehicleRepository.existsByPlateAndIdNot(plate, id)) {
            throw new BusinessRuleException("PLATE_ALREADY_EXISTS", "A vehicle with plate " + plate + " already exists");
        }
        // Business rule: the mileage of a vehicle never goes down.
        if (request.mileage() < vehicle.getMileage()) {
            throw new BusinessRuleException("MILEAGE_CANNOT_DECREASE",
                    "Mileage cannot be lower than the current value (" + vehicle.getMileage() + " km)");
        }

        vehicle.setPlate(plate);
        applyChanges(vehicle, request);
        vehicle.setMileage(request.mileage());

        return VehicleResponse.from(vehicleRepository.saveAndFlush(vehicle));
    }

    @Transactional
    public void delete(UUID id) {
        vehicleRepository.delete(getVehicle(id));
    }

    private Vehicle getVehicle(UUID id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle " + id + " not found"));
    }

    private void applyChanges(Vehicle vehicle, VehicleRequest request) {
        vehicle.setType(request.type());
        vehicle.setBrand(request.brand().trim());
        vehicle.setModel(request.model().trim());
        vehicle.setModelYear(request.modelYear());
    }

    private String normalizePlate(String plate) {
        return plate.trim().toUpperCase(Locale.ROOT);
    }
}
