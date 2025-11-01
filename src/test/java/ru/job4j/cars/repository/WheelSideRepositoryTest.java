package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.WheelSide;
import ru.job4j.cars.testutil.TestDatabaseConfig;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для репозитория типов расположения руля автомобилей (WheelSideRepository)
 */
class WheelSideRepositoryTest {

    private SessionFactory sessionFactory;
    private CrudRepository crudRepository;
    private WheelSideRepository wheelSideRepository;

    /**
     * Инициализация репозиториев перед каждым тестом.
     */
    @BeforeEach
    void setUp() throws Exception {
        this.sessionFactory = TestDatabaseConfig.setupTestEnvironment("db/liquibase_test.properties");
        this.crudRepository = new CrudRepository(sessionFactory);
        this.wheelSideRepository = new WheelSideRepository(crudRepository);
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
     * Создает тестовый тип расположения руля автомобиля.
     */
    private WheelSide createTestWheelSide(String name) {
        WheelSide wheelSide = new WheelSide();
        wheelSide.setName(name + "_" + System.currentTimeMillis());
        return wheelSide;
    }

    /**
     * Тестирует создание нового типа расположения руля автомобиля в репозитории.
     * Проверяет, что после сохранения тип расположения руля имеет присвоенный идентификатор,
     * имя сохраняется корректно и тип расположения руля может быть найден по идентификатору.
     */
    @Test
    void whenCreateNewWheelSideThenRepositoryHasSameWheelSide() {
        WheelSide wheelSide = createTestWheelSide("Левый");
        WheelSide savedWheelSide = wheelSideRepository.create(wheelSide);

        assertThat(savedWheelSide.getId()).isGreaterThan(0);
        assertThat(savedWheelSide.getName()).isEqualTo(wheelSide.getName());

        Optional<WheelSide> foundWheelSide = wheelSideRepository.findById(Math.toIntExact(savedWheelSide.getId()));
        assertThat(foundWheelSide).isPresent();
        assertThat(foundWheelSide.get().getName()).isEqualTo(wheelSide.getName());
    }

    /**
     * Тестирует обновление данных типа расположения руля автомобиля в репозитории.
     * Проверяет, что изменение имени корректно сохраняется
     * и может быть получено при последующем поиске.
     */
    @Test
    void whenUpdateWheelSideThenRepositoryHasUpdatedWheelSide() {
        WheelSide wheelSide = createTestWheelSide("Правый");
        WheelSide savedWheelSide = wheelSideRepository.create(wheelSide);

        savedWheelSide.setName("Левый");
        wheelSideRepository.update(savedWheelSide);

        Optional<WheelSide> updatedWheelSide = wheelSideRepository.findById(Math.toIntExact(savedWheelSide.getId()));
        assertThat(updatedWheelSide).isPresent();
        assertThat(updatedWheelSide.get().getName()).isEqualTo("Левый");
    }

    /**
     * Тестирует удаление типа расположения руля автомобиля из репозитория.
     * Проверяет, что после удаления тип расположения руля больше не находится в хранилище
     * при поиске по идентификатору.
     */
    @Test
    void whenDeleteWheelSideThenRepositoryDoesNotHaveWheelSide() {
        WheelSide wheelSide = createTestWheelSide("Левый руль");
        WheelSide savedWheelSide = wheelSideRepository.create(wheelSide);

        wheelSideRepository.delete(Math.toIntExact(savedWheelSide.getId()));

        Optional<WheelSide> deletedWheelSide = wheelSideRepository.findById(Math.toIntExact(savedWheelSide.getId()));
        assertThat(deletedWheelSide).isNotPresent();
    }

    /**
     * Тестирует получение всех типов расположения руля автомобилей с сортировкой по идентификатору.
     * Проверяет, что возвращаются все сохраненные типы расположения руля в правильном порядке,
     * отсортированные по возрастанию идентификатора.
     */
    @Test
    void whenFindAllOrderByIdThenReturnsAllWheelSidesSortedById() {
        WheelSide wheelSide1 = createTestWheelSide("Левый");
        WheelSide wheelSide2 = createTestWheelSide("Правый");
        WheelSide savedWheelSide1 = wheelSideRepository.create(wheelSide1);
        WheelSide savedWheelSide2 = wheelSideRepository.create(wheelSide2);

        List<WheelSide> allWheelSides = wheelSideRepository.findAllOrderById();

        assertThat(allWheelSides).hasSizeGreaterThanOrEqualTo(2);
        assertThat(allWheelSides.get(0).getId()).isLessThan(allWheelSides.get(1).getId());
    }

    /**
     * Тестирует поиск типа расположения руля автомобиля по несуществующему идентификатору.
     * Проверяет, что для несуществующего идентификатора возвращается пустой Optional,
     * что соответствует ожидаемому поведению при отсутствии данных.
     */
    @Test
    void whenFindByNonExistentIdThenReturnsEmptyOptional() {
        Optional<WheelSide> foundWheelSide = wheelSideRepository.findById(-1);
        assertThat(foundWheelSide).isNotPresent();
    }

    /**
     * Тестирует поиск типа расположения руля автомобиля по нулевому идентификатору.
     * Проверяет граничное условие - возвращается пустой Optional для идентификатора 0,
     * так как в системе идентификаторы обычно начинаются с 1.
     */
    @Test
    void whenFindByIdWithZeroIdThenReturnsEmpty() {
        Optional<WheelSide> result = wheelSideRepository.findById(0);
        assertThat(result).isNotPresent();
    }

    /**
     * Тестирует удаление несуществующего типа расположения руля автомобиля.
     * Проверяет, что операция удаления для несуществующего идентификатора
     * завершается без генерации исключений, обеспечивая отказоустойчивость.
     */
    @Test
    void whenDeleteNonExistentWheelSideThenNoException() {
        boolean deletionResult = true;
        try {
            wheelSideRepository.delete(-1);
        } catch (Exception e) {
            deletionResult = false;
        }
        assertThat(deletionResult).isTrue();
    }
}