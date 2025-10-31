package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import ru.job4j.cars.model.Body;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class BodyRepository {

    private final CrudRepository crudRepository;

    /**
     * Сохранить в базе.
     *
     * @param body тип кузова автомобиля.
     * @return тип кузова автомобиля с id.
     */
    public Body create(Body body) {
        crudRepository.run(session -> session.persist(body));
        return body;
    }

    /**
     * Обновить в базе тип кузова автомобиля.
     *
     * @param body тип кузова автомобиля.
     */
    public void update(Body body) {
        crudRepository.run(session -> session.merge(body));
    }

    /**
     * Удалить тип кузова автомобиля по id.
     *
     * @param bodyId ID
     */
    public void delete(int bodyId) {
        crudRepository.run(
                "DELETE FROM Body WHERE id = :fId",
                Map.of("fId", bodyId)
        );
    }

    /**
     * Список всех типов кузова автомобилей, отсортированных по id.
     *
     * @return список типов кузова.
     */
    public List<Body> findAllOrderById() {
        return crudRepository.query("FROM Body ORDER BY id ASC", Body.class);
    }

    /**
     * Найти тип кузова автомобиля по ID
     *
     * @param bodyId ID типа кузова
     * @return тип кузова автомобиля.
     */
    public Optional<Body> findById(int bodyId) {
        return crudRepository.optional(
                "SELECT b FROM Body b WHERE b.id = :fId",
                Body.class,
                Map.of("fId", bodyId)
        );
    }
}