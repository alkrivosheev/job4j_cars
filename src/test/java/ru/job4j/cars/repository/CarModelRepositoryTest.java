package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.CarModel;
import ru.job4j.cars.testutil.TestDatabaseConfig;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для репозитория моделей автомобилей (CarModelRepository)
 */
class CarModelRepositoryTest {

    private SessionFactory sessionFactory;
    private CrudRepository crudRepository;
    private CarModelRepository carModelRepository;

    /**
     * Инициализация репозиториев перед каждым тестом.
     */
    @BeforeEach
    void setUp() throws Exception {
        this.sessionFactory = TestDatabaseConfig.setupTestEnvironment("db/liquibase_test.properties");
        this.crudRepository = new CrudRepository(sessionFactory);
        this.carModelRepository = new CarModelRepository(crudRepository);
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
     * Создает тестовую модель автомобиля.
     */
    private CarModel createTestCarModel(String name) {
        CarModel carModel = new CarModel();
        carModel.setName(name + "_" + System.currentTimeMillis());
        return carModel;
    }

    /**
     * Тестирует создание новой модели автомобиля в репозитории.
     * Проверяет, что после сохранения модель имеет присвоенный идентификатор,
     * имя сохраняется корректно и модель может быть найдена по идентификатору.
     */
    @Test
    void whenCreateNewCarModelThenRepositoryHasSameCarModel() {
        CarModel carModel = createTestCarModel("Camry");
        CarModel savedCarModel = carModelRepository.create(carModel);

        assertThat(savedCarModel.getId()).isGreaterThan(0);
        assertThat(savedCarModel.getName()).isEqualTo(carModel.getName());

        Optional<CarModel> foundCarModel = carModelRepository.findById(Math.toIntExact(savedCarModel.getId()));
        assertThat(foundCarModel).isPresent();
        assertThat(foundCarModel.get().getName()).isEqualTo(carModel.getName());
    }

    /**
     * Тестирует обновление данных модели автомобиля в репозитории.
     * Проверяет, что изменение имени корректно сохраняется
     * и может быть получено при последующем поиске.
     */
    @Test
    void whenUpdateCarModelThenRepositoryHasUpdatedCarModel() {
        CarModel carModel = createTestCarModel("Corolla");
        CarModel savedCarModel = carModelRepository.create(carModel);

        savedCarModel.setName("RAV4");
        carModelRepository.update(savedCarModel);

        Optional<CarModel> updatedCarModel = carModelRepository.findById(Math.toIntExact(savedCarModel.getId()));
        assertThat(updatedCarModel).isPresent();
        assertThat(updatedCarModel.get().getName()).isEqualTo("RAV4");
    }

    /**
     * Тестирует удаление модели автомобиля из репозитория.
     * Проверяет, что после удаления модель больше не находится в хранилище
     * при поиске по идентификатору.
     */
    @Test
    void whenDeleteCarModelThenRepositoryDoesNotHaveCarModel() {
        CarModel carModel = createTestCarModel("Civic");
        CarModel savedCarModel = carModelRepository.create(carModel);

        carModelRepository.delete(Math.toIntExact(savedCarModel.getId()));

        Optional<CarModel> deletedCarModel = carModelRepository.findById(Math.toIntExact(savedCarModel.getId()));
        assertThat(deletedCarModel).isNotPresent();
    }

    /**
     * Тестирует получение всех моделей автомобилей с сортировкой по идентификатору.
     * Проверяет, что возвращаются все сохраненные модели в правильном порядке,
     * отсортированные по возрастанию идентификатора.
     */
    @Test
    void whenFindAllOrderByIdThenReturnsAllCarModelsSortedById() {
        CarModel carModel1 = createTestCarModel("X5");
        CarModel carModel2 = createTestCarModel("A4");
        CarModel savedCarModel1 = carModelRepository.create(carModel1);
        CarModel savedCarModel2 = carModelRepository.create(carModel2);

        List<CarModel> allCarModels = carModelRepository.findAllOrderById();

        assertThat(allCarModels).hasSizeGreaterThanOrEqualTo(2);
        assertThat(allCarModels.get(0).getId()).isLessThan(allCarModels.get(1).getId());
    }

    /**
     * Тестирует поиск модели автомобиля по несуществующему идентификатору.
     * Проверяет, что для несуществующего идентификатора возвращается пустой Optional,
     * что соответствует ожидаемому поведению при отсутствии данных.
     */
    @Test
    void whenFindByNonExistentIdThenReturnsEmptyOptional() {
        Optional<CarModel> foundCarModel = carModelRepository.findById(-1);
        assertThat(foundCarModel).isNotPresent();
    }

    /**
     * Тестирует поиск модели автомобиля по нулевому идентификатору.
     * Проверяет граничное условие - возвращается пустой Optional для идентификатора 0,
     * так как в системе идентификаторы обычно начинаются с 1.
     */
    @Test
    void whenFindByIdWithZeroIdThenReturnsEmpty() {
        Optional<CarModel> result = carModelRepository.findById(0);
        assertThat(result).isNotPresent();
    }

    /**
     * Тестирует удаление несуществующей модели автомобиля.
     * Проверяет, что операция удаления для несуществующего идентификатора
     * завершается без генерации исключений, обеспечивая отказоустойчивость.
     */
    @Test
    void whenDeleteNonExistentCarModelThenNoException() {
        boolean deletionResult = true;
        try {
            carModelRepository.delete(-1);
        } catch (Exception e) {
            deletionResult = false;
        }
        assertThat(deletionResult).isTrue();
    }
}