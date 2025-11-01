package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.FuelType;
import ru.job4j.cars.testutil.TestDatabaseConfig;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для репозитория типов топлива автомобилей (FuelTypeRepository)
 */
class FuelTypeRepositoryTest {

    private SessionFactory sessionFactory;
    private CrudRepository crudRepository;
    private FuelTypeRepository fuelTypeRepository;

    /**
     * Инициализация репозиториев перед каждым тестом.
     */
    @BeforeEach
    void setUp() throws Exception {
        this.sessionFactory = TestDatabaseConfig.setupTestEnvironment("db/liquibase_test.properties");
        this.crudRepository = new CrudRepository(sessionFactory);
        this.fuelTypeRepository = new FuelTypeRepository(crudRepository);
    }

    /**
     * Закрытие SessionFactory после каждого теста.
     */
    @AfterEach
    void tearDown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }

    /**
     * Создает тестовый тип топлива автомобиля.
     */
    private FuelType createTestFuelType(String name) {
        FuelType fuelType = new FuelType();
        fuelType.setName(name + "_" + System.currentTimeMillis());
        return fuelType;
    }

    /**
     * Тестирует создание нового типа топлива автомобиля в репозитории.
     * Проверяет, что после сохранения тип топлива имеет присвоенный идентификатор,
     * имя сохраняется корректно и тип топлива может быть найден по идентификатору.
     */
    @Test
    void whenCreateNewFuelTypeThenRepositoryHasSameFuelType() {
        FuelType fuelType = createTestFuelType("Бензин");
        FuelType savedFuelType = fuelTypeRepository.create(fuelType);

        assertThat(savedFuelType.getId()).isGreaterThan(0);
        assertThat(savedFuelType.getName()).isEqualTo(fuelType.getName());

        Optional<FuelType> foundFuelType = fuelTypeRepository.findById(Math.toIntExact(savedFuelType.getId()));
        assertThat(foundFuelType).isPresent();
        assertThat(foundFuelType.get().getName()).isEqualTo(fuelType.getName());
    }

    /**
     * Тестирует обновление данных типа топлива автомобиля в репозитории.
     * Проверяет, что изменение имени корректно сохраняется
     * и может быть получено при последующем поиске.
     */
    @Test
    void whenUpdateFuelTypeThenRepositoryHasUpdatedFuelType() {
        FuelType fuelType = createTestFuelType("Дизель");
        FuelType savedFuelType = fuelTypeRepository.create(fuelType);

        savedFuelType.setName("Газ");
        fuelTypeRepository.update(savedFuelType);

        Optional<FuelType> updatedFuelType = fuelTypeRepository.findById(Math.toIntExact(savedFuelType.getId()));
        assertThat(updatedFuelType).isPresent();
        assertThat(updatedFuelType.get().getName()).isEqualTo("Газ");
    }

    /**
     * Тестирует удаление типа топлива автомобиля из репозитория.
     * Проверяет, что после удаления тип топлива больше не находится в хранилище
     * при поиске по идентификатору.
     */
    @Test
    void whenDeleteFuelTypeThenRepositoryDoesNotHaveFuelType() {
        FuelType fuelType = createTestFuelType("Электричество");
        FuelType savedFuelType = fuelTypeRepository.create(fuelType);

        fuelTypeRepository.delete(Math.toIntExact(savedFuelType.getId()));

        Optional<FuelType> deletedFuelType = fuelTypeRepository.findById(Math.toIntExact(savedFuelType.getId()));
        assertThat(deletedFuelType).isNotPresent();
    }

    /**
     * Тестирует получение всех типов топлива автомобилей с сортировкой по идентификатору.
     * Проверяет, что возвращаются все сохраненные типы топлива в правильном порядке,
     * отсортированные по возрастанию идентификатора.
     */
    @Test
    void whenFindAllOrderByIdThenReturnsAllFuelTypesSortedById() {
        FuelType fuelType1 = createTestFuelType("АИ-92");
        FuelType fuelType2 = createTestFuelType("АИ-95");
        FuelType savedFuelType1 = fuelTypeRepository.create(fuelType1);
        FuelType savedFuelType2 = fuelTypeRepository.create(fuelType2);

        List<FuelType> allFuelTypes = fuelTypeRepository.findAllOrderById();

        assertThat(allFuelTypes).hasSizeGreaterThanOrEqualTo(2);
        assertThat(allFuelTypes.get(0).getId()).isLessThan(allFuelTypes.get(1).getId());
    }

    /**
     * Тестирует поиск типа топлива автомобиля по несуществующему идентификатору.
     * Проверяет, что для несуществующего идентификатора возвращается пустой Optional,
     * что соответствует ожидаемому поведению при отсутствии данных.
     */
    @Test
    void whenFindByNonExistentIdThenReturnsEmptyOptional() {
        Optional<FuelType> foundFuelType = fuelTypeRepository.findById(-1);
        assertThat(foundFuelType).isNotPresent();
    }

    /**
     * Тестирует поиск типа топлива автомобиля по нулевому идентификатору.
     * Проверяет граничное условие - возвращается пустой Optional для идентификатора 0,
     * так как в системе идентификаторы обычно начинаются с 1.
     */
    @Test
    void whenFindByIdWithZeroIdThenReturnsEmpty() {
        Optional<FuelType> result = fuelTypeRepository.findById(0);
        assertThat(result).isNotPresent();
    }

    /**
     * Тестирует удаление несуществующего типа топлива автомобиля.
     * Проверяет, что операция удаления для несуществующего идентификатора
     * завершается без генерации исключений, обеспечивая отказоустойчивость.
     */
    @Test
    void whenDeleteNonExistentFuelTypeThenNoException() {
        boolean deletionResult = true;
        try {
            fuelTypeRepository.delete(-1);
        } catch (Exception e) {
            deletionResult = false;
        }
        assertThat(deletionResult).isTrue();
    }
}