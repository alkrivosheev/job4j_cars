package ru.job4j.cars.testutil;

import org.hibernate.SessionFactory;
import ru.job4j.cars.model.*;
import ru.job4j.cars.repository.CrudRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Утилитарный класс для тестирования репозиториев.
 * Содержит общие методы для инициализации тестовой среды и создания тестовых данных.
 */
public class TestRepositoryUtils {

    private final SessionFactory sessionFactory;
    private final CrudRepository crudRepository;

    /**
     * Конструктор утилитарного класса.
     *
     * @param sessionFactory фабрика сессий Hibernate
     * @param crudRepository репозиторий для выполнения CRUD операций
     */
    public TestRepositoryUtils(SessionFactory sessionFactory, CrudRepository crudRepository) {
        this.sessionFactory = sessionFactory;
        this.crudRepository = crudRepository;
    }

    /**
     * Создает тестового пользователя с уникальными данными.
     *
     * @param username базовое имя пользователя для генерации уникального логина
     * @return созданный пользователь
     */
    public User createTestUser(String username) {
        User user = new User();
        user.setLogin(username + "_" + System.currentTimeMillis());
        user.setPassword("password");
        user.setName("testUser");
        crudRepository.run(session -> session.persist(user));
        return user;
    }

    /**
     * Создает тестового пользователя с указанными параметрами.
     *
     * @param name     имя пользователя
     * @param login    логин пользователя
     * @param password пароль пользователя
     * @return созданный пользователь
     */
    public User createTestUser(String name, String login, String password) {
        User user = new User();
        user.setName(name + "_" + System.currentTimeMillis());
        user.setLogin(login + "_" + System.currentTimeMillis());
        user.setPassword(password);
        crudRepository.run(session -> session.persist(user));
        return user;
    }

    /**
     * Создает тестовый бренд с уникальным именем.
     *
     * @param name название бренда
     * @return созданный бренд
     */
    public Brand createTestBrand(String name) {
        Brand brand = new Brand();
        brand.setName(name + "_" + System.currentTimeMillis());
        crudRepository.run(session -> session.persist(brand));
        return brand;
    }

    /**
     * Создает тестовую модель автомобиля.
     *
     * @param name название модели
     * @return созданная модель
     */
    public CarModel createTestModel(String name) {
        CarModel model = new CarModel();
        model.setName(name + "_" + System.currentTimeMillis());
        crudRepository.run(session -> session.persist(model));
        return model;
    }

    /**
     * Создает тестовую категорию автомобиля.
     *
     * @param name название категории
     * @return созданная категория
     */
    public Category createTestCategory(String name) {
        Category category = new Category();
        category.setName(name);
        crudRepository.run(session -> session.persist(category));
        return category;
    }

    /**
     * Создает тестовый тип кузова.
     *
     * @param name название типа кузова
     * @return созданный тип кузова
     */
    public Body createTestBody(String name) {
        Body body = new Body();
        body.setName(name);
        crudRepository.run(session -> session.persist(body));
        return body;
    }

    /**
     * Создает тестовый двигатель.
     *
     * @param name название двигателя
     * @return созданный двигатель
     */
    public Engine createTestEngine(String name) {
        Engine engine = new Engine();
        engine.setName(name);
        crudRepository.run(session -> session.persist(engine));
        return engine;
    }

    /**
     * Создает тестовый тип трансмиссии.
     *
     * @param name название типа трансмиссии
     * @return созданный тип трансмиссии
     */
    public TransmissionType createTestTransmissionType(String name) {
        TransmissionType transmissionType = new TransmissionType();
        transmissionType.setName(name);
        crudRepository.run(session -> session.persist(transmissionType));
        return transmissionType;
    }

    /**
     * Создает тестовый тип привода.
     *
     * @param name название типа привода
     * @return созданный тип привода
     */
    public DriveType createTestDriveType(String name) {
        DriveType driveType = new DriveType();
        driveType.setName(name);
        crudRepository.run(session -> session.persist(driveType));
        return driveType;
    }

    /**
     * Создает тестовый цвет автомобиля.
     *
     * @param name название цвета
     * @return созданный цвет
     */
    public CarColor createTestCarColor(String name) {
        CarColor carColor = new CarColor();
        carColor.setName(name);
        crudRepository.run(session -> session.persist(carColor));
        return carColor;
    }

    /**
     * Создает тестовый тип топлива.
     *
     * @param name название типа топлива
     * @return созданный тип топлива
     */
    public FuelType createTestFuelType(String name) {
        FuelType fuelType = new FuelType();
        fuelType.setName(name);
        crudRepository.run(session -> session.persist(fuelType));
        return fuelType;
    }

