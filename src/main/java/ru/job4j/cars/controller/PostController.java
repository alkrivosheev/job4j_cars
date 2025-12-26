package ru.job4j.cars.controller;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.config.TomcatMultipartCustomizer;
import ru.job4j.cars.dto.PostCreationDto;
import ru.job4j.cars.model.*;
import ru.job4j.cars.service.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Контроллер для управления объявлениями (постами).
 * Обрабатывает запросы, связанные с созданием, просмотром и обновлением объявлений.
 */
@Controller
@RequestMapping("/post")
@AllArgsConstructor
public class PostController {
    private static final Logger log = LoggerFactory.getLogger(TomcatMultipartCustomizer.class);

    private final PostService postService;
    private final CarService carService;
    private final BrandService brandService;
    private final CarModelService carModelService;
    private final CategoryService categoryService;
    private final BodyService bodyService;
    private final EngineService engineService;
    private final TransmissionTypeService transmissionTypeService;
    private final DriveTypeService driveTypeService;
    private final CarColorService carColorService;
    private final FuelTypeService fuelTypeService;
    private final WheelSideService wheelSideService;
    private final PostPhotoService postPhotoService;

    private final String uploadDir = "uploads/images";

    /**
     * Отображает форму для создания нового объявления.
     * Добавляет в модель все необходимые справочники для выбора характеристик автомобиля.
     *
     * @param model объект Model для передачи данных в представление
     * @return имя шаблона формы создания объявления
     */
    @GetMapping("/createPost")
    public String showCreateForm(Model model) {
        model.addAttribute("brands", brandService.findAllOrderById());
        model.addAttribute("models", carModelService.findAllOrderById());
        model.addAttribute("categories", categoryService.findAllOrderById());
        model.addAttribute("bodies", bodyService.findAllOrderById());
        model.addAttribute("engines", engineService.findAllOrderById());
        model.addAttribute("transmissionTypes", transmissionTypeService.findAllOrderById());
        model.addAttribute("driveTypes", driveTypeService.findAllOrderById());
        model.addAttribute("carColors", carColorService.findAllOrderById());
        model.addAttribute("fuelTypes", fuelTypeService.findAllOrderById());
        model.addAttribute("wheelSides", wheelSideService.findAllOrderById());

        return "post/createPost";
    }

    /**
     * Обрабатывает отправку формы создания объявления.
     * Создает автомобиль и объявление на основе полученных данных из DTO.
     * Сохраняет загруженные фотографии и привязывает их к объявлению.
     *
//     * @param postCreationDto DTO с данными для создания объявления
     * @param session объект HttpSession для получения текущего пользователя
     * @return перенаправление на главную страницу в случае успеха или обратно к форме с ошибкой
     */
    @PostMapping("/createPost")
    public String createPost(@ModelAttribute PostCreationDto postCreationDto,
                             HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        try {
            Car car = createCarFromDto(postCreationDto);
            Car savedCar = carService.create(car);

            Post post = createPostFromDto(postCreationDto, savedCar, currentUser);
            Post savedPost = postService.create(post);

            savePhotos(postCreationDto.getPhotos(), savedPost);

            return "redirect:/";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/post/createPost?error=true";
        }
    }


    /**
     * Создает объект Car на основе данных из DTO.
     *
     * @param dto DTO с данными автомобиля
     * @return созданный объект Car
     */
    public Car createCarFromDto(PostCreationDto dto) {
        Car car = new Car();
        car.setVin(dto.getVin());
        car.setMileage(dto.getMileage());
        car.setYearOfManufacture(dto.getYearOfManufacture());
        car.setCountOwners(dto.getCountOwners());
        car.setBrand(brandService.findById(dto.getBrandId().intValue()).orElseThrow());
        car.setModel(carModelService.findById(dto.getModelId().intValue()).orElseThrow());
        car.setCategory(categoryService.findById(dto.getCategoryId().intValue()).orElseThrow());
        car.setBody(bodyService.findById(dto.getBodyId().intValue()).orElseThrow());
        car.setEngine(engineService.findById(dto.getEngineId().intValue()).orElseThrow());
        car.setTransmissionType(transmissionTypeService.findById(dto.getTransmissionTypeId().intValue()).orElseThrow());
        car.setDriveType(driveTypeService.findById(dto.getDriveTypeId().intValue()).orElseThrow());
        car.setCarColor(carColorService.findById(dto.getCarColorId().intValue()).orElseThrow());
        car.setFuelType(fuelTypeService.findById(dto.getFuelTypeId().intValue()).orElseThrow());
        car.setWheelSide(wheelSideService.findById(dto.getWheelSideId().intValue()).orElseThrow());

        return car;
    }

    /**
     * Создает объект Post на основе данных из DTO.
     *
     * @param dto DTO с данными объявления
     * @param car созданный автомобиль
     * @param user текущий пользователь
     * @return созданный объект Post
     */
    public Post createPostFromDto(PostCreationDto dto, Car car, User user) {
        Post post = new Post();
        post.setStatus("active");
        post.setDescription(dto.getDescription());
        post.setPrice(dto.getPrice());
        post.setCreatedAt(LocalDateTime.now());
        post.setCar(car);
        post.setUser(user);

        return post;
    }

    /**
     * Сохраняет загруженные фотографии на диск и создает записи в базе данных.
     *
     * @param photos список загруженных файлов
     * @param post объявление, к которому привязываются фотографии
     * @throws IOException если возникает ошибка при сохранении файлов
     */
    public void savePhotos(List<MultipartFile> photos, Post post) throws IOException {
        if (photos == null || photos.isEmpty()) {
            return;
        }

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        for (MultipartFile photo : photos) {
            if (!photo.isEmpty()) {
                String filename = UUID.randomUUID() + "_" + photo.getOriginalFilename();
                Path filePath = uploadPath.resolve(filename);
                Files.copy(photo.getInputStream(), filePath);

                PostPhoto postPhoto = new PostPhoto();
                postPhoto.setPhotoPath(filename);
                postPhoto.setPost(post);
                postPhotoService.create(postPhoto);
            }
        }
    }
}