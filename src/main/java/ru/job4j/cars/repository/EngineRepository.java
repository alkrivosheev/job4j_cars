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
     * @param engine двигатель.
     * @return двигатель с id.
     */
    public Engine create(Engine engine) {
        crudRepository.run(session -> session.persist(engine));
        return engine;
    }

    /**
     * Обновить в базе двигатель.
     * @param engine двигатель.
     */
    public void update(Engine engine) {
        crudRepository.run(session -> session.merge(engine));
    }

    /**
     * Удалить двигатель по id.
     * @param engineId ID
     */
    public void delete(int engineId) {
        crudRepository.run(
                "DELETE FROM Engine WHERE id = :fId",
                Map.of("fId", engineId)
        );
    }

    /**
     * Список двигателей, отсортированных по id.
     * @return список двигателей.
     */
    public List<Engine> findAllOrderById() {
        return crudRepository.query("FROM Engine ORDER BY id ASC", Engine.class);
    }

    /**
     * Найти двигатель по ID
     * @return двигатель.
     */
    public Optional<Engine> findById(int engineId) {
        return crudRepository.optional(
                "FROM Engine WHERE id = :fId", Engine.class,
                Map.of("fId", engineId)
        );
    }
}