package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import ru.job4j.cars.model.TransmissionType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class TransmissionTypeRepository {

    private final CrudRepository crudRepository;

    /**
     * Сохранить в базе.
     *
     * @param transmissionType тип трансмиссии автомобиля.
     * @return тип трансмиссии автомобиля с id.
     */
    public TransmissionType create(TransmissionType transmissionType) {
        crudRepository.run(session -> session.persist(transmissionType));
        return transmissionType;
    }

    /**
     * Обновить в базе тип трансмиссии автомобиля.
     *
     * @param transmissionType тип трансмиссии автомобиля.
     */
    public void update(TransmissionType transmissionType) {
        crudRepository.run(session -> session.merge(transmissionType));
    }

    /**
     * Удалить тип трансмиссии автомобиля по id.
     *
     * @param transmissionTypeId ID
     */
    public void delete(int transmissionTypeId) {
        crudRepository.run(
                "DELETE FROM TransmissionType WHERE id = :fId",
                Map.of("fId", transmissionTypeId)
        );
    }

    /**
     * Список всех типов трансмиссии автомобилей, отсортированных по id.
     *
     * @return список типов трансмиссии.
     */
    public List<TransmissionType> findAllOrderById() {
        return crudRepository.query("FROM TransmissionType ORDER BY id ASC", TransmissionType.class);
    }

    /**
     * Найти тип трансмиссии автомобиля по ID
     *
     * @param transmissionTypeId ID типа трансмиссии
     * @return тип трансмиссии автомобиля.
     */
    public Optional<TransmissionType> findById(int transmissionTypeId) {
        return crudRepository.optional(
                "FROM TransmissionType WHERE id = :fId",
                TransmissionType.class,
                Map.of("fId", transmissionTypeId)
        );
    }
}