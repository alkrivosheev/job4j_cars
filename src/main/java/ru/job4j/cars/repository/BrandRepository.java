package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Brand;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class BrandRepository {

    private final CrudRepository crudRepository;

    /**
     * Сохранить в базе.
     *
     * @param brand марка автомобиля.
     * @return марка автомобиля с id.
     */
    public Brand create(Brand brand) {
        crudRepository.run(session -> session.persist(brand));
        return brand;
    }

    /**
     * Обновить в базе марку автомобиля.
     *
     * @param brand марка автомобиля.
     */
    public void update(Brand brand) {
        crudRepository.run(session -> session.merge(brand));
    }

    /**
     * Удалить марку автомобиля по id.
     *
     * @param brandId ID
     */
    public void delete(int brandId) {
        crudRepository.run(
                "DELETE FROM Brand WHERE id = :fId",
                Map.of("fId", brandId)
        );
    }

    /**
     * Список всех марок автомобилей, отсортированных по id.
     *
     * @return список марок.
     */
    public List<Brand> findAllOrderById() {
        return crudRepository.query("FROM Brand ORDER BY id ASC", Brand.class);
    }

    /**
     * Найти марку автомобиля по ID
     *
     * @param brandId ID марки
     * @return марка автомобиля.
     */
    public Optional<Brand> findById(int brandId) {
        return crudRepository.optional(
                "SELECT b FROM Brand b WHERE b.id = :fId",
                Brand.class,
                Map.of("fId", brandId)
        );
    }
}