package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.Body;
import ru.job4j.cars.repository.BodyRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BodyService {

    private final BodyRepository bodyRepository;

    public Body create(Body body) {
        return bodyRepository.create(body);
    }

    public void update(Body body) {
        bodyRepository.update(body);
    }

    public void delete(int bodyId) {
        bodyRepository.delete(bodyId);
    }

    public List<Body> findAllOrderById() {
        return bodyRepository.findAllOrderById();
    }

    public Optional<Body> findById(int bodyId) {
        return bodyRepository.findById(bodyId);
    }
}
