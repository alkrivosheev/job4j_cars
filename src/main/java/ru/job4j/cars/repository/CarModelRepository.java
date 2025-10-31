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
     * @param carSpecId ID
     */
    public void delete(int carSpecId) {
        crudRepository.run(
                "DELETE FROM CarSpecification WHERE id = :fId",
                Map.of("fId", carSpecId)
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
     * @param carSpecId ID модели
     * @return модель автомобиля.
     */
    public Optional<CarModel> findById(int carSpecId) {
        return crudRepository.optional(
                "SELECT cs FROM CarModel cs LEFT JOIN FETCH cs.brand WHERE cs.id = :fId",
                CarModel.class,
                Map.of("fId", carSpecId)
        );
    }

    /**
     * Найти модели автомобилей по ID марки.
     *
     * @param brandId ID марки.
     * @return список моделей для марки.
     */
    public List<CarModel> findByBrandId(int brandId) {
        return crudRepository.query(
                "SELECT cs FROM CarModel cs WHERE cs.brand.id = :brandId ORDER BY cs.id ASC",
                CarModel.class,
                Map.of("brandId", brandId)
        );
    }
}