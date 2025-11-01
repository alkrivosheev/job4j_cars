package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import ru.job4j.cars.model.Category;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class CategoryRepository {

    private final CrudRepository crudRepository;

    /**
     * Сохранить в базе.
     *
     * @param category категория автомобиля.
     * @return категория автомобиля с id.
     */
    public Category create(Category category) {
        crudRepository.run(session -> session.persist(category));
        return category;
    }

    /**
     * Обновить в базе категорию автомобиля.
     *
     * @param category категория автомобиля.
     */
    public void update(Category category) {
        crudRepository.run(session -> session.merge(category));
    }

    /**
     * Удалить категорию автомобиля по id.
     *
     * @param categoryId ID
     */
    public void delete(int categoryId) {
        crudRepository.run(
                "DELETE FROM Category WHERE id = :fId",
                Map.of("fId", categoryId)
        );
    }

    /**
     * Список всех категорий автомобилей, отсортированных по id.
     *
     * @return список категорий.
     */
    public List<Category> findAllOrderById() {
        return crudRepository.query("FROM Category ORDER BY id ASC", Category.class);
    }

    /**
     * Найти категорию автомобиля по ID
     *
     * @param categoryId ID категории
     * @return категория автомобиля.
     */
    public Optional<Category> findById(int categoryId) {
        return crudRepository.optional(
                "FROM Category WHERE id = :fId",
                Category.class,
                Map.of("fId", categoryId)
        );
    }
}