package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import ru.job4j.cars.model.WheelSide;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class WheelSideRepository {

    private final CrudRepository crudRepository;

    /**
     * Сохранить в базе.
     *
     * @param wheelSide тип расположения руля автомобиля.
     * @return тип расположения руля автомобиля с id.
     */
    public WheelSide create(WheelSide wheelSide) {
        crudRepository.run(session -> session.persist(wheelSide));
        return wheelSide;
    }

    /**
     * Обновить в базе тип расположения руля автомобиля.
     *
     * @param wheelSide тип расположения руля автомобиля.
     */
    public void update(WheelSide wheelSide) {
        crudRepository.run(session -> session.merge(wheelSide));
    }

    /**
     * Удалить тип расположения руля автомобиля по id.
     *
     * @param wheelSideId ID
     */
    public void delete(int wheelSideId) {
        crudRepository.run(
                "DELETE FROM WheelSide WHERE id = :fId",
                Map.of("fId", wheelSideId)
        );
    }

    /**
     * Список всех типов расположения руля автомобилей, отсортированных по id.
     *
     * @return список типов расположения руля.
     */
    public List<WheelSide> findAllOrderById() {
        return crudRepository.query("FROM WheelSide ORDER BY id ASC", WheelSide.class);
    }

    /**
     * Найти тип расположения руля автомобиля по ID
     *
     * @param wheelSideId ID типа расположения руля
     * @return тип расположения руля автомобиля.
     */
    public Optional<WheelSide> findById(int wheelSideId) {
        return crudRepository.optional(
                "SELECT ws FROM WheelSide ws WHERE ws.id = :fId",
                WheelSide.class,
                Map.of("fId", wheelSideId)
        );
    }
}