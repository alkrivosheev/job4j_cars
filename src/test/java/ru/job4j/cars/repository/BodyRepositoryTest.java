package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Body;
import ru.job4j.cars.testutil.TestDatabaseConfig;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для репозитория типов кузова автомобилей (BodyRepository)
 */
class BodyRepositoryTest {

    private SessionFactory sessionFactory;
    private CrudRepository crudRepository;
    private BodyRepository bodyRepository;

    /**
     * Инициализация репозиториев перед каждым тестом.
     */
    @BeforeEach
    void setUp() throws Exception {
        this.sessionFactory = TestDatabaseConfig.setupTestEnvironment("db/liquibase_test.properties");
        this.crudRepository = new CrudRepository(sessionFactory);
        this.bodyRepository = new BodyRepository(crudRepository);
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
     * Создает тестовый тип кузова автомобиля.
     */
    private Body createTestBody(String name) {
        Body body = new Body();
        body.setName(name + "_" + System.currentTimeMillis());
        return body;
    }

    /**
     * Тестирует создание нового типа кузова в репозитории.
     * Проверяет, что после сохранения тип кузова имеет присвоенный идентификатор,
     * имя сохраняется корректно и тип кузова может быть найден по идентификатору.
     */
    @Test
    void whenCreateNewBodyThenRepositoryHasSameBody() {
        Body body = createTestBody("Седан");
        Body savedBody = bodyRepository.create(body);

        assertThat(savedBody.getId()).isGreaterThan(0);
        assertThat(savedBody.getName()).isEqualTo(body.getName());

        Optional<Body> foundBody = bodyRepository.findById(Math.toIntExact(savedBody.getId()));
        assertThat(foundBody).isPresent();
        assertThat(foundBody.get().getName()).isEqualTo(body.getName());
    }

    /**
     * Тестирует обновление данных типа кузова в репозитории.
     * Проверяет, что изменение имени корректно сохраняется
     * и может быть получено при последующем поиске.
     */
    @Test
    void whenUpdateBodyThenRepositoryHasUpdatedBody() {
        Body body = createTestBody("Хэтчбек");
        Body savedBody = bodyRepository.create(body);

        savedBody.setName("Универсал");
        bodyRepository.update(savedBody);

        Optional<Body> updatedBody = bodyRepository.findById(Math.toIntExact(savedBody.getId()));
        assertThat(updatedBody).isPresent();
        assertThat(updatedBody.get().getName()).isEqualTo("Универсал");
    }

    /**
     * Тестирует удаление типа кузова из репозитория.
     * Проверяет, что после удаления тип кузова больше не находится в хранилище
     * при поиске по идентификатору.
     */
    @Test
    void whenDeleteBodyThenRepositoryDoesNotHaveBody() {
        Body body = createTestBody("Купе");
        Body savedBody = bodyRepository.create(body);

        bodyRepository.delete(Math.toIntExact(savedBody.getId()));

        Optional<Body> deletedBody = bodyRepository.findById(Math.toIntExact(savedBody.getId()));
        assertThat(deletedBody).isNotPresent();
    }

    /**
     * Тестирует получение всех типов кузова с сортировкой по идентификатору.
     * Проверяет, что возвращаются все сохраненные типы кузова в правильном порядке,
     * отсортированные по возрастанию идентификатора.
     */
    @Test
    void whenFindAllOrderByIdThenReturnsAllBodiesSortedById() {
        Body body1 = createTestBody("Седан");
        Body body2 = createTestBody("Внедорожник");
        Body savedBody1 = bodyRepository.create(body1);
        Body savedBody2 = bodyRepository.create(body2);

        List<Body> allBodies = bodyRepository.findAllOrderById();

        assertThat(allBodies).hasSizeGreaterThanOrEqualTo(2);
        assertThat(allBodies.get(0).getId()).isLessThan(allBodies.get(1).getId());
    }

    /**
     * Тестирует поиск типа кузова по несуществующему идентификатору.
     * Проверяет, что для несуществующего идентификатора возвращается пустой Optional,
     * что соответствует ожидаемому поведению при отсутствии данных.
     */
    @Test
    void whenFindByNonExistentIdThenReturnsEmptyOptional() {
        Optional<Body> foundBody = bodyRepository.findById(-1);
        assertThat(foundBody).isNotPresent();
    }

    /**
     * Тестирует поиск типа кузова по нулевому идентификатору.
     * Проверяет граничное условие - возвращается пустой Optional для идентификатора 0,
     * так как в системе идентификаторы обычно начинаются с 1.
     */
    @Test
    void whenFindByIdWithZeroIdThenReturnsEmpty() {
        Optional<Body> result = bodyRepository.findById(0);
        assertThat(result).isNotPresent();
    }
}