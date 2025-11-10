package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.CarModel;
import ru.job4j.cars.repository.CarModelRepository;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class CarModelService {

    private final CarModelRepository carModelRepository;

    public CarModel create(CarModel carModel) {
        return carModelRepository.create(carModel);
    }

    public void update(CarModel carModel) {
        carModelRepository.update(carModel);
    }

    public void delete(int carModelId) {
        carModelRepository.delete(carModelId);
    }

    public List<CarModel> findAllOrderById() {
        return carModelRepository.findAllOrderById();
    }

    public Optional<CarModel> findById(int carModelId) {
        return carModelRepository.findById(carModelId);
    }
}
