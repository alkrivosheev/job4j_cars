package ru.job4j.cars.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO для передачи данных из формы создания объявления.
 * Содержит данные поста, автомобиля и фотографий.
 */
@Data
public class PostCreationDto {

    private String vin;
    private Long mileage;
    private Long yearOfManufacture;
    private Long countOwners;

    private Long brandId;
    private Long modelId;
    private Long categoryId;
    private Long bodyId;
    private Long engineId;
    private Long transmissionTypeId;
    private Long driveTypeId;
    private Long carColorId;
    private Long fuelTypeId;
    private Long wheelSideId;

    private String description;
    private BigDecimal price;

    private List<MultipartFile> photos;
}