    /**
     * Создает тестовое расположение руля.
     *
     * @param name название расположения руля
     * @return созданное расположение руля
     */
    public WheelSide createTestWheelSide(String name) {
        WheelSide wheelSide = new WheelSide();
        wheelSide.setName(name);
        crudRepository.run(session -> session.persist(wheelSide));
        return wheelSide;
    }

    /**
     * Создает все зависимости для автомобиля.
     *
     * @return запись с созданными зависимостями
     */
    public CarDependencies createCarDependencies() {
        return new CarDependencies(
                createTestBrand("TestBrand"),
                createTestModel("TestModel"),
                createTestCategory("Легковой"),
                createTestBody("Седан"),
                createTestEngine("V6"),
                createTestTransmissionType("Автомат"),
                createTestDriveType("Передний"),
                createTestCarColor("Черный"),
                createTestFuelType("Бензин"),
                createTestWheelSide("Левый")
        );
    }

    /**
     * Создает тестовый автомобиль с указанным VIN.
     *
     * @param vin VIN автомобиля
     * @return созданный автомобиль
     */
    public Car createTestCar(String vin) {
        CarDependencies dependencies = createCarDependencies();

        if (vin.length() > 17) {
            vin = vin.substring(0, 17);
        }

        Car car = new Car();
        car.setVin(vin);
        car.setMileage(10000L);
        car.setYearOfManufacture(2020L);
        car.setCountOwners(1L);
        car.setBrand(dependencies.brand());
        car.setModel(dependencies.model());
        car.setCategory(dependencies.category());
        car.setBody(dependencies.body());
        car.setEngine(dependencies.engine());
        car.setTransmissionType(dependencies.transmissionType());
        car.setDriveType(dependencies.driveType());
        car.setCarColor(dependencies.carColor());
        car.setFuelType(dependencies.fuelType());
        car.setWheelSide(dependencies.wheelSide());

        crudRepository.run(session -> session.persist(car));
        return car;
    }

    /**
     * Создает тестовый автомобиль с минимально необходимыми полями.
     *
     * @param vin VIN автомобиля
     * @return созданный автомобиль
     */
    public Car createTestCarWithRequiredFields(String vin) {
        Brand brand = createTestBrand("TestBrand");
        CarModel model = createTestModel("TestModel");
        Category category = createTestCategory("Легковой");
        Body body = createTestBody("Седан");
        Engine engine = createTestEngine("V6");
        TransmissionType transmissionType = createTestTransmissionType("Автомат");
        DriveType driveType = createTestDriveType("Передний");
        CarColor carColor = createTestCarColor("Черный");
        FuelType fuelType = createTestFuelType("Бензин");
        WheelSide wheelSide = createTestWheelSide("Левый");

        if (vin.length() > 17) {
            vin = vin.substring(0, 17);
        }

        Car car = new Car();
        car.setVin(vin);
        car.setMileage(10000L);
        car.setYearOfManufacture(2020L);
        car.setCountOwners(1L);
        car.setBrand(brand);
        car.setModel(model);
        car.setCategory(category);
        car.setBody(body);
        car.setEngine(engine);
        car.setTransmissionType(transmissionType);
        car.setDriveType(driveType);
        car.setCarColor(carColor);
        car.setFuelType(fuelType);
        car.setWheelSide(wheelSide);

        crudRepository.run(session -> session.persist(car));
        return car;
    }

    /**
     * Создает тестовое объявление.
     *
     * @param user   пользователь-владелец
     * @param car    автомобиль
     * @param status статус объявления
     * @param price  цена
     * @return созданное объявление
     */
    public Post createTestPost(User user, Car car, String status, BigDecimal price) {
        Post post = new Post();
        post.setStatus(status);
        post.setDescription("Test description");
        post.setCreatedAt(LocalDateTime.now());
        post.setPrice(price);
        post.setCar(car);
        post.setUser(user);
        return post;
    }

    /**
     * Запись для хранения зависимостей автомобиля.
     */
    public record CarDependencies(
            Brand brand,
            CarModel model,
            Category category,
            Body body,
            Engine engine,
            TransmissionType transmissionType,
            DriveType driveType,
            CarColor carColor,
            FuelType fuelType,
            WheelSide wheelSide
    ) { }

    /**
     * Возвращает CrudRepository.
     *
     * @return CrudRepository
     */
    public CrudRepository getCrudRepository() {
        return crudRepository;
    }

    /**
     * Возвращает SessionFactory.
     *
     * @return SessionFactory
     */
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}