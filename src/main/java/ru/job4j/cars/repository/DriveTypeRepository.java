package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import ru.job4j.cars.model.DriveType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
public class DriveTypeRepository {

    private final CrudRepository crudRepository;

    /**
     * Сохранить в базе.
     *
     * @param driveType тип привода автомобиля.
     * @return тип привода автомобиля с id.
     */
    public DriveType create(DriveType driveType) {
        crudRepository.run(session -> session.persist(driveType));
        return driveType;
    }

    /**
     * Обновить в базе тип привода автомобиля.
     *
     * @param driveType тип привода автомобиля.
     */
    public void update(DriveType driveType) {
        crudRepository.run(session -> session.merge(driveType));
    }

    /**
     * Удалить тип привода автомобиля по id.
     *
     * @param driveTypeId ID
     */
    public void delete(int driveTypeId) {
        crudRepository.run(
                "DELETE FROM DriveType WHERE id = :fId",
                Map.of("fId", driveTypeId)
        );
    }

    /**
     * Список всех типов привода автомобилей, отсортированных по id.
     *
     * @return список типов привода.
     */
    public List<DriveType> findAllOrderById() {
        return crudRepository.query("FROM DriveType ORDER BY id ASC", DriveType.class);
    }

    /**
     * Найти тип привода автомобиля по ID
     *
     * @param driveTypeId ID типа привода
     * @return тип привода автомобиля.
     */
    public Optional<DriveType> findById(int driveTypeId) {
        return crudRepository.optional(
                "FROM DriveType WHERE id = :fId",
                DriveType.class,
                Map.of("fId", driveTypeId)
        );
    }
}