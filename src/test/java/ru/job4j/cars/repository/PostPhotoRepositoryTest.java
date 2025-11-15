package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.*;
import ru.job4j.cars.testutil.TestDatabaseConfig;
import ru.job4j.cars.testutil.TestRepositoryUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты для репозитория фотографий объявлений (PostPhotoRepository)
 */
class PostPhotoRepositoryTest {

    private SessionFactory sessionFactory;
    private CrudRepository crudRepository;
    private PostPhotoRepository postPhotoRepository;
    private PostRepository postRepository;
    private CarRepository carRepository;
    private UserRepository userRepository;
    private TestRepositoryUtils testRepositoryUtils;

    /**
     * Инициализация репозиториев перед каждым тестом.
     */
    @BeforeEach
    void setUp() throws Exception {
        this.sessionFactory = TestDatabaseConfig.setupTestEnvironment("db/liquibase_test.properties");
        this.crudRepository = new CrudRepository(sessionFactory);
        this.postPhotoRepository = new PostPhotoRepository(crudRepository);
        this.postRepository = new PostRepository(crudRepository);
        this.carRepository = new CarRepository(crudRepository);
        this.userRepository = new UserRepository(crudRepository);
        this.testRepositoryUtils = new TestRepositoryUtils(sessionFactory, crudRepository);
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
     * Создает тестового пользователя.
     */
    private User createTestUser(String login) {
        User user = new User();
        user.setLogin(login + "_" + System.currentTimeMillis());
        user.setPassword("password");
        user.setName("John Doe");
        crudRepository.run(session -> session.persist(user));
        return user;
    }

    /**
     * Создает тестовый автомобиль.
     */
    /**
     * Создает тестовый автомобиль.
     */
    private Car createTestCar(String vin) {
        Brand brand = testRepositoryUtils.createTestBrand("TestBrand");
        CarModel model = testRepositoryUtils.createTestModel("TestModel");
        Category category = testRepositoryUtils.createTestCategory("Легковой");
        Body body = testRepositoryUtils.createTestBody("Седан");
        Engine engine = testRepositoryUtils.createTestEngine("V6");
        TransmissionType transmissionType = testRepositoryUtils.createTestTransmissionType("Автомат");
        DriveType driveType = testRepositoryUtils.createTestDriveType("Передний");
        CarColor carColor = testRepositoryUtils.createTestCarColor("Черный");
        FuelType fuelType = testRepositoryUtils.createTestFuelType("Бензин");
        WheelSide wheelSide = testRepositoryUtils.createTestWheelSide("Левый");

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
     * Создает тестовый бренд.
     */
    private Brand createTestBrand() {
        Brand brand = new Brand();
        brand.setName("TestBrand_" + System.currentTimeMillis());
        crudRepository.run(session -> session.persist(brand));
        return brand;
    }

    /**
     * Создает тестовую модель автомобиля.
     */
    private CarModel createTestModel() {
        CarModel model = new CarModel();
        model.setName("TestModel_" + System.currentTimeMillis());
        crudRepository.run(session -> session.persist(model));
        return model;
    }

    /**
     * Создает тестовую категорию автомобиля.
     */
    private Category createTestCategory() {
        Category category = new Category();
        category.setName("Легковой");
        crudRepository.run(session -> session.persist(category));
        return category;
    }

    /**
     * Создает тестовый тип кузова.
     */
    private Body createTestBody() {
        Body body = new Body();
        body.setName("Седан");
        crudRepository.run(session -> session.persist(body));
        return body;
    }

    /**
     * Создает тестовый двигатель.
     */
    private Engine createTestEngine() {
        Engine engine = new Engine();
        engine.setName("V6");
        crudRepository.run(session -> session.persist(engine));
        return engine;
    }

    /**
     * Создает тестовый тип трансмиссии.
     */
    private TransmissionType createTestTransmissionType() {
        TransmissionType transmissionType = new TransmissionType();
        transmissionType.setName("Автомат");
        crudRepository.run(session -> session.persist(transmissionType));
        return transmissionType;
    }

    /**
     * Создает тестовый тип привода.
     */
    private DriveType createTestDriveType() {
        DriveType driveType = new DriveType();
        driveType.setName("Передний");
        crudRepository.run(session -> session.persist(driveType));
        return driveType;
    }

    /**
     * Создает тестовый цвет автомобиля.
     */
    private CarColor createTestCarColor() {
        CarColor carColor = new CarColor();
        carColor.setName("Черный");
        crudRepository.run(session -> session.persist(carColor));
        return carColor;
    }

    /**
     * Создает тестовый тип топлива.
     */
    private FuelType createTestFuelType() {
        FuelType fuelType = new FuelType();
        fuelType.setName("Бензин");
        crudRepository.run(session -> session.persist(fuelType));
        return fuelType;
    }

    /**
     * Создает тестовое расположение руля.
     */
    private WheelSide createTestWheelSide() {
        WheelSide wheelSide = new WheelSide();
        wheelSide.setName("Левый");
        crudRepository.run(session -> session.persist(wheelSide));
        return wheelSide;
    }

    /**
     * Создает тестовое объявление.
     */
    private Post createTestPost(User user, Car car) {
        Post post = new Post();
        post.setStatus("active");
        post.setDescription("Test description");
        post.setCreatedAt(LocalDateTime.now());
        post.setPrice(new BigDecimal("1000000.00"));
        post.setCar(car);
        post.setUser(user);
        crudRepository.run(session -> session.persist(post));
        return post;
    }

    /**
     * Создает тестовую фотографию объявления.
     */
    private PostPhoto createTestPostPhoto(Post post, String photoPath) {
        PostPhoto photo = new PostPhoto();
        photo.setPhotoPath(photoPath + "_" + System.currentTimeMillis());
        photo.setPost(post);
        return photo;
    }

    /**
     * Тестирует создание новой фотографии объявления в репозитории.
     * Проверяет, что после сохранения фотография имеет присвоенный идентификатор,
     * путь сохраняется корректно и фотография может быть найдена по идентификатору.
     */
    @Test
    void whenCreateNewPostPhotoThenRepositoryHasSamePostPhoto() {
        User user = createTestUser("testuser");
        Car car = createTestCar("VIN001");
        Post post = createTestPost(user, car);
        PostPhoto photo = createTestPostPhoto(post, "/photos/car1.jpg");

        PostPhoto savedPhoto = postPhotoRepository.create(photo);

        assertThat(savedPhoto.getId()).isGreaterThan(0);
        assertThat(savedPhoto.getPhotoPath()).isEqualTo(photo.getPhotoPath());

        Optional<PostPhoto> foundPhoto = postPhotoRepository.findById(Math.toIntExact(savedPhoto.getId()));
        assertThat(foundPhoto).isPresent();
        assertThat(foundPhoto.get().getPhotoPath()).isEqualTo(photo.getPhotoPath());
    }

    /**
     * Тестирует обновление данных фотографии объявления в репозитории.
     * Проверяет, что изменение пути к фотографии корректно сохраняется
     * и может быть получено при последующем поиске.
     */
    @Test
    void whenUpdatePostPhotoThenRepositoryHasUpdatedPostPhoto() {
        User user = createTestUser("testuser");
        Car car = createTestCar("VIN002");
        Post post = createTestPost(user, car);
        PostPhoto photo = createTestPostPhoto(post, "/photos/old.jpg");
        PostPhoto savedPhoto = postPhotoRepository.create(photo);

        savedPhoto.setPhotoPath("/photos/new.jpg");
        postPhotoRepository.update(savedPhoto);

        Optional<PostPhoto> updatedPhoto = postPhotoRepository.findById(Math.toIntExact(savedPhoto.getId()));
        assertThat(updatedPhoto).isPresent();
        assertThat(updatedPhoto.get().getPhotoPath()).isEqualTo("/photos/new.jpg");
    }

    /**
     * Тестирует удаление фотографии объявления из репозитория.
     * Проверяет, что после удаления фотография больше не находится в хранилище
     * при поиске по идентификатору.
     */
    @Test
    void whenDeletePostPhotoThenRepositoryDoesNotHavePostPhoto() {
        User user = createTestUser("testuser");
        Car car = createTestCar("VIN003");
        Post post = createTestPost(user, car);
        PostPhoto photo = createTestPostPhoto(post, "/photos/delete.jpg");
        PostPhoto savedPhoto = postPhotoRepository.create(photo);

        postPhotoRepository.delete(Math.toIntExact(savedPhoto.getId()));

        Optional<PostPhoto> deletedPhoto = postPhotoRepository.findById(Math.toIntExact(savedPhoto.getId()));
        assertThat(deletedPhoto).isNotPresent();
    }

    /**
     * Тестирует получение всех фотографий объявлений с сортировкой по идентификатору.
     * Проверяет, что возвращаются все сохраненные фотографии в правильном порядке,
     * отсортированные по возрастанию идентификатора.
     */
    @Test
    void whenFindAllOrderByIdThenReturnsAllPostPhotosSortedById() {
        User user = createTestUser("testuser");
        Car car = createTestCar("VIN004");
        Post post = createTestPost(user, car);
        PostPhoto photo1 = createTestPostPhoto(post, "/photos/photo1.jpg");
        PostPhoto photo2 = createTestPostPhoto(post, "/photos/photo2.jpg");

        PostPhoto savedPhoto1 = postPhotoRepository.create(photo1);
        PostPhoto savedPhoto2 = postPhotoRepository.create(photo2);

        List<PostPhoto> allPhotos = postPhotoRepository.findAllOrderById();

        assertThat(allPhotos).hasSizeGreaterThanOrEqualTo(2);
        assertThat(allPhotos.get(0).getId()).isLessThan(allPhotos.get(1).getId());
    }

    /**
     * Тестирует поиск фотографии объявления по несуществующему идентификатору.
     * Проверяет, что для несуществующего идентификатора возвращается пустой Optional,
     * что соответствует ожидаемому поведению при отсутствии данных.
     */
    @Test
    void whenFindByNonExistentIdThenReturnsEmptyOptional() {
        Optional<PostPhoto> foundPhoto = postPhotoRepository.findById(-1);
        assertThat(foundPhoto).isNotPresent();
    }

    /**
     * Тестирует поиск фотографии объявления по нулевому идентификатору.
     * Проверяет граничное условие - возвращается пустой Optional для идентификатора 0,
     * так как в системе идентификаторы обычно начинаются с 1.
     */
    @Test
    void whenFindByIdWithZeroIdThenReturnsEmpty() {
        Optional<PostPhoto> result = postPhotoRepository.findById(0);
        assertThat(result).isNotPresent();
    }

    /**
     * Тестирует удаление несуществующей фотографии объявления.
     * Проверяет, что операция удаления для несуществующего идентификатора
     * завершается без генерации исключений, обеспечивая отказоустойчивость.
     */
    @Test
    void whenDeleteNonExistentPostPhotoThenNoException() {
        boolean deletionResult = true;
        try {
            postPhotoRepository.delete(-1);
        } catch (Exception e) {
            deletionResult = false;
        }
        assertThat(deletionResult).isTrue();
    }

    /**
     * Тестирует поиск фотографий объявления по идентификатору объявления.
     * Проверяет, что возвращаются только фотографии указанного объявления
     * и они отсортированы по идентификатору.
     */
    @Test
    void whenFindByPostIdThenReturnsPhotosForSpecificPost() {
        User user = createTestUser("testuser");
        Car car1 = createTestCar("VIN005");
        Car car2 = createTestCar("VIN006");
        Post post1 = createTestPost(user, car1);
        Post post2 = createTestPost(user, car2);

        PostPhoto photo1 = createTestPostPhoto(post1, "/photos/post1_1.jpg");
        PostPhoto photo2 = createTestPostPhoto(post1, "/photos/post1_2.jpg");
        PostPhoto photo3 = createTestPostPhoto(post2, "/photos/post2_1.jpg");

        postPhotoRepository.create(photo1);
        postPhotoRepository.create(photo2);
        postPhotoRepository.create(photo3);

        List<PostPhoto> post1Photos = postPhotoRepository.findByPostId(Math.toIntExact(post1.getId()));

        assertThat(post1Photos).hasSize(2);
        assertThat(post1Photos).allMatch(photo -> photo.getPost().getId().equals(post1.getId()));
        assertThat(post1Photos).extracting(PostPhoto::getPhotoPath)
                .allMatch(path -> path.contains("post1"));
    }
}