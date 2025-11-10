package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.WheelSide;
import ru.job4j.cars.repository.WheelSideRepository;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class WheelSideService {

    private final WheelSideRepository wheelSideRepository;

    public WheelSide create(WheelSide wheelSide) {
        return wheelSideRepository.create(wheelSide);
    }

    public void update(WheelSide wheelSide) {
        wheelSideRepository.update(wheelSide);
    }

    public void delete(int wheelSideId) {
        wheelSideRepository.delete(wheelSideId);
    }

    public List<WheelSide> findAllOrderById() {
        return wheelSideRepository.findAllOrderById();
    }

    public Optional<WheelSide> findById(int wheelSideId) {
        return wheelSideRepository.findById(wheelSideId);
    }
}
