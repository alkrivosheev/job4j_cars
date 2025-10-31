package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.*;
import ru.job4j.cars.testutil.TestDatabaseConfig;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для репозитория автомобилей (CarRepository)
 */
class CarRepositoryTest {

    private SessionFactory sessionFactory;
    private CrudRepository crudRepository;
    private CarRepository carRepository;

    /**
     * Инициализация репозиториев перед каждым тестом.
     */
    @BeforeEach
    void setUp() throws Exception {
        this.sessionFactory = TestDatabaseConfig.setupTestEnvironment("db/liquibase_test.properties");
        this.crudRepository = new CrudRepository(sessionFactory);
        this.carRepository = new CarRepository(crudRepository);
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
     * Создает тестовый бренд с уникальным именем.
     */
    private Brand createTestBrand(String name) {
        Brand brand = new Brand();
        brand.setName(name + "_" + System.currentTimeMillis());
        crudRepository.run(session -> session.persist(brand));
        return brand;
    }

    /**
     * Создает тестовую модель автомобиля.
     */
    private CarModel createTestModel(String name) {
        CarModel model = new CarModel();
        model.setName(name + "_" + System.currentTimeMillis());
        crudRepository.run(session -> session.persist(model));
        return model;
    }

    /**
     * Создает тестовую категорию автомобиля.
     */
    private Category createTestCategory(String name) {
        Category category = new Category();
        category.setName(name);
        crudRepository.run(session -> session.persist(category));
        return category;
    }

    /**
     * Создает тестовый тип кузова.
     */
    private Body createTestBody(String name) {
        Body body = new Body();
        body.setName(name);
        crudRepository.run(session -> session.persist(body));
        return body;
    }

    /**
     * Создает тестовый двигатель.
     */
    private Engine createTestEngine(String name) {
        Engine engine = new Engine();
        engine.setName(name);
        crudRepository.run(session -> session.persist(engine));
        return engine;
    }

    /**
     * Создает тестовый тип трансмиссии.
     */
    private TransmissionType createTestTransmissionType(String name) {
        TransmissionType transmissionType = new TransmissionType();
        transmissionType.setName(name);
        crudRepository.run(session -> session.persist(transmissionType));
        return transmissionType;
    }

    /**
     * Создает тестовый тип привода.
     */
    private DriveType createTestDriveType(String name) {
        DriveType driveType = new DriveType();
        driveType.setName(name);
        crudRepository.run(session -> session.persist(driveType));
        return driveType;
    }

    /**
     * Создает тестовый цвет автомобиля.
     */
    private CarColor createTestCarColor(String name) {
        CarColor carColor = new CarColor();
        carColor.setName(name);
        crudRepository.run(session -> session.persist(carColor));
        return carColor;
    }

    /**
     * Создает тестовый тип топлива.
     */
    private FuelType createTestFuelType(String name) {
        FuelType fuelType = new FuelType();
        fuelType.setName(name);
        crudRepository.run(session -> session.persist(fuelType));
        return fuelType;
    }

    /**
     * Создает тестовое расположение руля.
     */
    private WheelSide createTestWheelSide(String name) {
        WheelSide wheelSide = new WheelSide();
        wheelSide.setName(name);
        crudRepository.run(session -> session.persist(wheelSide));
        return wheelSide;
    }

    /**
     * Создает автомобиль с минимально необходимыми полями.
     */
    private Car createTestCarWithRequiredFields(String vin) {
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

        return car;
    }

    /**
     * Создает автомобиль с указанным брендом.
     */
    private Car createTestCarWithBrand(String vin, Brand brand) {
        Car car = createTestCarWithRequiredFields(vin);
        car.setBrand(brand);
        return car;
    }

    /**
     * Создает автомобиль с указанным годом выпуска.
     */
    private Car createTestCarWithYear(String vin, Long year) {
        Car car = createTestCarWithRequiredFields(vin);
        car.setYearOfManufacture(year);
        return car;
    }

    /**
     * Создает автомобиль с указанным пробегом.
     */
    private Car createTestCarWithMileage(String vin, Long mileage) {
        Car car = createTestCarWithRequiredFields(vin);
        car.setMileage(mileage);
        return car;
    }

    /**
     * Находит бренд по идентификатору.
     */
    private Optional<Brand> findBrandById(Long id) {
        return crudRepository.optional(
                "FROM Brand WHERE id = :id",
                Brand.class,
                java.util.Map.of("id", id)
        );
    }

    /**
     * Тестирует создание нового автомобиля в репозитории.
     * Проверяет, что после сохранения автомобиль имеет присвоенный идентификатор,
     * VIN номер сохраняется корректно и автомобиль может быть найден по идентификатору.
     */
    @Test
    void whenCreateNewCarThenRepositoryHasSameCar() {
        Car car = createTestCarWithRequiredFields("123456789012345");
        Car savedCar = carRepository.create(car);

        assertThat(savedCar.getId()).isGreaterThan(0);
        assertThat(savedCar.getVin()).isEqualTo(car.getVin());

        Optional<Car> foundCar = carRepository.findById(Math.toIntExact(savedCar.getId()));
        assertThat(foundCar).isPresent();
        assertThat(foundCar.get().getVin()).isEqualTo(car.getVin());
    }

    /**
     * Тестирует обновление данных автомобиля в репозитории.
     * Проверяет, что изменения пробега и количества владельцев корректно сохраняются
     * и могут быть получены при последующем поиске.
     */
    @Test
    void whenUpdateCarThenRepositoryHasUpdatedCar() {
        Car car = createTestCarWithRequiredFields("1HGBH41JXMN109186");
        Car savedCar = carRepository.create(car);

        savedCar.setMileage(15000L);
        savedCar.setCountOwners(2L);
        carRepository.update(savedCar);

        Optional<Car> updatedCar = carRepository.findById(Math.toIntExact(savedCar.getId()));
        assertThat(updatedCar).isPresent();
        assertThat(updatedCar.get().getMileage()).isEqualTo(15000L);
        assertThat(updatedCar.get().getCountOwners()).isEqualTo(2L);
    }

    /**
     * Тестирует удаление автомобиля из репозитория.
     * Проверяет, что после удаления автомобиль больше не находится в хранилище
     * при поиске по идентификатору.
     */
    @Test
    void whenDeleteCarThenRepositoryDoesNotHaveCar() {
        Car car = createTestCarWithRequiredFields("1HGBH41JXMN109186");
        Car savedCar = carRepository.create(car);

        carRepository.delete(Math.toIntExact(savedCar.getId()));

        Optional<Car> deletedCar = carRepository.findById(Math.toIntExact(savedCar.getId()));
        assertThat(deletedCar).isNotPresent();
    }

    /**
     * Тестирует получение всех автомобилей с сортировкой по идентификатору.
     * Проверяет, что возвращаются все сохраненные автомобили в правильном порядке,
     * отсортированные по возрастанию идентификатора.
     */
    @Test
    void whenFindAllOrderByIdThenReturnsAllCarsSortedById() {
        Car car1 = createTestCarWithRequiredFields("VIN001");
        Car car2 = createTestCarWithRequiredFields("VIN002");
        Car savedCar1 = carRepository.create(car1);
        Car savedCar2 = carRepository.create(car2);

        List<Car> allCars = carRepository.findAllOrderById();

        assertThat(allCars).hasSize(2);
        assertThat(allCars.get(0).getId()).isLessThan(allCars.get(1).getId());
        assertThat(allCars).extracting(Car::getVin)
                .containsExactly(savedCar1.getVin(), savedCar2.getVin());
    }

    /**
     * Тестирует поиск автомобиля по несуществующему идентификатору.
     * Проверяет, что для несуществующего идентификатора возвращается пустой Optional,
     * что соответствует ожидаемому поведению при отсутствии данных.
     */
    @Test
    void whenFindByNonExistentIdThenReturnsEmptyOptional() {
        Optional<Car> foundCar = carRepository.findById(-1);
        assertThat(foundCar).isNotPresent();
    }

    /**
     * Тестирует поиск автомобиля по нулевому идентификатору.
     * Проверяет граничное условие - возвращается пустой Optional для идентификатора 0,
     * так как в системе идентификаторы обычно начинаются с 1.
     */
    @Test
    void whenFindByIdWithZeroIdThenReturnsEmpty() {
        Optional<Car> result = carRepository.findById(0);
        assertThat(result).isNotPresent();
    }

    /**
     * Тестирует удаление несуществующего автомобиля.
     * Проверяет, что операция удаления для несуществующего идентификатора
     * завершается без генерации исключений, обеспечивая отказоустойчивость.
     */
    @Test
    void whenDeleteNonExistentCarThenNoException() {
        boolean deletionResult = true;
        try {
            carRepository.delete(-1);
        } catch (Exception e) {
            deletionResult = false;
        }
        assertThat(deletionResult).isTrue();
    }

    /**
     * Тестирует корректность подсчета автомобилей при массовом создании.
     * Проверяет, что количество возвращаемых автомобилей соответствует
     * количеству созданных плюс исходное количество в базе данных.
     */
    @Test
    void whenCreateMultipleCarsThenFindAllReturnsCorrectCount() {
        int initialCount = carRepository.findAllOrderById().size();

        Car car1 = createTestCarWithRequiredFields("VIN_MULTI_1");
        Car car2 = createTestCarWithRequiredFields("VIN_MULTI_2");
        Car car3 = createTestCarWithRequiredFields("VIN_MULTI_3");

        carRepository.create(car1);
        carRepository.create(car2);
        carRepository.create(car3);

        List<Car> allCars = carRepository.findAllOrderById();
        assertThat(allCars).hasSize(initialCount + 3);
    }

    /**
     * Тестирует обновление марки автомобиля.
     * Проверяет, что изменение связанной сущности Brand корректно сохраняется
     * и отражается при последующем поиске автомобиля.
     */
    @Test
    void whenUpdateCarWithNewBrandThenBrandChanges() {
        Brand oldBrand = createTestBrand("OldBrand");
        Brand newBrand = createTestBrand("NewBrand");
        Car car = createTestCarWithBrand("VIN_CHANGE_BRAND", oldBrand);
        Car savedCar = carRepository.create(car);

        savedCar.setBrand(newBrand);
        carRepository.update(savedCar);

        Optional<Car> updatedCar = carRepository.findById(Math.toIntExact(savedCar.getId()));
        assertThat(updatedCar).isPresent();
        assertThat(updatedCar.get().getBrand().getName()).isEqualTo(newBrand.getName());
    }

    /**
     * Тестирует отсутствие каскадного удаления связанных сущностей.
     * Проверяет, что при удалении автомобиля связанные с ним сущности
     * (в данном случае Brand) не удаляются из базы данных.
     */
    @Test
    void whenDeleteCarThenRelatedEntitiesNotDeleted() {
        Brand brand = createTestBrand("SharedBrand");
        Car car = createTestCarWithBrand("VIN_SHARED_BRAND", brand);
        Car savedCar = carRepository.create(car);

        carRepository.delete(Math.toIntExact(savedCar.getId()));

        Optional<Brand> foundBrand = findBrandById(brand.getId());
        assertThat(foundBrand).isPresent();
        assertThat(foundBrand.get().getName()).isEqualTo(brand.getName());
    }

    /**
     * Тестирует поиск автомобилей по марке.
     * Проверяет, что метод возвращает только автомобили указанной марки.
     */
    @Test
    void whenFindByBrandThenReturnsCarsOfThatBrand() {
        Brand bmw = createTestBrand("BMW");
        Brand audi = createTestBrand("Audi");

        Car bmwCar = createTestCarWithBrand("VIN_BMW_001", bmw);
        Car audiCar = createTestCarWithBrand("VIN_AUDI_001", audi);
        Car anotherBmw = createTestCarWithBrand("VIN_BMW_002", bmw);

        carRepository.create(bmwCar);
        carRepository.create(audiCar);
        carRepository.create(anotherBmw);

        List<Car> bmwCars = crudRepository.query(
                "FROM Car c WHERE c.brand = :brand ORDER BY c.id",
                Car.class,
                Map.of("brand", bmw)
        );

        assertThat(bmwCars).hasSize(2);
        assertThat(bmwCars).extracting(Car::getVin)
                .containsExactly("VIN_BMW_001", "VIN_BMW_002");
    }

    /**
     * Тестирует поиск автомобилей по году выпуска в диапазоне.
     * Проверяет, что возвращаются только автомобили с годом выпуска в указанном диапазоне.
     */
    @Test
    void whenFindByYearRangeThenReturnsCarsInRange() {
        Car car2019 = createTestCarWithYear("VIN2019", 2019L);
        Car car2021 = createTestCarWithYear("VIN2021", 2021L);
        Car car2023 = createTestCarWithYear("VIN2023", 2023L);

        carRepository.create(car2019);
        carRepository.create(car2021);
        carRepository.create(car2023);

        List<Car> carsInRange = crudRepository.query(
                "FROM Car c WHERE c.yearOfManufacture BETWEEN :startYear AND :endYear ORDER BY c.id",
                Car.class,
                Map.of("startYear", 2020L, "endYear", 2022L)
        );

        assertThat(carsInRange).hasSize(1);
        assertThat(carsInRange.get(0).getYearOfManufacture()).isEqualTo(2021L);
    }

    /**
     * Тестирует поиск автомобилей с пробегом меньше указанного значения.
     * Проверяет, что возвращаются только автомобили с малым пробегом.
     */
    @Test
    void whenFindByMileageLessThanThenReturnsCarsWithLowMileage() {
        Car lowMileageCar = createTestCarWithMileage("VIN_LOW", 5000L);
        Car mediumMileageCar = createTestCarWithMileage("VIN_MEDIUM", 25000L);
        Car highMileageCar = createTestCarWithMileage("VIN_HIGH", 100000L);

        carRepository.create(lowMileageCar);
        carRepository.create(mediumMileageCar);
        carRepository.create(highMileageCar);

        List<Car> lowMileageCars = crudRepository.query(
                "FROM Car c WHERE c.mileage < :maxMileage ORDER BY c.id",
                Car.class,
                Map.of("maxMileage", 15000L)
        );

        assertThat(lowMileageCars).hasSize(1);
        assertThat(lowMileageCars.get(0).getVin()).isEqualTo("VIN_LOW");
    }

    /**
     * Тестирует поиск автомобилей по модели.
     * Проверяет, что метод возвращает автомобили указанной модели.
     */
    @Test
    void whenFindByModelThenReturnsCarsOfThatModel() {
        CarModel x5Model = createTestModel("X5");
        CarModel a4Model = createTestModel("A4");

        Car bmwX5 = createTestCarWithRequiredFields("X5001");
        bmwX5.setModel(x5Model);

        Car audiA4 = createTestCarWithRequiredFields("A4001");
        audiA4.setModel(a4Model);

        Car anotherX5 = createTestCarWithRequiredFields("X5002");
        anotherX5.setModel(x5Model);

        carRepository.create(bmwX5);
        carRepository.create(audiA4);
        carRepository.create(anotherX5);

        List<Car> x5Cars = crudRepository.query(
                "FROM Car c WHERE c.model = :model ORDER BY c.id",
                Car.class,
                Map.of("model", x5Model)
        );

        assertThat(x5Cars).hasSize(2);
        assertThat(x5Cars).extracting(Car::getVin)
                .containsExactly("X5001", "X5002");
    }

    /**
     * Тестирует поиск автомобилей по цвету.
     * Проверяет, что возвращаются автомобили только указанного цвета.
     */
    @Test
    void whenFindByColorThenReturnsCarsOfThatColor() {
        CarColor redColor = createTestCarColor("Красный");
        CarColor blueColor = createTestCarColor("Синий");

        Car redCar = createTestCarWithRequiredFields("VIN_RED_001");
        redCar.setCarColor(redColor);

        Car blueCar = createTestCarWithRequiredFields("VIN_BLUE_001");
        blueCar.setCarColor(blueColor);

        Car anotherRedCar = createTestCarWithRequiredFields("VIN_RED_002");
        anotherRedCar.setCarColor(redColor);

        carRepository.create(redCar);
        carRepository.create(blueCar);
        carRepository.create(anotherRedCar);

        List<Car> redCars = crudRepository.query(
                "FROM Car c WHERE c.carColor = :color ORDER BY c.id",
                Car.class,
                Map.of("color", redColor)
        );

        assertThat(redCars).hasSize(2);
        assertThat(redCars).extracting(Car::getVin)
                .containsExactly("VIN_RED_001", "VIN_RED_002");
    }

    /**
     * Тестирует поиск автомобилей по типу топлива.
     * Проверяет фильтрацию по типу топлива.
     */
    @Test
    void whenFindByFuelTypeThenReturnsCarsWithThatFuel() {
        FuelType petrol = createTestFuelType("Бензин");
        FuelType diesel = createTestFuelType("Дизель");

        Car petrolCar = createTestCarWithRequiredFields("VIN_PETROL_001");
        petrolCar.setFuelType(petrol);

        Car dieselCar = createTestCarWithRequiredFields("VIN_DIESEL_001");
        dieselCar.setFuelType(diesel);

        Car anotherPetrolCar = createTestCarWithRequiredFields("VIN_PETROL_002");
        anotherPetrolCar.setFuelType(petrol);

        carRepository.create(petrolCar);
        carRepository.create(dieselCar);
        carRepository.create(anotherPetrolCar);

        List<Car> petrolCars = crudRepository.query(
                "FROM Car c WHERE c.fuelType = :fuelType ORDER BY c.id",
                Car.class,
                Map.of("fuelType", petrol)
        );

        assertThat(petrolCars).hasSize(2);
        assertThat(petrolCars).extracting(Car::getVin)
                .containsExactly("VIN_PETROL_001", "VIN_PETROL_002");
    }

    /**
     * Тестирует комбинированный поиск по нескольким параметрам.
     * Проверяет корректность работы сложного запроса с несколькими условиями.
     */
    @Test
    void whenFindByMultipleCriteriaThenReturnsFilteredCars() {
        Brand toyota = createTestBrand("Toyota");
        CarColor white = createTestCarColor("Белый");

        Car targetCar = createTestCarWithBrand("VIN_TARGET", toyota);
        targetCar.setCarColor(white);
        targetCar.setYearOfManufacture(2020L);
        targetCar.setMileage(30000L);

        Car nonTargetCar1 = createTestCarWithBrand("VIN_NONTARGET1", toyota);
        nonTargetCar1.setCarColor(createTestCarColor("Черный"));

        Car nonTargetCar2 = createTestCarWithRequiredFields("VIN_NONTARGET2");
        nonTargetCar2.setCarColor(white);

        carRepository.create(targetCar);
        carRepository.create(nonTargetCar1);
        carRepository.create(nonTargetCar2);

        List<Car> filteredCars = crudRepository.query(
                """
                FROM Car c 
                WHERE c.brand = :brand 
                AND c.carColor = :color 
                AND c.yearOfManufacture = :year
                AND c.mileage <= :maxMileage
                ORDER BY c.id
                """,
                Car.class,
                Map.of(
                        "brand", toyota,
                        "color", white,
                        "year", 2020L,
                        "maxMileage", 50000L
                )
        );

        assertThat(filteredCars).hasSize(1);
        assertThat(filteredCars.get(0).getVin()).isEqualTo("VIN_TARGET");
    }

    /**
     * Тестирует поиск автомобилей с пробегом в указанном диапазоне.
     * Проверяет корректность работы BETWEEN для числового диапазона.
     */
    @Test
    void whenFindByMileageRangeThenReturnsCarsInMileageRange() {
        Car lowMileage = createTestCarWithMileage("VIN_LOW_RANGE", 5000L);
        Car mediumMileage = createTestCarWithMileage("VIN_MEDIUM_RANGE", 50000L);
        Car highMileage = createTestCarWithMileage("VIN_HIGH_RANGE", 150000L);

        carRepository.create(lowMileage);
        carRepository.create(mediumMileage);
        carRepository.create(highMileage);

        List<Car> carsInRange = crudRepository.query(
                "FROM Car c WHERE c.mileage BETWEEN :minMileage AND :maxMileage ORDER BY c.id",
                Car.class,
                Map.of("minMileage", 10000L, "maxMileage", 100000L)
        );

        assertThat(carsInRange).hasSize(1);
        assertThat(carsInRange.get(0).getVin()).isEqualTo("VIN_MEDIUM_RANGE");
        assertThat(carsInRange.get(0).getMileage()).isEqualTo(50000L);
    }

    /**
     * Тестирует создание автомобиля с отрицательным пробегом.
     * Проверяет, что система корректно обрабатывает некорректные данные.
     */
    @Test
    void whenCreateCarWithNegativeMileageThenCarIsCreatedButWithIncorrectData() {
        Car car = createTestCarWithRequiredFields("VIN12345678901234");
        car.setMileage(-100L);

        Car savedCar = carRepository.create(car);

        assertThat(savedCar).isNotNull();
        assertThat(savedCar.getMileage()).isEqualTo(-100L);

        Optional<Car> foundCar = carRepository.findById(Math.toIntExact(savedCar.getId()));
        assertThat(foundCar).isPresent();
        assertThat(foundCar.get().getMileage()).isEqualTo(-100L);
    }

    /**
     * Тестирует создание автомобиля с будущим годом выпуска.
     * Проверяет, что система сохраняет данные без валидации на уровне репозитория.
     */
    @Test
    void whenCreateCarWithFutureYearThenCarIsCreated() {
        Car car = createTestCarWithRequiredFields("VIN12345678901235");
        car.setYearOfManufacture((long) (java.time.Year.now().getValue() + 10));

        Car savedCar = carRepository.create(car);

        assertThat(savedCar).isNotNull();
        assertThat(savedCar.getYearOfManufacture()).isGreaterThan(java.time.Year.now().getValue());

        Optional<Car> foundCar = carRepository.findById(Math.toIntExact(savedCar.getId()));
        assertThat(foundCar).isPresent();
        assertThat(foundCar.get().getYearOfManufacture()).isEqualTo(car.getYearOfManufacture());
    }

    /**
     * Тестирует создание автомобиля с VIN максимальной длины.
     * Проверяет граничное значение для VIN поля.
     */
    @Test
    void whenCreateCarWithMaxLengthVinThenSuccess() {
        Car car = createTestCarWithRequiredFields("12345678901234567"); // 17 символов
        Car savedCar = carRepository.create(car);

        assertThat(savedCar).isNotNull();
        assertThat(savedCar.getVin()).hasSize(17);

        Optional<Car> foundCar = carRepository.findById(Math.toIntExact(savedCar.getId()));
        assertThat(foundCar).isPresent();
        assertThat(foundCar.get().getVin()).isEqualTo("12345678901234567");
    }

    /**
     * Тестирует создание автомобиля с нулевым пробегом.
     * Проверяет граничное значение для пробега.
     */
    @Test
    void whenCreateCarWithZeroMileageThenSuccess() {
        Car car = createTestCarWithRequiredFields("VIN12345678901236");
        car.setMileage(0L);

        Car savedCar = carRepository.create(car);

        assertThat(savedCar).isNotNull();
        assertThat(savedCar.getMileage()).isZero();

        Optional<Car> foundCar = carRepository.findById(Math.toIntExact(savedCar.getId()));
        assertThat(foundCar).isPresent();
        assertThat(foundCar.get().getMileage()).isZero();
    }

    /**
     * Тестирует создание автомобиля с очень большим пробегом.
     * Проверяет обработку больших числовых значений.
     */
    @Test
    void whenCreateCarWithLargeMileageThenSuccess() {
        Car car = createTestCarWithRequiredFields("VIN12345678901237");
        car.setMileage(999999L);

        Car savedCar = carRepository.create(car);

        assertThat(savedCar).isNotNull();
        assertThat(savedCar.getMileage()).isEqualTo(999999L);

        Optional<Car> foundCar = carRepository.findById(Math.toIntExact(savedCar.getId()));
        assertThat(foundCar).isPresent();
        assertThat(foundCar.get().getMileage()).isEqualTo(999999L);
    }
}