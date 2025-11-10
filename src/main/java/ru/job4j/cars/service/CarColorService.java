package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.CarColor;
import ru.job4j.cars.repository.CarColorRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CarColorService {

    private final CarColorRepository carColorRepository;

    public CarColor create(CarColor carColor) {
        return carColorRepository.create(carColor);
    }

    public void update(CarColor carColor) {
        carColorRepository.update(carColor);
    }

    public void delete(int carColorId) {
        carColorRepository.delete(carColorId);
    }

    public List<CarColor> findAllOrderById() {
        return carColorRepository.findAllOrderById();
    }

    public Optional<CarColor> findById(int carColorId) {
        return carColorRepository.findById(carColorId);
    }
}
