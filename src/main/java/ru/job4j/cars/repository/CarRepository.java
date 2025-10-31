package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import ru.job4j.cars.model.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class CarRepository {

    private final CrudRepository crudRepository;

    /**
     * Сохранить в базе.
     *
     * @param car автомобиль.
     * @return автомобиль с id.
     */
    public Car create(Car car) {
        crudRepository.run(session -> session.persist(car));
        return car;
    }

    /**
     * Обновить в базе автомобиль.
     *
     * @param car автомобиль.
     */
    public void update(Car car) {
        crudRepository.run(session -> session.merge(car));
    }

    /**
     * Удалить автомобиль по id.
     *
     * @param carId ID
     */
    public void delete(int carId) {
        crudRepository.run(
                "DELETE FROM Car WHERE id = :fId",
                Map.of("fId", carId)
        );
    }

    /**
     * Список автомобилей, отсортированных по id.
     *
     * @return список автомобилей.
     */
    public List<Car> findAllOrderById() {
        return crudRepository.query("FROM Car ORDER BY id ASC", Car.class);
    }

    /**
     * Найти автомобиль по ID
     *
     * @param carId ID автомобиля
     * @return автомобиль.
     */
    public Optional<Car> findById(int carId) {
        return crudRepository.optional(
                """
                        SELECT DISTINCT c FROM Car c
                        LEFT JOIN FETCH c.model
                        LEFT JOIN FETCH c.brand
                        LEFT JOIN FETCH c.brand
                        LEFT JOIN FETCH c.category
                        LEFT JOIN FETCH c.body
                        LEFT JOIN FETCH c.engine
                        LEFT JOIN FETCH c.transmissionType
                        LEFT JOIN FETCH c.driveType
                        LEFT JOIN FETCH c.carColor
                        LEFT JOIN FETCH c.fuelType
                        LEFT JOIN FETCH c.wheelSide
                        WHERE c.id = :fId
                        """,
                Car.class,
                Map.of("fId", carId)
        );
    }
}