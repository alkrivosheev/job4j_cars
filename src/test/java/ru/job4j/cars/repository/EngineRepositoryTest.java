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

class EngineRepositoryTest {

    private SessionFactory sessionFactory;
    private CrudRepository crudRepository;
    private EngineRepository engineRepository;

    @BeforeEach
    void setUp() throws Exception {
        this.sessionFactory = TestDatabaseConfig.setupTestEnvironment("db/liquibase_test.properties");
        this.crudRepository = new CrudRepository(sessionFactory);
        this.engineRepository = new EngineRepository(crudRepository);
    }

    @AfterEach
    void tearDown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }

    /**
     * Тестирует метод create.
     * Проверяет, что при добавлении нового двигателя он корректно сохраняется в хранилище
     * и может быть найден по его ID.
     * Ожидаемый результат: двигатель, найденный по ID после добавления, имеет то же имя,
     * что и исходный добавленный двигатель, и у него появляется ID.
     */
    @Test
    void whenCreateNewEngineThenRepositoryHasSameEngine() {
        Engine engine = new Engine();
        engine.setName("Test Engine");

        Engine savedEngine = engineRepository.create(engine);

        assertThat(savedEngine.getId()).isGreaterThan(0);
        assertThat(savedEngine.getName()).isEqualTo(engine.getName());

        var foundEngine = engineRepository.findById(Math.toIntExact(savedEngine.getId()));
        assertThat(foundEngine).isPresent();
        assertThat(foundEngine.get().getName()).isEqualTo(engine.getName());
    }

    /**
     * Тестирует метод update.
     * Проверяет, что при обновлении данных двигателя изменения корректно сохраняются в хранилище.
     * Ожидаемый результат: после обновления двигатель имеет новые данные.
     */
    @Test
    void whenUpdateEngineThenRepositoryHasUpdatedEngine() {
        Engine engine = new Engine();
        engine.setName("Original Name");
        Engine savedEngine = engineRepository.create(engine);

        savedEngine.setName("Updated Name");
        engineRepository.update(savedEngine);

        Optional<Engine> updatedEngine = engineRepository.findById(Math.toIntExact(savedEngine.getId()));
        assertThat(updatedEngine).isPresent();
        assertThat(updatedEngine.get().getName()).isEqualTo("Updated Name");
    }

    /**
     * Тестирует метод delete.
     * Проверяет, что при удалении двигателя он больше не находится в хранилище.
     * Ожидаемый результат: после удаления двигатель отсутствует в хранилище.
     */
    @Test
    void whenDeleteEngineThenRepositoryDoesNotHaveEngine() {
        Engine engine = new Engine();
        engine.setName("Engine to Delete");
        Engine savedEngine = engineRepository.create(engine);

        engineRepository.delete(Math.toIntExact(savedEngine.getId()));

        Optional<Engine> deletedEngine = engineRepository.findById(Math.toIntExact(savedEngine.getId()));
        assertThat(deletedEngine).isNotPresent();
    }

    /**
     * Тестирует метод findAllOrderById.
     * Проверяет, что метод возвращает все двигатели, отсортированные по ID в возрастающем порядке.
     * Ожидаемый результат: список содержит все добавленные двигатели в правильном порядке.
     */
    @Test
    void whenFindAllOrderByIdThenReturnsAllEnginesSortedById() {
        Engine engine1 = new Engine();
        engine1.setName("First Engine");
        Engine engine2 = new Engine();
        engine2.setName("Second Engine");

        Engine savedEngine1 = engineRepository.create(engine1);
        Engine savedEngine2 = engineRepository.create(engine2);

        List<Engine> allEngines = engineRepository.findAllOrderById();

        assertThat(allEngines).hasSize(2);
        assertThat(allEngines.get(0).getId()).isLessThan(allEngines.get(1).getId());
        assertThat(allEngines).extracting(Engine::getName)
                .containsExactly("First Engine", "Second Engine");
    }

    /**
     * Тестирует метод findById.
     * Проверяет, что метод возвращает Optional.empty() когда двигатель с указанным ID не существует.
     * Ожидаемый результат: для несуществующего ID возвращается пустой Optional.
     */
    @Test
    void whenFindByNonExistentIdThenReturnsEmptyOptional() {
        Optional<Engine> foundEngine = engineRepository.findById(-1);

        assertThat(foundEngine).isNotPresent();
    }
}