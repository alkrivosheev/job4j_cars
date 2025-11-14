package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.FuelType;
import ru.job4j.cars.repository.FuelTypeRepository;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class FuelTypeService {

    private final FuelTypeRepository fuelTypeRepository;

    public FuelType create(FuelType fuelType) {
        return fuelTypeRepository.create(fuelType);
    }

    public void update(FuelType fuelType) {
        fuelTypeRepository.update(fuelType);
    }

    public void delete(int fuelTypeId) {
        fuelTypeRepository.delete(fuelTypeId);
    }

    public List<FuelType> findAllOrderById() {
        return fuelTypeRepository.findAllOrderById();
    }

    public Optional<FuelType> findById(int fuelTypeId) {
        return fuelTypeRepository.findById(fuelTypeId);
    }
}
