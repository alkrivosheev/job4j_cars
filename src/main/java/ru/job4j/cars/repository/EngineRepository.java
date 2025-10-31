package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import ru.job4j.cars.model.Engine;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class EngineRepository {

    private final CrudRepository crudRepository;

    /**
     * Сохранить в базе.
     *
     * @param engine двигатель автомобиля.
     * @return двигатель автомобиля с id.
     */
    public Engine create(Engine engine) {
        crudRepository.run(session -> session.persist(engine));
        return engine;
    }

    /**
     * Обновить в базе двигатель автомобиля.
     *
     * @param engine двигатель автомобиля.
     */
    public void update(Engine engine) {
        crudRepository.run(session -> session.merge(engine));
    }

    /**
     * Удалить двигатель автомобиля по id.
     *
     * @param engineId ID
     */
    public void delete(int engineId) {
        crudRepository.run(
                "DELETE FROM Engine WHERE id = :fId",
                Map.of("fId", engineId)
        );
    }

    /**
     * Список всех двигателей автомобилей, отсортированных по id.
     *
     * @return список двигателей.
     */
    public List<Engine> findAllOrderById() {
        return crudRepository.query("FROM Engine ORDER BY id ASC", Engine.class);
    }

    /**
     * Найти двигатель автомобиля по ID
     *
     * @param engineId ID двигателя
     * @return двигатель автомобиля.
     */
    public Optional<Engine> findById(int engineId) {
        return crudRepository.optional(
                "SELECT e FROM Engine e WHERE e.id = :fId",
                Engine.class,
                Map.of("fId", engineId)
        );
    }
}