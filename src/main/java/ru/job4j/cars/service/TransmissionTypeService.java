package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.TransmissionType;
import ru.job4j.cars.repository.TransmissionTypeRepository;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class TransmissionTypeService {

    private final TransmissionTypeRepository transmissionTypeRepository;

    public TransmissionType create(TransmissionType transmissionType) {
        return transmissionTypeRepository.create(transmissionType);
    }

    public void update(TransmissionType transmissionType) {
        transmissionTypeRepository.update(transmissionType);
    }

    public void delete(int transmissionTypeId) {
        transmissionTypeRepository.delete(transmissionTypeId);
    }

    public List<TransmissionType> findAllOrderById() {
        return transmissionTypeRepository.findAllOrderById();
    }

    public Optional<TransmissionType> findById(int transmissionTypeId) {
        return transmissionTypeRepository.findById(transmissionTypeId);
    }
}
