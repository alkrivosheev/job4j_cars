package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.DriveType;
import ru.job4j.cars.repository.DriveTypeRepository;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class DriveTypeService {

    private final DriveTypeRepository driveTypeRepository;

    public DriveType create(DriveType driveType) {
        return driveTypeRepository.create(driveType);
    }

    public void update(DriveType driveType) {
        driveTypeRepository.update(driveType);
    }

    public void delete(int driveTypeId) {
        driveTypeRepository.delete(driveTypeId);
    }

    public List<DriveType> findAllOrderById() {
        return driveTypeRepository.findAllOrderById();
    }

    public Optional<DriveType> findById(int driveTypeId) {
        return driveTypeRepository.findById(driveTypeId);
    }
}
