package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.repository.CarRepository;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class CarService {

    private final CarRepository carRepository;

    public Car create(Car car) {
        return carRepository.create(car);
    }

    public void update(Car car) {
        carRepository.update(car);
    }

    public void delete(int carId) {
        carRepository.delete(carId);
    }

    public List<Car> findAllOrderById() {
        return carRepository.findAllOrderById();
    }

    public Optional<Car> findById(int carId) {
        return carRepository.findById(carId);
    }
}
