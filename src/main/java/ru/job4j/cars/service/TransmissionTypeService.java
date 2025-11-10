package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.TransmissionType;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class TransmissionTypeService {

    private final TransmissionTypeService transmissionTypeService;

    public TransmissionType create(TransmissionType transmissionType) {
        return transmissionTypeService.create(transmissionType);
    }

    public void update(TransmissionType transmissionType) {
        transmissionTypeService.update(transmissionType);
    }

    public void delete(int transmissionTypeId) {
        transmissionTypeService.delete(transmissionTypeId);
    }

    public List<TransmissionType> findAllOrderById() {
        return transmissionTypeService.findAllOrderById();
    }

    public Optional<TransmissionType> findById(int transmissionTypeId) {
        return transmissionTypeService.findById(transmissionTypeId);
    }


}
