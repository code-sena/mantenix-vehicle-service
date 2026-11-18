package com.mantenix.vehicle.service;

import com.mantenix.vehicle.dto.VehicleRequest;
import com.mantenix.vehicle.dto.VehicleResponse;
import com.mantenix.vehicle.exception.BusinessRuleException;
import com.mantenix.vehicle.exception.ResourceNotFoundException;
import com.mantenix.vehicle.model.Vehicle;
import com.mantenix.vehicle.model.VehicleType;
import com.mantenix.vehicle.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Unit tests for the business rules of VehicleService (no database needed). */
class VehicleServiceTest {

    private VehicleRepository repository;
    private VehicleService service;

    private final UUID ownerId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        repository = mock(VehicleRepository.class);
        service = new VehicleService(repository);
        when(repository.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private VehicleRequest request(String plate, int mileage) {
        return new VehicleRequest(ownerId, plate, VehicleType.CAR, "Mazda", "CX-30", 2022, mileage);
    }

    @Test
    void createNormalizesThePlateToUppercase() {
        when(repository.existsByPlate("ABC123")).thenReturn(false);

        VehicleResponse response = service.create(request(" abc123 ", 35000));

        assertThat(response.plate()).isEqualTo("ABC123");
        assertThat(response.mileage()).isEqualTo(35000);
    }

    @Test
    void createRejectsADuplicatedPlate() {
        when(repository.existsByPlate("ABC123")).thenReturn(true);

        assertThatThrownBy(() -> service.create(request("ABC123", 0)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("ABC123");
        verify(repository, never()).save(any());
    }

    @Test
    void updateRejectsALowerMileage() {
        UUID id = UUID.randomUUID();
        Vehicle existing = new Vehicle();
        existing.setId(id);
        existing.setMileage(40000);
        when(repository.findById(id)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.update(id, request("ABC123", 39000)))
                .isInstanceOf(BusinessRuleException.class)
                .extracting("code").isEqualTo("MILEAGE_CANNOT_DECREASE");
    }

    @Test
    void findByIdFailsWhenTheVehicleDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
