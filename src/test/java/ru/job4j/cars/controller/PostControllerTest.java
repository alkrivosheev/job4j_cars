package ru.job4j.cars.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.dto.PostCreationDto;
import ru.job4j.cars.model.*;
import ru.job4j.cars.service.*;
import ru.job4j.cars.testutil.TestDatabaseConfig;
import ru.job4j.cars.testutil.TestRepositoryUtils;
import org.hibernate.SessionFactory;
import ru.job4j.cars.repository.CrudRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class PostControllerTest {
    private PostController postController;
    private PostService postService;
    private CarService carService;
    private BrandService brandService;
    private CarModelService carModelService;
    private CategoryService categoryService;
    private BodyService bodyService;
    private EngineService engineService;
    private TransmissionTypeService transmissionTypeService;
    private DriveTypeService driveTypeService;
    private CarColorService carColorService;
    private FuelTypeService fuelTypeService;
    private WheelSideService wheelSideService;
    private PostPhotoService postPhotoService;
    private SessionFactory sessionFactory;
    private TestRepositoryUtils testUtils;
    private User testUser;

    @BeforeEach
    void setUp() throws Exception {
        sessionFactory = TestDatabaseConfig.setupTestEnvironment("test.properties");
        CrudRepository crudRepository = new CrudRepository(sessionFactory);
        testUtils = new TestRepositoryUtils(sessionFactory, crudRepository);

        postService = mock(PostService.class);
        carService = mock(CarService.class);
        brandService = mock(BrandService.class);
        carModelService = mock(CarModelService.class);
        categoryService = mock(CategoryService.class);
        bodyService = mock(BodyService.class);
        engineService = mock(EngineService.class);
        transmissionTypeService = mock(TransmissionTypeService.class);
        driveTypeService = mock(DriveTypeService.class);
        carColorService = mock(CarColorService.class);
        fuelTypeService = mock(FuelTypeService.class);
        wheelSideService = mock(WheelSideService.class);
        postPhotoService = mock(PostPhotoService.class);

        postController = new PostController(
                postService, carService, brandService, carModelService,
                categoryService, bodyService, engineService, transmissionTypeService,
                driveTypeService, carColorService, fuelTypeService, wheelSideService,
                postPhotoService
        );

        testUser = testUtils.createTestUser("testUser");

        cleanUploadDirectory();
    }

    @AfterEach
    void tearDown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
        cleanUploadDirectory();
    }

    private void cleanUploadDirectory() {
        try {
            Path uploadPath = Paths.get("uploads/images");
            if (Files.exists(uploadPath)) {
                Files.walk(uploadPath)
                        .sorted((a, b) -> -a.compareTo(b))
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                Files.deleteIfExists(uploadPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Тест отображения формы создания объявления.
     */

    @Test
    void showCreateFormShouldReturnViewName() {
        Model model = mock(Model.class);

        List<Brand> brands = List.of(testUtils.createTestBrand("Toyota"));
        List<CarModel> models = List.of(testUtils.createTestModel("Camry"));
        List<Category> categories = List.of(testUtils.createTestCategory("Легковой"));
        List<Body> bodies = List.of(testUtils.createTestBody("Седан"));
        List<Engine> engines = List.of(testUtils.createTestEngine("V6"));
        List<TransmissionType> transmissionTypes = List.of(testUtils.createTestTransmissionType("Автомат"));
        List<DriveType> driveTypes = List.of(testUtils.createTestDriveType("Передний"));
        List<CarColor> carColors = List.of(testUtils.createTestCarColor("Красный"));
        List<FuelType> fuelTypes = List.of(testUtils.createTestFuelType("Бензин"));
        List<WheelSide> wheelSides = List.of(testUtils.createTestWheelSide("Левый"));

        when(brandService.findAllOrderById()).thenReturn(brands);
        when(carModelService.findAllOrderById()).thenReturn(models);
        when(categoryService.findAllOrderById()).thenReturn(categories);
        when(bodyService.findAllOrderById()).thenReturn(bodies);
        when(engineService.findAllOrderById()).thenReturn(engines);
        when(transmissionTypeService.findAllOrderById()).thenReturn(transmissionTypes);
        when(driveTypeService.findAllOrderById()).thenReturn(driveTypes);
        when(carColorService.findAllOrderById()).thenReturn(carColors);
        when(fuelTypeService.findAllOrderById()).thenReturn(fuelTypes);
        when(wheelSideService.findAllOrderById()).thenReturn(wheelSides);

        String viewName = postController.showCreateForm(model);

        assertThat(viewName).isEqualTo("post/createPost");

        verify(model).addAttribute("brands", brands);
        verify(model).addAttribute("models", models);
        verify(model).addAttribute("categories", categories);
        verify(model).addAttribute("bodies", bodies);
        verify(model).addAttribute("engines", engines);
        verify(model).addAttribute("transmissionTypes", transmissionTypes);
        verify(model).addAttribute("driveTypes", driveTypes);
        verify(model).addAttribute("carColors", carColors);
        verify(model).addAttribute("fuelTypes", fuelTypes);
        verify(model).addAttribute("wheelSides", wheelSides);
    }

    /**
     * Тест создания объявления с авторизованным пользователем.
     */
    @Test
    void createPostWithAuthenticatedUserShouldRedirectToHome() throws IOException {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", testUser);

        PostCreationDto dto = createTestPostCreationDto();
        List<MultipartFile> photos = createTestPhotos();
        dto.setPhotos(photos);

        Brand brand = testUtils.createTestBrand("Toyota");
        CarModel model = testUtils.createTestModel("Camry");
        Category category = testUtils.createTestCategory("Легковой");
        Body body = testUtils.createTestBody("Седан");
        Engine engine = testUtils.createTestEngine("V6");
        TransmissionType transmissionType = testUtils.createTestTransmissionType("Автомат");
        DriveType driveType = testUtils.createTestDriveType("Передний");
        CarColor carColor = testUtils.createTestCarColor("Красный");
        FuelType fuelType = testUtils.createTestFuelType("Бензин");
        WheelSide wheelSide = testUtils.createTestWheelSide("Левый");

        when(brandService.findById(1)).thenReturn(java.util.Optional.of(brand));
        when(carModelService.findById(1)).thenReturn(java.util.Optional.of(model));
        when(categoryService.findById(1)).thenReturn(java.util.Optional.of(category));
        when(bodyService.findById(1)).thenReturn(java.util.Optional.of(body));
        when(engineService.findById(1)).thenReturn(java.util.Optional.of(engine));
        when(transmissionTypeService.findById(1)).thenReturn(java.util.Optional.of(transmissionType));
        when(driveTypeService.findById(1)).thenReturn(java.util.Optional.of(driveType));
        when(carColorService.findById(1)).thenReturn(java.util.Optional.of(carColor));
        when(fuelTypeService.findById(1)).thenReturn(java.util.Optional.of(fuelType));
        when(wheelSideService.findById(1)).thenReturn(java.util.Optional.of(wheelSide));

        Car savedCar = testUtils.createTestCar("VIN12345678901234");
        when(carService.create(any(Car.class))).thenReturn(savedCar);

        Post savedPost = testUtils.createTestPost(testUser, savedCar, "active", new BigDecimal("1000000.00"));
        when(postService.create(any(Post.class))).thenReturn(savedPost);

        String redirectUrl = postController.createPost(dto, session);

        assertThat(redirectUrl).isEqualTo("redirect:/");

        verify(carService).create(any(Car.class));
        verify(postService).create(any(Post.class));
        verify(postPhotoService, atLeastOnce()).create(any(PostPhoto.class));
    }

    /**
     * Тест создания объявления без авторизации.
     */
    @Test
    void createPostWithoutAuthenticationShouldRedirectToLogin() {
        MockHttpSession session = new MockHttpSession();

        PostCreationDto dto = createTestPostCreationDto();

        String redirectUrl = postController.createPost(dto, session);

        assertThat(redirectUrl).isEqualTo("redirect:/auth/login");

        verify(carService, never()).create(any(Car.class));
        verify(postService, never()).create(any(Post.class));
    }

    /**
     * Тест создания объявления с ошибкой.
     */
    @Test
    void createPostWithExceptionShouldRedirectWithError() {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", testUser);

        PostCreationDto dto = createTestPostCreationDto();

        when(brandService.findById(1)).thenThrow(new RuntimeException("Service error"));

        String redirectUrl = postController.createPost(dto, session);

        assertThat(redirectUrl).isEqualTo("redirect:/post/createPost?error=true");

        verify(carService, never()).create(any(Car.class));
        verify(postService, never()).create(any(Post.class));
    }

    /**
     * Тест создания автомобиля из DTO.
     */
    @Test
    void createCarFromDtoShouldReturnCarWithCorrectProperties() {
        PostCreationDto dto = createTestPostCreationDto();

        Brand brand = testUtils.createTestBrand("Toyota");
        CarModel model = testUtils.createTestModel("Camry");
        Category category = testUtils.createTestCategory("Легковой");
        Body body = testUtils.createTestBody("Седан");
        Engine engine = testUtils.createTestEngine("V6");
        TransmissionType transmissionType = testUtils.createTestTransmissionType("Автомат");
        DriveType driveType = testUtils.createTestDriveType("Передний");
        CarColor carColor = testUtils.createTestCarColor("Красный");
        FuelType fuelType = testUtils.createTestFuelType("Бензин");
        WheelSide wheelSide = testUtils.createTestWheelSide("Левый");

        when(brandService.findById(1)).thenReturn(java.util.Optional.of(brand));
        when(carModelService.findById(1)).thenReturn(java.util.Optional.of(model));
        when(categoryService.findById(1)).thenReturn(java.util.Optional.of(category));
        when(bodyService.findById(1)).thenReturn(java.util.Optional.of(body));
        when(engineService.findById(1)).thenReturn(java.util.Optional.of(engine));
        when(transmissionTypeService.findById(1)).thenReturn(java.util.Optional.of(transmissionType));
        when(driveTypeService.findById(1)).thenReturn(java.util.Optional.of(driveType));
        when(carColorService.findById(1)).thenReturn(java.util.Optional.of(carColor));
        when(fuelTypeService.findById(1)).thenReturn(java.util.Optional.of(fuelType));
        when(wheelSideService.findById(1)).thenReturn(java.util.Optional.of(wheelSide));

        Car car = postController.createCarFromDto(dto);

        assertThat(car.getVin()).isEqualTo("VIN12345678901234");
        assertThat(car.getMileage()).isEqualTo(10000L);
        assertThat(car.getYearOfManufacture()).isEqualTo(2020L);
        assertThat(car.getCountOwners()).isEqualTo(1L);
        assertThat(car.getBrand()).isEqualTo(brand);
        assertThat(car.getModel()).isEqualTo(model);
        assertThat(car.getCategory()).isEqualTo(category);
        assertThat(car.getBody()).isEqualTo(body);
        assertThat(car.getEngine()).isEqualTo(engine);
        assertThat(car.getTransmissionType()).isEqualTo(transmissionType);
        assertThat(car.getDriveType()).isEqualTo(driveType);
        assertThat(car.getCarColor()).isEqualTo(carColor);
        assertThat(car.getFuelType()).isEqualTo(fuelType);
        assertThat(car.getWheelSide()).isEqualTo(wheelSide);
    }

    /**
     * Тест создания объявления из DTO.
     */
    @Test
    void createPostFromDtoShouldReturnPostWithCorrectProperties() {
        PostCreationDto dto = createTestPostCreationDto();
        Car car = testUtils.createTestCar("VIN12345678901234");
        User user = testUser;

        Post post = postController.createPostFromDto(dto, car, user);

        assertThat(post.getStatus()).isEqualTo("active");
        assertThat(post.getDescription()).isEqualTo("Test description");
        assertThat(post.getPrice()).isEqualByComparingTo(new BigDecimal("1000000.00"));
        assertThat(post.getCar()).isEqualTo(car);
        assertThat(post.getUser()).isEqualTo(user);
        assertThat(post.getCreatedAt()).isNotNull();
    }

    /**
     * Тест сохранения фотографий.
     */
    @Test
    void savePhotosShouldCreateFilesAndDatabaseRecords() throws IOException {
        Post post = testUtils.createTestPost(testUser,
                testUtils.createTestCar("VIN12345678901234"),
                "active",
                new BigDecimal("1000000.00"));

        List<MultipartFile> photos = createTestPhotos();

        postController.savePhotos(photos, post);

        Path uploadPath = Paths.get("uploads/images");
        assertThat(Files.exists(uploadPath)).isTrue();

        verify(postPhotoService, times(2)).create(any(PostPhoto.class));
    }

    /**
     * Тест сохранения пустого списка фотографий.
     */
    @Test
    void savePhotosWithEmptyListShouldDoNothing() throws IOException {
        Post post = testUtils.createTestPost(testUser,
                testUtils.createTestCar("VIN12345678901234"),
                "active",
                new BigDecimal("1000000.00"));

        postController.savePhotos(List.of(), post);

        verify(postPhotoService, never()).create(any(PostPhoto.class));
    }

    /**
     * Тест сохранения null списка фотографий.
     */
    @Test
    void savePhotosWithNullListShouldDoNothing() throws IOException {
        Post post = testUtils.createTestPost(testUser,
                testUtils.createTestCar("VIN12345678901234"),
                "active",
                new BigDecimal("1000000.00"));

        postController.savePhotos(null, post);

        verify(postPhotoService, never()).create(any(PostPhoto.class));
    }

    private PostCreationDto createTestPostCreationDto() {
        PostCreationDto dto = new PostCreationDto();
        dto.setVin("VIN12345678901234");
        dto.setMileage(10000L);
        dto.setYearOfManufacture(2020L);
        dto.setCountOwners(1L);
        dto.setDescription("Test description");
        dto.setPrice(new BigDecimal("1000000.00"));
        dto.setBrandId(1L);
        dto.setModelId(1L);
        dto.setCategoryId(1L);
        dto.setBodyId(1L);
        dto.setEngineId(1L);
        dto.setTransmissionTypeId(1L);
        dto.setDriveTypeId(1L);
        dto.setCarColorId(1L);
        dto.setFuelTypeId(1L);
        dto.setWheelSideId(1L);
        return dto;
    }

    private List<MultipartFile> createTestPhotos() {
        MockMultipartFile photo1 = new MockMultipartFile(
                "photos",
                "car1.jpg",
                "image/jpeg",
                "test image content 1".getBytes()
        );

        MockMultipartFile photo2 = new MockMultipartFile(
                "photos",
                "car2.jpg",
                "image/jpeg",
                "test image content 2".getBytes()
        );

        return new CopyOnWriteArrayList<>(List.of(photo1, photo2));
    }
}
