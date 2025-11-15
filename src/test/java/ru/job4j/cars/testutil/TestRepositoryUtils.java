package ru.job4j.cars.testutil;

import org.hibernate.SessionFactory;
import ru.job4j.cars.model.*;
import ru.job4j.cars.repository.CrudRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

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
     * Создает тестовый бренд. Ищет по имени, если не найден - создает.
     *
     * @param name название бренда
     * @return найденный или созданный бренд
     */
    public Brand createTestBrand(String name) {
        // 1. Попробовать найти существующую запись
        String findQuery = "SELECT b FROM Brand b WHERE b.name = :name";
        List<Brand> existingBrands = crudRepository.query(findQuery, Brand.class, Map.of("name", name));

        if (!existingBrands.isEmpty()) {
            // 2. Если найдена, вернуть существующую
            return existingBrands.get(0);
        } else {
            // 3. Если не найдена, создать новую
            Brand brand = new Brand();
            brand.setName(name);
            crudRepository.run(session -> {
                session.persist(brand);
                session.flush(); // Убедиться, что ID сгенерирован
            });
            return brand;
        }
    }

    /**
     * Создает тестовую модель автомобиля. Ищет по имени, если не найдена - создает.
     *
     * @param name название модели
     * @return найденная или созданная модель
     */
    public CarModel createTestModel(String name) {
        String findQuery = "SELECT m FROM CarModel m WHERE m.name = :name";
        List<CarModel> existingModels = crudRepository.query(findQuery, CarModel.class, Map.of("name", name));

        if (!existingModels.isEmpty()) {
            return existingModels.get(0);
        } else {
            CarModel model = new CarModel();
            model.setName(name);
            crudRepository.run(session -> {
                session.persist(model);
                session.flush();
            });
            return model;
        }
    }

    /**
     * Создает тестовую категорию автомобиля. Ищет по имени, если не найдена - создает.
     *
     * @param name название категории
     * @return найденная или созданная категория
     */
    public Category createTestCategory(String name) {
        String findQuery = "SELECT c FROM Category c WHERE c.name = :name";
        List<Category> existingCategories = crudRepository.query(findQuery, Category.class, Map.of("name", name));

        if (!existingCategories.isEmpty()) {
            return existingCategories.get(0);
        } else {
            Category category = new Category();
            category.setName(name);
            crudRepository.run(session -> {
                session.persist(category);
                session.flush();
            });
            return category;
        }
    }

    /**
     * Создает тестовый тип кузова. Ищет по имени, если не найден - создает.
     *
     * @param name название типа кузова
     * @return найденный или созданный тип кузова
     */
    public Body createTestBody(String name) {
        String findQuery = "SELECT b FROM Body b WHERE b.name = :name";
        List<Body> existingBodies = crudRepository.query(findQuery, Body.class, Map.of("name", name));

        if (!existingBodies.isEmpty()) {
            return existingBodies.get(0);
        } else {
            Body body = new Body();
            body.setName(name);
            crudRepository.run(session -> {
                session.persist(body);
                session.flush();
            });
            return body;
        }
    }

    /**
     * Создает тестовый двигатель. Ищет по имени, если не найден - создает.
     *
     * @param name название двигателя
     * @return найденный или созданный двигатель
     */
    public Engine createTestEngine(String name) {
        String findQuery = "SELECT e FROM Engine e WHERE e.name = :name";
        List<Engine> existingEngines = crudRepository.query(findQuery, Engine.class, Map.of("name", name));

        if (!existingEngines.isEmpty()) {
            return existingEngines.get(0);
        } else {
            Engine engine = new Engine();
            engine.setName(name);
            crudRepository.run(session -> {
                session.persist(engine);
                session.flush();
            });
            return engine;
        }
    }

    /**
     * Создает тестовый тип трансмиссии. Ищет по имени, если не найден - создает.
     *
     * @param name название типа трансмиссии
     * @return найденный или созданный тип трансмиссии
     */
    public TransmissionType createTestTransmissionType(String name) {
        String findQuery = "SELECT t FROM TransmissionType t WHERE t.name = :name";
        List<TransmissionType> existingTypes = crudRepository.query(findQuery, TransmissionType.class, Map.of("name", name));

        if (!existingTypes.isEmpty()) {
            return existingTypes.get(0);
        } else {
            TransmissionType transmissionType = new TransmissionType();
            transmissionType.setName(name);
            crudRepository.run(session -> {
                session.persist(transmissionType);
                session.flush();
            });
            return transmissionType;
        }
    }

    /**
     * Создает тестовый тип привода. Ищет по имени, если не найден - создает.
     *
     * @param name название типа привода
     * @return найденный или созданный тип привода
     */
    public DriveType createTestDriveType(String name) {
        String findQuery = "SELECT d FROM DriveType d WHERE d.name = :name";
        List<DriveType> existingTypes = crudRepository.query(findQuery, DriveType.class, Map.of("name", name));

        if (!existingTypes.isEmpty()) {
            return existingTypes.get(0);
        } else {
            DriveType driveType = new DriveType();
            driveType.setName(name);
            crudRepository.run(session -> {
                session.persist(driveType);
                session.flush();
            });
            return driveType;
        }
    }

    /**
     * Создает тестовый цвет автомобиля. Ищет по имени, если не найден - создает.
     *
     * @param name название цвета
     * @return найденный или созданный цвет
     */
    public CarColor createTestCarColor(String name) {
        String findQuery = "SELECT c FROM CarColor c WHERE c.name = :name";
        List<CarColor> existingColors = crudRepository.query(findQuery, CarColor.class, Map.of("name", name));

        if (!existingColors.isEmpty()) {
            return existingColors.get(0);
        } else {
            CarColor carColor = new CarColor();
            carColor.setName(name);
            crudRepository.run(session -> {
                session.persist(carColor);
                session.flush();
            });
            return carColor;
        }
    }

    /**
     * Создает тестовый тип топлива. Ищет по имени, если не найден - создает.
     *
     * @param name название типа топлива
     * @return найденный или созданный тип топлива
     */
    public FuelType createTestFuelType(String name) {
        String findQuery = "SELECT f FROM FuelType f WHERE f.name = :name";
        List<FuelType> existingTypes = crudRepository.query(findQuery, FuelType.class, Map.of("name", name));

        if (!existingTypes.isEmpty()) {
            return existingTypes.get(0);
        } else {
            FuelType fuelType = new FuelType();
            fuelType.setName(name);
            crudRepository.run(session -> {
                session.persist(fuelType);
                session.flush();
            });
            return fuelType;
        }
    }

    /**
     * Создает тестовое расположение руля. Ищет по имени, если не найдено - создает.
     *
     * @param name название расположения руля
     * @return найденное или созданное расположение руля
     */
    public WheelSide createTestWheelSide(String name) {
        String findQuery = "SELECT w FROM WheelSide w WHERE w.name = :name";
        List<WheelSide> existingWheelSides = crudRepository.query(findQuery, WheelSide.class, Map.of("name", name));

        if (!existingWheelSides.isEmpty()) {
            return existingWheelSides.get(0);
        } else {
            WheelSide wheelSide = new WheelSide();
            wheelSide.setName(name);
            crudRepository.run(session -> {
                session.persist(wheelSide);
                session.flush();
            });
            return wheelSide;
        }
    }

    // --- Остальные методы остаются без изменений ---
    // createCarDependencies, createTestCar, createTestCarWithRequiredFields, createTestPost, record CarDependencies, геттеры

    /**
     * Создает все зависимости для автомобиля.
     *
     * @return запись с созданными зависимостями
     */
    public CarDependencies createCarDependencies() {
        return new CarDependencies(
                createTestBrand("Toyota"), // Теперь будет искать "Toyota", если не найдет - создаст
                createTestModel("Camry"),  // Теперь будет искать "Camry", если не найдет - создаст
                createTestCategory("Легковой"), // Теперь будет искать "Легковой", если не найдет - создаст
                createTestBody("Седан"),       // Теперь будет искать "Седан", если не найдет - создаст
                createTestEngine("V8"),        // и т.д.
                createTestTransmissionType("Автомат"),
                createTestDriveType("Передний"),
                createTestCarColor("Красный"),
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
        // Эти методы теперь ищут или создают
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