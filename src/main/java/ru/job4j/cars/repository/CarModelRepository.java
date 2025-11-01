package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import ru.job4j.cars.model.CarModel;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class CarModelRepository {

    private final CrudRepository crudRepository;

    /**
     * Сохранить в базе.
     *
     *  @param carModel модель автомобиля.
     * @return модель автомобиля с id.
     */
    public CarModel create(CarModel carModel) {
        crudRepository.run(session -> session.persist(carModel));
        return carModel;
    }

    /**
     * Обновить в базе модель автомобиля.
     *
     * @param carModel модель автомобиля.
     */
    public void update(CarModel carModel) {
        crudRepository.run(session -> session.merge(carModel));
    }

    /**
     * Удалить модель автомобиля по id.
     *
     * @param carModelId ID
     */
    public void delete(int carModelId) {
        crudRepository.run(
                "DELETE FROM CarModel WHERE id = :fId",
                Map.of("fId", carModelId)
        );
    }

    /**
     * Список всех моделей автомобилей, отсортированных по id.
     *
     * @return список моделей.
     */
    public List<CarModel> findAllOrderById() {
        return crudRepository.query("FROM CarModel ORDER BY id ASC", CarModel.class);
    }

    /**
     * Найти модель автомобиля по ID
     *
     * @param carModelId ID модели
     * @return модель автомобиля.
     */
    public Optional<CarModel> findById(int carModelId) {
        return crudRepository.optional(
                "FROM CarModel WHERE id = :fId",
                CarModel.class,
                Map.of("fId", carModelId)
        );
    }
}