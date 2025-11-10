package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.FuelType;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class FuelTypeService {

    private final FuelTypeService fuelTypeService;

    public FuelType create(FuelType fuelType) {
        return fuelTypeService.create(fuelType);
    }

    public void update(FuelType fuelType) {
        fuelTypeService.update(fuelType);
    }

    public void delete(int fuelTypeId) {
        fuelTypeService.delete(fuelTypeId);
    }

    public List<FuelType> findAllOrderById() {
        return fuelTypeService.findAllOrderById();
    }

    public Optional<FuelType> findById(int fuelTypeId) {
        return fuelTypeService.findById(fuelTypeId);
    }
}
