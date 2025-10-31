package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import ru.job4j.cars.model.FuelType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class FuelTypeRepository {

    private final CrudRepository crudRepository;

    /**
     * Сохранить в базе.
     *
     * @param fuelType тип топлива автомобиля.
     * @return тип топлива автомобиля с id.
     */
    public FuelType create(FuelType fuelType) {
        crudRepository.run(session -> session.persist(fuelType));
        return fuelType;
    }

    /**
     * Обновить в базе тип топлива автомобиля.
     *
     * @param fuelType тип топлива автомобиля.
     */
    public void update(FuelType fuelType) {
        crudRepository.run(session -> session.merge(fuelType));
    }

    /**
     * Удалить тип топлива автомобиля по id.
     *
     * @param fuelTypeId ID
     */
    public void delete(int fuelTypeId) {
        crudRepository.run(
                "DELETE FROM FuelType WHERE id = :fId",
                Map.of("fId", fuelTypeId)
        );
    }

    /**
     * Список всех типов топлива автомобилей, отсортированных по id.
     *
     * @return список типов топлива.
     */
    public List<FuelType> findAllOrderById() {
        return crudRepository.query("FROM FuelType ORDER BY id ASC", FuelType.class);
    }

    /**
     * Найти тип топлива автомобиля по ID
     *
     * @param fuelTypeId ID типа топлива
     * @return тип топлива автомобиля.
     */
    public Optional<FuelType> findById(int fuelTypeId) {
        return crudRepository.optional(
                "SELECT ft FROM FuelType ft WHERE ft.id = :fId",
                FuelType.class,
                Map.of("fId", fuelTypeId)
        );
    }
}