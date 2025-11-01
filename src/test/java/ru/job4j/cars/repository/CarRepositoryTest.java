package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.testutil.TestDatabaseConfig;
import ru.job4j.cars.testutil.TestRepositoryUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для репозитория автомобилей (CarRepository)
 */
class CarRepositoryTest {

    private SessionFactory sessionFactory;
    private CrudRepository crudRepository;
    private CarRepository carRepository;
    private TestRepositoryUtils testUtils;

    /**
     * Инициализация репозиториев перед каждым тестом.
     */
    @BeforeEach
    void setUp() throws Exception {
        this.sessionFactory = TestDatabaseConfig.setupTestEnvironment("db/liquibase_test.properties");
        this.crudRepository = new CrudRepository(sessionFactory);
        this.carRepository = new CarRepository(crudRepository);
        this.testUtils = new TestRepositoryUtils(sessionFactory, crudRepository);
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
     * Тестирует создание автомобиля.
     */
    @Test
    void whenCreateCarThenCarHasId() {
        Car car = testUtils.createTestCarWithRequiredFields("VIN12345678901234");

        assertThat(car.getId()).isGreaterThan(0);
        Optional<Car> foundCar = carRepository.findById(Math.toIntExact(car.getId()));
        assertThat(foundCar).isPresent();
        assertThat(foundCar.get().getVin()).isEqualTo("VIN12345678901234");
    }

    /**
     * Тестирует обновление автомобиля.
     */
    @Test
    void whenUpdateCarThenChangesSaved() {
        Car car = testUtils.createTestCarWithRequiredFields("VIN12345678901235");

        car.setMileage(15000L);
        car.setCountOwners(2L);
        carRepository.update(car);

        Optional<Car> updatedCar = carRepository.findById(Math.toIntExact(car.getId()));
        assertThat(updatedCar).isPresent();
        assertThat(updatedCar.get().getMileage()).isEqualTo(15000L);
        assertThat(updatedCar.get().getCountOwners()).isEqualTo(2L);
    }

    /**
     * Тестирует удаление автомобиля.
     */
    @Test
    void whenDeleteCarThenCarNotFound() {
        Car car = testUtils.createTestCarWithRequiredFields("VIN12345678901236");

        carRepository.delete(Math.toIntExact(car.getId()));

        Optional<Car> deletedCar = carRepository.findById(Math.toIntExact(car.getId()));
        assertThat(deletedCar).isNotPresent();
    }

    /**
     * Тестирует получение всех автомобилей с сортировкой.
     */
    @Test
    void whenFindAllOrderByIdThenReturnsSortedCars() {
        Car car1 = testUtils.createTestCarWithRequiredFields("VIN001");
        Car car2 = testUtils.createTestCarWithRequiredFields("VIN002");

        List<Car> allCars = carRepository.findAllOrderById();

        assertThat(allCars).hasSizeGreaterThanOrEqualTo(2);
        assertThat(allCars.get(0).getId()).isLessThan(allCars.get(1).getId());
    }

    /**
     * Тестирует поиск автомобиля по ID.
     */
    @Test
    void whenFindByIdThenReturnsCarWithAllRelations() {
        Car car = testUtils.createTestCarWithRequiredFields("VIN12345678901237");

        Optional<Car> foundCar = carRepository.findById(Math.toIntExact(car.getId()));

        assertThat(foundCar).isPresent();
        assertThat(foundCar.get().getVin()).isEqualTo("VIN12345678901237");
        assertThat(foundCar.get().getBrand()).isNotNull();
        assertThat(foundCar.get().getModel()).isNotNull();
        assertThat(foundCar.get().getCategory()).isNotNull();
        assertThat(foundCar.get().getBody()).isNotNull();
        assertThat(foundCar.get().getEngine()).isNotNull();
        assertThat(foundCar.get().getTransmissionType()).isNotNull();
        assertThat(foundCar.get().getDriveType()).isNotNull();
        assertThat(foundCar.get().getCarColor()).isNotNull();
        assertThat(foundCar.get().getFuelType()).isNotNull();
        assertThat(foundCar.get().getWheelSide()).isNotNull();
    }
}