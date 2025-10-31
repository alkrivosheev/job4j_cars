package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import ru.job4j.cars.model.CarColor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class CarColorRepository {

    private final CrudRepository crudRepository;

    /**
     * Сохранить в базе.
     *
     * @param carColor цвет автомобиля.
     * @return цвет автомобиля с id.
     */
    public CarColor create(CarColor carColor) {
        crudRepository.run(session -> session.persist(carColor));
        return carColor;
    }

    /**
     * Обновить в базе цвет автомобиля.
     *
     * @param carColor цвет автомобиля.
     */
    public void update(CarColor carColor) {
        crudRepository.run(session -> session.merge(carColor));
    }

    /**
     * Удалить цвет автомобиля по id.
     *
     * @param carColorId ID
     */
    public void delete(int carColorId) {
        crudRepository.run(
                "DELETE FROM CarColor WHERE id = :fId",
                Map.of("fId", carColorId)
        );
    }

    /**
     * Список всех цветов автомобилей, отсортированных по id.
     *
     * @return список цветов.
     */
    public List<CarColor> findAllOrderById() {
        return crudRepository.query("FROM CarColor ORDER BY id ASC", CarColor.class);
    }

    /**
     * Найти цвет автомобиля по ID
     *
     * @param carColorId ID цвета
     * @return цвет автомобиля.
     */
    public Optional<CarColor> findById(int carColorId) {
        return crudRepository.optional(
                "SELECT c FROM CarColor c WHERE c.id = :fId",
                CarColor.class,
                Map.of("fId", carColorId)
        );
    }
}