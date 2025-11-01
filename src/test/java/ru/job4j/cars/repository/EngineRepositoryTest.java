package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.testutil.TestDatabaseConfig;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для репозитория двигателей автомобилей (EngineRepository)
 */
class EngineRepositoryTest {

    private SessionFactory sessionFactory;
    private CrudRepository crudRepository;
    private EngineRepository engineRepository;

    /**
     * Инициализация репозиториев перед каждым тестом.
     */
    @BeforeEach
    void setUp() throws Exception {
        this.sessionFactory = TestDatabaseConfig.setupTestEnvironment("db/liquibase_test.properties");
        this.crudRepository = new CrudRepository(sessionFactory);
        this.engineRepository = new EngineRepository(crudRepository);
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
     * Создает тестовый двигатель автомобиля.
     */
    private Engine createTestEngine(String name) {
        Engine engine = new Engine();
        engine.setName(name + "_" + System.currentTimeMillis());
        return engine;
    }

    /**
     * Тестирует создание нового двигателя автомобиля в репозитории.
     * Проверяет, что после сохранения двигатель имеет присвоенный идентификатор,
     * имя сохраняется корректно и двигатель может быть найден по идентификатору.
     */
    @Test
    void whenCreateNewEngineThenRepositoryHasSameEngine() {
        Engine engine = createTestEngine("Бензиновый 1.6");
        Engine savedEngine = engineRepository.create(engine);

        assertThat(savedEngine.getId()).isGreaterThan(0);
        assertThat(savedEngine.getName()).isEqualTo(engine.getName());

        Optional<Engine> foundEngine = engineRepository.findById(Math.toIntExact(savedEngine.getId()));
        assertThat(foundEngine).isPresent();
        assertThat(foundEngine.get().getName()).isEqualTo(engine.getName());
    }

    /**
     * Тестирует обновление данных двигателя автомобиля в репозитории.
     * Проверяет, что изменение имени корректно сохраняется
     * и может быть получено при последующем поиске.
     */
    @Test
    void whenUpdateEngineThenRepositoryHasUpdatedEngine() {
        Engine engine = createTestEngine("Дизельный 2.0");
        Engine savedEngine = engineRepository.create(engine);

        savedEngine.setName("Гибридный 1.8");
        engineRepository.update(savedEngine);

        Optional<Engine> updatedEngine = engineRepository.findById(Math.toIntExact(savedEngine.getId()));
        assertThat(updatedEngine).isPresent();
        assertThat(updatedEngine.get().getName()).isEqualTo("Гибридный 1.8");
    }

    /**
     * Тестирует удаление двигателя автомобиля из репозитория.
     * Проверяет, что после удаления двигатель больше не находится в хранилище
     * при поиске по идентификатору.
     */
    @Test
    void whenDeleteEngineThenRepositoryDoesNotHaveEngine() {
        Engine engine = createTestEngine("Электрический");
        Engine savedEngine = engineRepository.create(engine);

        engineRepository.delete(Math.toIntExact(savedEngine.getId()));

        Optional<Engine> deletedEngine = engineRepository.findById(Math.toIntExact(savedEngine.getId()));
        assertThat(deletedEngine).isNotPresent();
    }

    /**
     * Тестирует получение всех двигателей автомобилей с сортировкой по идентификатору.
     * Проверяет, что возвращаются все сохраненные двигатели в правильном порядке,
     * отсортированные по возрастанию идентификатора.
     */
    @Test
    void whenFindAllOrderByIdThenReturnsAllEnginesSortedById() {
        Engine engine1 = createTestEngine("V6 3.0");
        Engine engine2 = createTestEngine("V8 4.0");
        Engine savedEngine1 = engineRepository.create(engine1);
        Engine savedEngine2 = engineRepository.create(engine2);

        List<Engine> allEngines = engineRepository.findAllOrderById();

        assertThat(allEngines).hasSizeGreaterThanOrEqualTo(2);
        assertThat(allEngines.get(0).getId()).isLessThan(allEngines.get(1).getId());
    }

    /**
     * Тестирует поиск двигателя автомобиля по несуществующему идентификатору.
     * Проверяет, что для несуществующего идентификатора возвращается пустой Optional,
     * что соответствует ожидаемому поведению при отсутствии данных.
     */
    @Test
    void whenFindByNonExistentIdThenReturnsEmptyOptional() {
        Optional<Engine> foundEngine = engineRepository.findById(-1);
        assertThat(foundEngine).isNotPresent();
    }

    /**
     * Тестирует поиск двигателя автомобиля по нулевому идентификатору.
     * Проверяет граничное условие - возвращается пустой Optional для идентификатора 0,
     * так как в системе идентификаторы обычно начинаются с 1.
     */
    @Test
    void whenFindByIdWithZeroIdThenReturnsEmpty() {
        Optional<Engine> result = engineRepository.findById(0);
        assertThat(result).isNotPresent();
    }

    /**
     * Тестирует удаление несуществующего двигателя автомобиля.
     * Проверяет, что операция удаления для несуществующего идентификатора
     * завершается без генерации исключений, обеспечивая отказоустойчивость.
     */
    @Test
    void whenDeleteNonExistentEngineThenNoException() {
        boolean deletionResult = true;
        try {
            engineRepository.delete(-1);
        } catch (Exception e) {
            deletionResult = false;
        }
        assertThat(deletionResult).isTrue();
    }
}