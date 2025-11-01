package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.CarColor;
import ru.job4j.cars.testutil.TestDatabaseConfig;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для репозитория цветов автомобилей (CarColorRepository)
 */
class CarColorRepositoryTest {

    private SessionFactory sessionFactory;
    private CrudRepository crudRepository;
    private CarColorRepository carColorRepository;

    /**
     * Инициализация репозиториев перед каждым тестом.
     */
    @BeforeEach
    void setUp() throws Exception {
        this.sessionFactory = TestDatabaseConfig.setupTestEnvironment("db/liquibase_test.properties");
        this.crudRepository = new CrudRepository(sessionFactory);
        this.carColorRepository = new CarColorRepository(crudRepository);
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
     * Создает тестовый цвет автомобиля.
     */
    private CarColor createTestCarColor(String name) {
        CarColor carColor = new CarColor();
        carColor.setName(name + "_" + System.currentTimeMillis());
        return carColor;
    }

    /**
     * Тестирует создание нового цвета автомобиля в репозитории.
     * Проверяет, что после сохранения цвет имеет присвоенный идентификатор,
     * имя сохраняется корректно и цвет может быть найден по идентификатору.
     */
    @Test
    void whenCreateNewCarColorThenRepositoryHasSameCarColor() {
        CarColor carColor = createTestCarColor("Красный");
        CarColor savedCarColor = carColorRepository.create(carColor);

        assertThat(savedCarColor.getId()).isGreaterThan(0);
        assertThat(savedCarColor.getName()).isEqualTo(carColor.getName());

        Optional<CarColor> foundCarColor = carColorRepository.findById(Math.toIntExact(savedCarColor.getId()));
        assertThat(foundCarColor).isPresent();
        assertThat(foundCarColor.get().getName()).isEqualTo(carColor.getName());
    }

    /**
     * Тестирует обновление данных цвета автомобиля в репозитории.
     * Проверяет, что изменение имени корректно сохраняется
     * и может быть получено при последующем поиске.
     */
    @Test
    void whenUpdateCarColorThenRepositoryHasUpdatedCarColor() {
        CarColor carColor = createTestCarColor("Синий");
        CarColor savedCarColor = carColorRepository.create(carColor);

        savedCarColor.setName("Голубой");
        carColorRepository.update(savedCarColor);

        Optional<CarColor> updatedCarColor = carColorRepository.findById(Math.toIntExact(savedCarColor.getId()));
        assertThat(updatedCarColor).isPresent();
        assertThat(updatedCarColor.get().getName()).isEqualTo("Голубой");
    }

    /**
     * Тестирует удаление цвета автомобиля из репозитория.
     * Проверяет, что после удаления цвет больше не находится в хранилище
     * при поиске по идентификатору.
     */
    @Test
    void whenDeleteCarColorThenRepositoryDoesNotHaveCarColor() {
        CarColor carColor = createTestCarColor("Зеленый");
        CarColor savedCarColor = carColorRepository.create(carColor);

        carColorRepository.delete(Math.toIntExact(savedCarColor.getId()));

        Optional<CarColor> deletedCarColor = carColorRepository.findById(Math.toIntExact(savedCarColor.getId()));
        assertThat(deletedCarColor).isNotPresent();
    }

    /**
     * Тестирует получение всех цветов автомобилей с сортировкой по идентификатору.
     * Проверяет, что возвращаются все сохраненные цвета в правильном порядке,
     * отсортированные по возрастанию идентификатора.
     */
    @Test
    void whenFindAllOrderByIdThenReturnsAllCarColorsSortedById() {
        CarColor carColor1 = createTestCarColor("Черный");
        CarColor carColor2 = createTestCarColor("Белый");
        CarColor savedCarColor1 = carColorRepository.create(carColor1);
        CarColor savedCarColor2 = carColorRepository.create(carColor2);

        List<CarColor> allCarColors = carColorRepository.findAllOrderById();

        assertThat(allCarColors).hasSizeGreaterThanOrEqualTo(2);
        assertThat(allCarColors.get(0).getId()).isLessThan(allCarColors.get(1).getId());
    }

    /**
     * Тестирует поиск цвета автомобиля по несуществующему идентификатору.
     * Проверяет, что для несуществующего идентификатора возвращается пустой Optional,
     * что соответствует ожидаемому поведению при отсутствии данных.
     */
    @Test
    void whenFindByNonExistentIdThenReturnsEmptyOptional() {
        Optional<CarColor> foundCarColor = carColorRepository.findById(-1);
        assertThat(foundCarColor).isNotPresent();
    }

    /**
     * Тестирует поиск цвета автомобиля по нулевому идентификатору.
     * Проверяет граничное условие - возвращается пустой Optional для идентификатора 0,
     * так как в системе идентификаторы обычно начинаются с 1.
     */
    @Test
    void whenFindByIdWithZeroIdThenReturnsEmpty() {
        Optional<CarColor> result = carColorRepository.findById(0);
        assertThat(result).isNotPresent();
    }

    /**
     * Тестирует удаление несуществующего цвета автомобиля.
     * Проверяет, что операция удаления для несуществующего идентификатора
     * завершается без генерации исключений, обеспечивая отказоустойчивость.
     */
    @Test
    void whenDeleteNonExistentCarColorThenNoException() {
        boolean deletionResult = true;
        try {
            carColorRepository.delete(-1);
        } catch (Exception e) {
            deletionResult = false;
        }
        assertThat(deletionResult).isTrue();
    }
}